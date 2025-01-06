package cn.academy.ability.vanilla.meltdowner.skill;

import cn.academy.ability.Skill;
import net.minecraft.util.ResourceLocation;

public abstract class MineRaysBase extends Skill {
    private ResourceLocation particleTexture;
    private final String postfix;

    public MineRaysBase(String postfix, int atLevel) {
        super("mine_ray_" + postfix, atLevel);
        this.postfix = postfix;
    }

    public String getPostfix() {
        return postfix;
    }

    public ResourceLocation getParticleTexture() {
        return particleTexture;
    }

    public void setParticleTexture(ResourceLocation particleTexture) {
        this.particleTexture = particleTexture;
    }
}