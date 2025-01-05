package cn.academy.ability.vanilla.teleporter.skill

import cn.academy.ability.Skill
import cn.academy.ability.api.AbilityAPIExt
import cn.academy.ability.context.{ClientContext, ClientRuntime, Context, RegClientContext}
import cn.academy.ability.vanilla.teleporter.util.TPSkillHelper
import cn.academy.datapart.CPData
import cn.academy.entity.EntityTPMarking
import cn.academy.internel.sound.ACSounds
import cn.lambdalib2.s11n.network.NetworkMessage.Listener
import cn.lambdalib2.util.{Raytrace, VecUtils}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.{BlockPos, RayTraceResult, Vec3d}
import net.minecraft.util.{EnumFacing, SoundCategory}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 * @author WeAthFolD, KSkun
 */

object MarkTeleport extends Skill("mark_teleport", 2) {
  @SideOnly(Side.CLIENT)
  override def activate(rt: ClientRuntime, keyID: Int): Unit = activateSingleKey(rt, keyID, p => new MTContext(p))
}

object MTContext {
  final val MSG_EXECUTE = "execute"
  final val MSG_SOUND = "sound"
}

import cn.academy.ability.vanilla.teleporter.skill.MTContext._
import cn.lambdalib2.util.MathUtils._

class MTContext(p: EntityPlayer) extends Context(p, MarkTeleport) {
  private val MINIMUM_VALID_DISTANCE: Double = 3.0

  private var ticks: Int = 0
  private val exp: Float = ctx.getSkillExp

  @Listener(channel = AbilityAPIExt.MSG_KEYUP, side = Array(Side.CLIENT))
  private def l_onKeyUp(): Unit = sendToServer(MSG_EXECUTE)

  @Listener(channel = AbilityAPIExt.MSG_KEYABORT, side = Array(Side.CLIENT))
  private def l_onKeyAbort(): Unit = terminate()

  @Listener(channel = AbilityAPIExt.MSG_TICK, side = Array(Side.SERVER))
  private def s_tick(): Unit = {
    ticks += 1
  }

  @Listener(channel = MSG_EXECUTE, side = Array(Side.SERVER))
  private def s_execute(): Unit = {
    val dest: Vec3d = getDest(player, ticks)
    val distance: Float = dest.distanceTo(new Vec3d(player.posX, player.posY, player.posZ)).toFloat
    if (distance < MINIMUM_VALID_DISTANCE) {
      // TODO: Play abort sound
    } else {
      sendToClient(MSG_SOUND)
      val overload: Float = lerpf(40, 20, exp)
      ctx.consumeWithForce(overload, distance * getCPB(exp))
      if (player.isRiding)
        player.dismountRidingEntity()
      player.setPositionAndUpdate(dest.x, dest.y, dest.z)
      val expincr: Float = 0.00018f * distance
      ctx.addSkillExp(expincr)
      player.fallDistance = 0
      ctx.setCooldown(lerpf(30, 0, exp).toInt)
      TPSkillHelper.incrTPCount(player)
    }
    terminate()
  }

  private def getMaxDist(exp: Float, cp: Float, ticks: Int): Double = {
    val max: Double = lerpf(25, 60, exp)
    val cplim: Double = cp / getCPB(exp)
    Math.min((ticks + 1) * 2, Math.min(max, cplim))
  }

  /**
   * @return Consumption per block
   */
  private def getCPB(exp: Float): Float = lerpf(12, 4, exp)

  def getDest(player: EntityPlayer, ticks: Int): Vec3d = {
    val cpData: CPData = CPData.get(player)
    val dist: Double = getMaxDist(ctx.getSkillExp, cpData.getCP, ticks)
    val mop: RayTraceResult = Raytrace.traceLiving(player, dist)
    var x: Double = .0
    var y: Double = .0
    var z: Double = .0
    if (mop.typeOfHit != RayTraceResult.Type.MISS) {
      x = mop.hitVec.x
      y = mop.hitVec.y
      z = mop.hitVec.z
      if (mop.typeOfHit == RayTraceResult.Type.BLOCK) {
        mop.sideHit match {
          case EnumFacing.DOWN =>
            y -= 1.0
          case EnumFacing.UP =>
            y += 1.8
          case EnumFacing.NORTH =>
            z -= .6
            y = mop.getBlockPos.getY + 1.7
          case EnumFacing.SOUTH =>
            z += .6
            y = mop.getBlockPos.getY + 1.7
          case EnumFacing.WEST =>
            x -= .6
            y = mop.getBlockPos.getY + 1.7
          case EnumFacing.EAST =>
            x += .6
            y = mop.getBlockPos.getY + 1.7
        }
        // check head
        if (mop.sideHit.getIndex > 1) {
          val hx: Int = x.toInt
          val hy: Int = (y + 1).toInt
          val hz: Int = z.toInt
          if (!player.world.isAirBlock(new BlockPos(hx, hy, hz))) y -= 1.25
        }
      } else y += mop.entityHit.getEyeHeight
    } else {
      val mo = VecUtils.add(player.getPositionEyes(1f), VecUtils.multiply(player.getLookVec, dist))
      x = mo.x
      y = mo.y
      z = mo.z
    }
    new Vec3d(x, y, z)
  }

}

@SideOnly(Side.CLIENT)
@RegClientContext(classOf[MTContext])
class MTContextC(par: MTContext) extends ClientContext(par) {

  private var mark: EntityTPMarking = _
  private var ticks = 0

  @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Array(Side.CLIENT))
  private def l_start() = {
    if (isLocal) {
      mark = new EntityTPMarking(player)
      player.world.spawnEntity(mark)
    }
  }

  @Listener(channel = AbilityAPIExt.MSG_TICK, side = Array(Side.CLIENT))
  private def l_update(): Unit = {
    if (mark == null) terminate()

    ticks += 1
    val dest = par.getDest(player, ticks)
    if (isLocal) mark.setPosition(dest.x, dest.y, dest.z)
  }

  @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Array(Side.CLIENT))
  private def l_end(): Unit = {
    if (isLocal) mark.setDead()
  }

  @Listener(channel = MSG_SOUND, side = Array(Side.CLIENT))
  private def c_sound(): Unit = {
    ACSounds.playClient(player, "tp.tp", SoundCategory.AMBIENT, .5f)
  }
}