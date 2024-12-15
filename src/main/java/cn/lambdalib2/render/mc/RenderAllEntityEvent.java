package cn.lambdalib2.render.mc;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Fired at the beginning of an entity rendering pass.
 */
public class RenderAllEntityEvent extends Event {

    public final int pass;
    public final float partialTicks;
    public final float deltaTime;

    public RenderAllEntityEvent(int pass, float partialTicks, float deltaTime) {
        this.pass = pass;
        this.partialTicks = partialTicks;
        this.deltaTime = deltaTime;
    }
}