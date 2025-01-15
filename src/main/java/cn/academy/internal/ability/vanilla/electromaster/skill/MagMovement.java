package cn.academy.internal.ability.vanilla.electromaster.skill;

import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.ability.vanilla.electromaster.CatElectromaster;
import cn.academy.internal.datapart.AbilityData;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagMovement extends Skill {
    public static final MagMovement INSTANCE = new MagMovement();
    public static final double ACCEL = 0.08d;
    public static final String SOUND = "em.move_loop";

    public MagMovement() {
        super("mag_movement", 2);
    }

    public static int getMaxDistance(AbilityData data) {
        // maybe? it's need to verify in anime,manga and novel.
        return (int) (data.getLevel() * 5 + data.getSkillExp(MagMovement.INSTANCE) * 10);
    }

    public static Target toTarget(AbilityData aData, World world, RayTraceResult pos) {
        if (pos.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (aData.getSkillExp(MagMovement.INSTANCE) < 0.6f && !CatElectromaster.isMetalBlock(world.getBlockState(pos.getBlockPos()).getBlock())) {
                return null;
            }
            if (!CatElectromaster.isMetalBlock(world.getBlockState(pos.getBlockPos()).getBlock())) {
                return null;
            }
            return new PointTarget(pos.hitVec.x, pos.hitVec.y, pos.hitVec.z);
        } else {
            if (pos.entityHit != null && CatElectromaster.isEntityMetallic(pos.entityHit)) {
                return new EntityTarget(pos.entityHit);
            }
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyid) {
        activateSingleKey(rt, keyid, MovementContext::new);
    }
}