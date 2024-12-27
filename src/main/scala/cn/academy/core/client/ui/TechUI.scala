package cn.academy.core.client.ui

import cn.academy.Resources
import cn.academy.util.LocalHelper
import cn.lambdalib2.cgui.ScalaCGUI._
import cn.lambdalib2.cgui.Widget
import cn.lambdalib2.cgui.component._
import cn.lambdalib2.cgui.event.FrameEvent
import cn.lambdalib2.render.font.IFont.FontOption
import cn.lambdalib2.util._
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