package cn.academy.core.client.ui

import cn.academy.Resources
import cn.academy.energy.api.WirelessHelper
import cn.academy.energy.api.block.{IWirelessMatrix, IWirelessNode, IWirelessTile, IWirelessUser}
import cn.academy.energy.impl.NodeConn
import cn.academy.event.energy.{LinkUserEvent, UnlinkUserEvent}
import cn.lambdalib2.cgui.component.TextBox.ConfirmInputEvent
import cn.lambdalib2.cgui.event.{GainFocusEvent, LeftClickEvent, LostFocusEvent}
import cn.lambdalib2.cgui.loader.CGUIDocument
import cn.lambdalib2.registry.StateEventCallback
import cn.lambdalib2.s11n.SerializeStrategy.ExposeStrategy
import cn.lambdalib2.s11n.network.NetworkMessage.Listener
import cn.lambdalib2.s11n.network.{NetworkMessage, NetworkS11n, NetworkS11nType}
import cn.lambdalib2.s11n.{SerializeIncluded, SerializeNullable, SerializeStrategy}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.relauncher.Side

import scala.collection.JavaConversions.asScalaBuffer
//import cn.academy.core.Resources
//import cn.academy.event.node.UnlinkUserEvent
import cn.academy.util.LocalHelper
import cn.lambdalib2.cgui.ScalaCGUI._
import cn.lambdalib2.cgui.Widget
import cn.lambdalib2.cgui.component._
import cn.lambdalib2.cgui.event.FrameEvent
import cn.lambdalib2.render.font.IFont.FontOption
import cn.lambdalib2.s11n.network.Future
import cn.lambdalib2.util._
import net.minecraft.tileentity.TileEntity
import org.lwjgl.opengl.GL11._

object TechUI {
  val local: LocalHelper = LocalHelper.at("ac.gui.common")

  def drawTextBox(content: String, option: FontOption,
                  x: Float, y: Float,
                  limit: Float = Float.MaxValue): Unit = {
    val wmargin = 5
    val hmargin = 2

    val font = Resources.font()
    val extent = font.drawSeperated_Sim(content, limit, option)

    glColor4f(0, 0, 0, 0.5f)
    HudUtils.colorRect(x - extent.width * option.align.lenOffset, y,
      extent.width + wmargin * 2 + 2, extent.height + hmargin * 2)

    glColor4f(1, 1, 1, 0.8f)
    font.drawSeperated(content, x + wmargin, y + hmargin, limit, option)

    glColor4f(1, 1, 1, 1)
  }

  def breathe(widget: Widget): Any = {
    Option(widget.getComponent(classOf[DrawTexture])) match {
      case Some(tex) =>
        widget.listens[FrameEvent](() => {
          tex.color.setAlpha(Colors.f2i(breatheAlpha))
        })
      case _ =>
    }
  }

  /**
   * A global alpha value generator for producing uniform breathing effect.
   */
  private def breatheAlpha = {
    val time = GameTimer.getTime
    val sin = (1 + math.sin(time / 0.8)) * 0.5

    (0.675 + sin * 0.175).toFloat
  }
}


@NetworkS11nType
private class UserResult {
  @SerializeIncluded
  @SerializeNullable
  var linked: NodeData = null
  @SerializeIncluded
  var avail: Array[NodeData] = null
}

@NetworkS11nType
@SerializeStrategy(strategy = ExposeStrategy.ALL)
private class NodeData {
  var x: Int = 0
  var y: Int = 0
  var z: Int = 0
  var encrypted: Boolean = false

  def tile(world: World): Option[TileEntity with IWirelessNode] = world.getTileEntity(new BlockPos(x, y, z)) match {
    case tile: IWirelessNode => Some(tile)
    case _ => None
  }
}

object WirelessPage {
  type TileUser = TileEntity with IWirelessUser
  type TileNode = TileEntity with IWirelessNode
  type TileBase = TileEntity with IWirelessTile
  type TileMatrix = TileEntity with IWirelessMatrix

  private lazy val wirelessPageTemplate = CGUIDocument.read(Resources.getGui("rework/page_wireless")).getWidget("main")

  private val connectedIcon = Resources.getTexture("guis/icons/icon_connected")
  private val unconnectedIcon = Resources.getTexture("guis/icons/icon_unconnected")

  private val local = TechUI.local.subPath("pg_wireless")

  final val MSG_FIND_NODES = "find_nodes"
  final val MSG_USER_CONNECT = "user_connect"
  final val MSG_USER_DISCONNECT = "unlink"

  trait Target {
    def name: String
  }

  private trait AvailTarget extends Target {
    def connect(pass: String): Unit

    def encrypted: Boolean
  }

  private trait LinkedTarget extends Target {
    def disconnect(): Unit
  }

  private class LinkedInfo(var target: Option[LinkedTarget]) extends Component("LinkedInfo")

