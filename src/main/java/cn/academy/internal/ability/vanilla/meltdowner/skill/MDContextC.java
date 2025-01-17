package cn.academy.internal.ability.vanilla.meltdowner.skill;

import cn.academy.api.ability.AbilityAPIExt;
import cn.academy.internal.ability.context.ClientContext;
import cn.academy.internal.ability.context.RegClientContext;
import cn.academy.internal.entity.EntityMDRay;
import cn.academy.internal.client.renderer.particle.MdParticleFactory;
import cn.academy.internal.client.renderer.util.ACRenderingHelper;
import cn.academy.internal.sound.ACSounds;
import cn.academy.internal.sound.FollowEntitySound;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static cn.academy.internal.ability.vanilla.meltdowner.skill.MDContext.MSG_PERFORM;
import static cn.academy.internal.ability.vanilla.meltdowner.skill.MDContext.MSG_REFLECTED;

@SideOnly(Side.CLIENT)
@RegClientContext(MDContext.class)
@SuppressWarnings("unused")
public class MDContextC extends ClientContext {
    private int ticks = 0;
    private FollowEntitySound sound;

    public MDContextC(MDContext par) {
        super(par);
    }

    @Listener(channel = MSG_PERFORM, side = Side.CLIENT)
    private void c_perform(int ct, double length) {
        EntityMDRay ray = new EntityMDRay(ctx.player, length);
        ACSounds.playClient(ctx.player, "md.meltdowner", SoundCategory.PLAYERS, 0.5f);
        world().spawnEntity(ray);
    }

    @Listener(channel = MSG_REFLECTED, side = Side.CLIENT)
    private void c_reflected(Entity reflector) {
        Vec3d playerLook = ctx.player.getLookVec().normalize();
        double distance = VecUtils.entityHeadPos(ctx.player).distanceTo(VecUtils.entityHeadPos(reflector));
        Vec3d spawnPos = VecUtils.add(VecUtils.entityHeadPos(ctx.player), VecUtils.multiply(playerLook, distance));
        EntityMDRay ray = new EntityMDRay(ctx.player, 10);
        ray.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
        world().spawnEntity(ray);
    }

    @Listener(channel = AbilityAPIExt.MSG_TERMINATED, side = Side.CLIENT)
    private void c_terminate() {
        if (isLocal())
            ctx.player.capabilities.setPlayerWalkSpeed(0.1f);
        sound.stop();
    }

    @Listener(channel = AbilityAPIExt.MSG_MADEALIVE, side = Side.CLIENT)
    private void c_start() {
        sound = new FollowEntitySound(ctx.player, "md.md_charge", SoundCategory.AMBIENT).setVolume(1.0f);
        ACSounds.playClient(sound);
    }

    @Listener(channel = AbilityAPIExt.MSG_TICK, side = Side.CLIENT)
    private void c_tick() {
        ticks++;
        if (isLocal())
            ctx.player.capabilities.setPlayerWalkSpeed(0.1f - ticks * 0.001f);

        // Particles surrounding player
        for (int count = 2; count >= 0; count--) {
            double r = MathHelper.nextDouble(world().rand, 0.7, 1);
            double theta = MathHelper.nextDouble(world().rand, 0, Math.PI * 2);
            double h = MathHelper.nextDouble(world().rand, -1.2, 0);
            Vec3d pos = VecUtils.add(new Vec3d(ctx.player.posX, ctx.player.posY + (ACRenderingHelper.isThePlayer(ctx.player) ? 0 : 1.6), ctx.player.posZ), new Vec3d(r * Math.sin(theta), h, r * Math.cos(theta)));
            Vec3d vel = new Vec3d(MathHelper.nextDouble(world().rand, -0.03, 0.03), MathHelper.nextDouble(world().rand, 0.01, 0.05), MathHelper.nextDouble(world().rand, -0.03, 0.03));
            world().spawnEntity(MdParticleFactory.INSTANCE.next(world(), pos, vel));
        }
    }
}
