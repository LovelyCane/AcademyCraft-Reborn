package cn.academy.internal.ability.vanilla.teleporter.skill;

import cn.academy.internal.ability.AbilityContext;
import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.context.ClientRuntime;
import cn.academy.internal.ability.context.KeyDelegate;
import cn.academy.internal.ability.vanilla.teleporter.util.TPSkillHelper;
import cn.academy.internal.datapart.AbilityData;
import cn.lambdalib2.util.EntitySelectors;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.WorldUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LocationTeleport extends Skill {
    public static final LocationTeleport INSTANCE = new LocationTeleport();
    public static final Predicate<Entity> teleportSelector = EntitySelectors.living().and(t -> t.width * t.width * t.height < 80f);

    public LocationTeleport() {
        super("location_teleport", 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void activate(ClientRuntime rt, int keyID) {
        rt.addKey(keyID, new KeyDelegate() {
            @Override
            public ResourceLocation getIcon() {
                return getHintIcon();
            }

            @Override
            public int createID() {
                return 0;
            }

            @Override
            public Skill getSkill() {
                return LocationTeleport.this;
            }

            @Override
            public void onKeyDown() {
                getMC().displayGuiScreen(new Gui());
            }
        });
    }

    public static boolean canCrossDimension(EntityPlayer player) {
        return AbilityData.get(player).getSkillExp(INSTANCE) > 0.8f;
    }

    /**
     * @return (Overload, CP) consumption
     */
    public static float[] getConsumption(EntityPlayer player, Location dest) {
        AbilityData data = AbilityData.get(player);
        double distance = player.getDistance(dest.x, dest.y, dest.z);
        int dimPenalty = isCrossDim(player, dest) ? 2 : 1;

        float overload = 240;
        float cp = MathUtils.lerpf(200, 150, data.getSkillExp(INSTANCE)) * dimPenalty *
                Math.max(8.0f, MathHelper.sqrt(Math.min(800, distance)));

        return new float[]{overload, cp};
    }

    /**
     * @return `null` if can perform. `Some(reason)` if can't.
     */
    public static String getPerformStat(EntityPlayer player, Location dest) {
        if (isCrossDim(player, dest) && !canCrossDimension(player)) {
            return I18n.format("ac.gui.loctele.err_exp");
        } else {
            AbilityContext ctx = AbilityContext.of(player, INSTANCE);
            float[] consumption = getConsumption(player, dest);
            float cp = consumption[1];
            if (ctx.canConsumeCP(cp)) {
                return null;
            } else {
                return I18n.format("ac.gui.loctele.err_cp");
            }
        }
    }

    public static void perform(EntityPlayer player, Location dest) {
        AbilityContext ctx = AbilityContext.of(player, INSTANCE);

        float[] consumption = getConsumption(player, dest);
        float overload = consumption[0];
        float cp = consumption[1];
        ctx.consumeWithForce(overload, cp);

        List<Entity> entitiesToTeleport = new ArrayList<>(WorldUtils.getEntities(player, 5, teleportSelector.and(EntitySelectors.exclude(player))));
        entitiesToTeleport.add(player);

        if (isCrossDim(player, dest)) {
            entitiesToTeleport.forEach(e -> e.changeDimension(dest.dim));
        }

        double dist = player.getDistance(dest.x, dest.y, dest.z);
        float expincr = dist >= 200 ? 0.03f : 0.015f;
        double px = player.posX;
        double py = player.posY;
        double pz = player.posZ;
        for (Entity e : entitiesToTeleport) {
            double dx = e.posX - px;
            double dy = e.posY - py;
            double dz = e.posZ - pz;
            if (e.isRiding()) e.dismountRidingEntity();
            e.setPositionAndUpdate(dest.x + dx, dest.y + dy, dest.z + dz);
        }

        ctx.addSkillExp(expincr);
        ctx.setCooldown((int) MathUtils.lerpf(30, 20, ctx.getSkillExp()));

        TPSkillHelper.incrTPCount(player);
    }

    private static boolean isCrossDim(EntityPlayer player, Location dest) {
        return player.world.provider.getDimension() != dest.dim;
    }
}