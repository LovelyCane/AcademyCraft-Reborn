package cn.academy.internal.ability.vanilla.vecmanip.client.effect;

import cn.academy.internal.ability.context.Context.Status;
import cn.academy.internal.ability.vanilla.vecmanip.skill.VecAccelContext;
import cn.academy.internal.entity.LocalEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParabolaEffect extends LocalEntity {
    public VecAccelContext ctx;
    public boolean canPerform;

    public ParabolaEffect(VecAccelContext ctx) {
        super(ctx.player.getEntityWorld());
        this.ctx = ctx;
        this.setPosition(ctx.player.posX, ctx.player.posY, ctx.player.posZ);
        this.ignoreFrustumCheck = true;
    }

    @Override
    public void onUpdate() {
        this.setPosition(ctx.player.posX, ctx.player.posY, ctx.player.posZ);
        this.canPerform = ctx.canPerform;
        if (ctx.getStatus() == Status.TERMINATED) {
            setDead();
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }
}