package cn.lambdalib2.util.entityx;

import net.minecraft.entity.Entity;

/**
 * @author WeAthFolD
 */
public interface EntityCallback<T extends Entity> {

    void execute(T target);
    
}
