package cn.academy.entity;

import cn.academy.AcademyCraft;
import cn.academy.internel.render.util.ArcFactory;
import cn.academy.internel.render.util.SubArcHandler;
import cn.lambdalib2.util.MathUtils;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.VecUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author WeAthFolD
 *
 */
@SideOnly(Side.CLIENT)
public class EntityRailgunFX extends EntityRayBase {

    static final int ARC_SIZE = 15;

    static ArcFactory.Arc[] templates;
    static {

        ArcFactory factory = new ArcFactory();
        factory.widthShrink = 0.9;
        factory.maxOffset = 0.8;
        factory.passes = 3;
        factory.width = 0.3;
        factory.branchFactor = 0.7;

        templates = new ArcFactory.Arc[ARC_SIZE];
        for(int i = 0; i < ARC_SIZE; ++i) {
            templates[i] = factory.generate(RandUtils.ranged(2, 3));
        }
    }

    SubArcHandler arcHandler = new SubArcHandler(templates);

    public EntityRailgunFX(EntityPlayer player, double length) {
        super(player);
        posX=player.posX;
        posY=player.posY + player.getEyeHeight();
        posZ=player.posZ;

        this.rotationYaw = player.rotationYaw;
        this.rotationPitch = player.rotationPitch;

        this.life = 50;
        this.blendInTime = 150;
        this.widthShrinkTime = 800;
        this.widthWiggleRadius = 0.3;
        this.maxWiggleSpeed = 0.8;
        this.blendOutTime = 1000;
        this.length = length;

        ignoreFrustumCheck = true;

        //Build the arc list
        {
            double cur = 1.0;
            double len = this.length;
            AcademyCraft.log.info("Length: " + len);
            while(cur <= len) {
                float theta = RandUtils.rangef(0, MathUtils.PI_F * 2);
                double r = RandUtils.ranged(0.1, 0.25);
                Vec3d vec = new Vec3d(cur, r * MathHelper.sin(theta), r * MathHelper.cos(theta));
                VecUtils.rotateAroundZ(vec, rotationPitch * MathUtils.PI_F / 180);
                vec.rotateYaw((rotationYaw) * MathUtils.PI_F / 180);
                arcHandler.generateAt(vec);

                cur += RandUtils.ranged(1, 2);
            }
            AcademyCraft.log.info("DEBUG000");
        }
    }

    @Override
    protected void onFirstUpdate() {
        super.onFirstUpdate();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(ticksExisted == 30)
            arcHandler.clear();

        arcHandler.tick();
    }
}