  private def rebuildPage(window: Widget, linked: Option[LinkedTarget], avail: Seq[AvailTarget]): Unit = {
    val wlist = window.getWidget("panel_wireless/zone_elementlist")
    wlist.removeComponent(classOf[ElementList])

    val elist = new ElementList
    wlist.getWidget("element").transform.doesDraw = false
    val elemTemplate = wlist.getWidget("element").copy()

    {
      val connectElem = window.getWidget("panel_wireless/elem_connected")

      val (icon, name, alpha, tintEnabled) = linked match {
        case Some(target) => (connectedIcon, target.name, 1.0f, true)
        case None => (unconnectedIcon, local.get("not_connected"), 0.6f, false)
      }

      connectElem.child("icon_connect").component[DrawTexture].texture = icon
      connectElem.child("icon_connect").component[DrawTexture].color.setAlpha(Colors.f2i(alpha))
      connectElem.child("icon_connect").component[Tint].enabled = tintEnabled
      connectElem.child("icon_logo").component[DrawTexture].color.setAlpha(Colors.f2i(alpha))
      connectElem.child("text_name").component[TextBox].setContent(name)

      connectElem.child("icon_connect").component[LinkedInfo].target = linked
    }

    avail.foreach(target => {
      val instance = elemTemplate.copy()

      val passBox = instance.getWidget("input_pass")
      val iconKey = instance.getWidget("icon_key")

      def confirm() = {
        val password = passBox.component[TextBox].content
        target.connect(password)

        passBox.component[TextBox].setContent("")
      }

      instance.getWidget("text_name").component[TextBox].setContent(target.name)

      if (target.encrypted) {
        passBox.listens[ConfirmInputEvent](() => confirm())
        passBox.listens[GainFocusEvent](() => iconKey.component[DrawTexture].color.setAlpha(Colors.f2i(1.0f)))
        passBox.listens[LostFocusEvent](() => iconKey.component[DrawTexture].color.setAlpha(Colors.f2i(0.6f)))
      } else {
        Array(passBox, iconKey).foreach(_.transform.doesDraw = false)
      }

      instance.getWidget("icon_connect").listens[LeftClickEvent](() => confirm())

      elist.addWidget(instance)
    })

    wlist :+ elist
  }

  def userPage(user: TileUser): Page = {
    val ret = WirelessPage()

    val world = user.getWorld

    def rebuild(): Unit = {
      def newFuture() = Future.create2((_: Boolean) => rebuild())

      send(MSG_FIND_NODES, user, Future.create2((result: UserResult) => {
        val linked = Option(result.linked).flatMap(_.tile(world)).map(node => new LinkedTarget {
          override def disconnect(): Unit = send(MSG_USER_DISCONNECT, user, newFuture())

          override def name: String = node.getNodeName
        })

        val avail = result.avail.toList.map(a => (a.tile(world), a.encrypted))
          .flatMap {
            case (Some(node), enc) =>
              Some[AvailTarget](new AvailTarget {
                override def connect(pass: String): Unit = send(MSG_USER_CONNECT, user, node, pass, newFuture())

                override def name: String = node.getNodeName

                override def encrypted: Boolean = enc
              })
            case _ => None
          }

        rebuildPage(ret.window, linked, avail)
      }))
    }

    rebuild()

    ret
  }

  private def apply(): Page = {
    val widget = wirelessPageTemplate.copy()

    TechUI.breathe(widget.getWidget("icon_logo"))

    val wirelessPanel = widget.getWidget("panel_wireless")

    val wlist = wirelessPanel.getWidget("zone_elementlist")

    def elist = wlist.getComponent(classOf[ElementList])

    wirelessPanel.getWidget("btn_arrowup").listens[LeftClickEvent](() => elist.progressLast())
    wirelessPanel.getWidget("btn_arrowdown").listens[LeftClickEvent](() => elist.progressNext())

    val connectIcon = widget.child("panel_wireless/elem_connected/icon_connect")
    connectIcon :+ new LinkedInfo(None)
    connectIcon.listens[LeftClickEvent](() => connectIcon.component[LinkedInfo].target match {
      case Some(target) => target.disconnect()
      case _ =>
    })

    new Page("wireless", widget)
  }

  private def send(msg: String, pars: Any*): Unit = {
    NetworkMessage.sendToServer(WirelessNetDelegate, msg, pars.map(_.asInstanceOf[AnyRef]): _*)
  }
}

object WirelessNetDelegate {

  import WirelessPage._

  @StateEventCallback
  def __init(ev: FMLInitializationEvent): Unit = {
    NetworkS11n.addDirectInstance(WirelessNetDelegate)
  }

  @Listener(channel = MSG_FIND_NODES, side = Array(Side.SERVER))
  private def hFindNodes(user: TileUser, fut: Future[UserResult]): Unit = {
    def cvt(conn: NodeConn) = {
      val tile = conn.getNode.asInstanceOf[TileNode]
      val ret = new NodeData
      ret.x = tile.getPos.getX
      ret.y = tile.getPos.getY
      ret.z = tile.getPos.getZ
      ret.encrypted = tile.getPassword.nonEmpty
      ret
    }

    val linked = Option(WirelessHelper.getNodeConn(user))

    val nodes = WirelessHelper.getNodesInRange(user.getWorld, user.getPos)
      .map(WirelessHelper.getNodeConn)
      .filter(!linked.contains(_))

    val data = new UserResult

    data.linked = linked.map(cvt).orNull
    data.avail = nodes.map(cvt).toArray

    fut.sendResult(data)
  }

  @Listener(channel = MSG_USER_CONNECT, side = Array(Side.SERVER))
  private def hUserConnect(user: TileUser,
                           target: TileNode,
                           password: String,
                           fut: Future[Boolean]): Unit = {
    val evt = new LinkUserEvent(user, target, password)
    val result = !MinecraftForge.EVENT_BUS.post(evt)

    fut.sendResult(result)
  }

  @Listener(channel = MSG_USER_DISCONNECT, side = Array(Side.SERVER))
  private def hUserDisconnect(user: TileBase, fut: Future[Boolean]): Unit = {
    val evt = new UnlinkUserEvent(user)
    val result = !MinecraftForge.EVENT_BUS.post(evt)

    fut.sendResult(result)
  }
}