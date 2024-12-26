package cn.academy.energy.client.ui

import cn.academy.block.container.ContainerMatrix
import cn.academy.block.tileentity.TileMatrix
import cn.academy.core.client.ui._
import cn.academy.energy.api.WirelessHelper
import cn.academy.event.energy.{ChangePassEvent, CreateNetworkEvent}
import cn.lambdalib2.cgui.component.TextBox
import cn.lambdalib2.registry.StateEventCallback
import cn.lambdalib2.s11n.SerializeStrategy.ExposeStrategy
import cn.lambdalib2.s11n.network.NetworkMessage.Listener
import cn.lambdalib2.s11n.network.{Future, NetworkMessage, NetworkS11n, NetworkS11nType}
import cn.lambdalib2.s11n.{SerializeNullable, SerializeStrategy}
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.relauncher.Side

object GuiMatrix2 {

  import MatrixNetProxy._

  def apply(container: ContainerMatrix): ContainerUI = {
    val tile = container.tile
    val thePlayer = Minecraft.getMinecraft.player
    val isPlacer = tile.getPlacerName == thePlayer.getName

    val invPage = InventoryPage.apply("matrix")

    val ret = new ContainerUI(container, invPage)

    {
      def rebuildInfo(data: InitData): Unit = {
        ret.infoPage.reset()

        ret.infoPage.histogram(
          HistUtils.histCapacity(data.load, tile.getCapacity)
        )

        ret.infoPage.sepline("info")
          .property("owner", tile.getPlacerName, null, false, true, null)
          .property("range", "%.0f".format(tile.getRange), null, false, true, null)
          .property("bandwidth", tile.getBandwidth + " IF/T", null, false, true, null)

        if (data.init) {
          ret.infoPage.sepline("wireless_info")
          if (isPlacer) {
            ret.infoPage
              .property("ssid", data.ssid,
                new java.util.function.Function[String, Void] {
                  override def apply(newSSID: String): Void = {
                    send(MSG_CHANGE_SSID, tile, thePlayer, newSSID)
                    null
                  }
                }, false, true, null
              )
              .sepline("change_pass")
              .property("password", data.pass,
                new java.util.function.Function[String, Void] {
                  override def apply(newPass: String): Void = {
                    send(MSG_CHANGE_PASSWORD, tile, thePlayer, newPass)
                    null
                  }
                }, true, true, null
              )
          } else {
            ret.infoPage
              .property("ssid", data.ssid, null, false, true, null)
              .property("password", data.pass, null, true, true, null)
          }
        } else {
          val ssidCell = Array[TextBox](null)
          val passwordCell = Array[TextBox](null)

          if (isPlacer) {
            ret.infoPage
              .sepline("wireless_init")
              .property("ssid", "", new java.util.function.Function[String, Void] {
                override def apply(newSSID: String): Void = {
                  null
                }
              }, false, false, ssidCell)
              .property("password", "", new java.util.function.Function[String, Void] {
                override def apply(newPass: String): Void = {
                  null
                }
              }, true, false, passwordCell)
              .blank(1)
              .button("INIT", new Runnable {
                override def run(): Unit = {
                  val ssidBox = ssidCell(0)
                  val passBox = passwordCell(0)
                  send(MSG_INIT, tile, ssidBox.content, passBox.content, Future.create2((_: Boolean) =>
                    send(MSG_GATHER_INFO, tile, Future.create2((inf: InitData) => rebuildInfo(inf)))
                  ))
                }
              })
          } else {
            ret.infoPage.sepline("wireless_noinit")
          }
        }
      }

      send(MSG_GATHER_INFO, tile, Future.create2((inf: InitData) => rebuildInfo(inf)))
    }

    ret
  }

  private def send(channel: String, args: Any*): Unit = {
    NetworkMessage.sendToServer(MatrixNetProxy, channel, args.map(_.asInstanceOf[AnyRef]): _*)
  }
}

@NetworkS11nType
@SerializeStrategy(strategy = ExposeStrategy.ALL)
private class InitData {
  @SerializeNullable
  var ssid: String = null
  @SerializeNullable
  var pass: String = null
  var load: Int = 0

  def init: Boolean = ssid != null
}

@NetworkS11nType
private object MatrixNetProxy {

  @StateEventCallback
  def __init(ev: FMLInitializationEvent): Unit = {
    NetworkS11n.addDirectInstance(MatrixNetProxy)
  }

  final val MSG_GATHER_INFO = "gather"
  final val MSG_INIT = "init"
  final val MSG_CHANGE_PASSWORD = "pass"
  final val MSG_CHANGE_SSID = "ssid"

  @Listener(channel = MSG_GATHER_INFO, side = Array(Side.SERVER))
  def gatherInfo(matrix: TileMatrix, future: Future[InitData]): Unit = {
    val optNetwork = Option(WirelessHelper.getWirelessNet(matrix))
    val result = new InitData
    optNetwork match {
      case Some(net) =>
        result.ssid = net.getSSID
        result.pass = net.getPassword
        result.load = net.getLoad
      case _ =>
    }

    future.sendResult(result)
  }

  @Listener(channel = MSG_INIT, side = Array(Side.SERVER))
  def init(matrix: TileMatrix, ssid: String, pwd: String, fut: Future[Boolean]): Unit = {
    MinecraftForge.EVENT_BUS.post(new CreateNetworkEvent(matrix, ssid, pwd))

    fut.sendResult(true)
  }

  @Listener(channel = MSG_CHANGE_PASSWORD, side = Array(Side.SERVER))
  def changePassword(matrix: TileMatrix, player: EntityPlayer, pwd: String): AnyVal = {
    if (matrix.getPlacerName == player.getName) {
      MinecraftForge.EVENT_BUS.post(new ChangePassEvent(matrix, pwd))
    }
  }

  @Listener(channel = MSG_CHANGE_SSID, side = Array(Side.SERVER))
  def changeSSID(matrix: TileMatrix, player: EntityPlayer, newSSID: String): Unit = {
    if (matrix.getPlacerName == player.getName) {
      Option(WirelessHelper.getWirelessNet(matrix)) match {
        case Some(net) =>
          net.setSSID(newSSID)
        case _ =>
      }
    }
  }
}