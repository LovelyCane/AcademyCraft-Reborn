/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.registry.mc.gui;

//import cn.lambdalib.annoreg.base.RegistrationInstance;
//import cn.lambdalib.annoreg.core.LoadStage;
//import cn.lambdalib.annoreg.core.RegistryTypeDecl;
//import cpw.mods.fml.relauncher.Side;
//import cpw.mods.fml.relauncher.SideOnly;

import cn.lambdalib2.auxgui.AuxGui;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.util.Debug;
import cn.lambdalib2.util.ReflectionUtils;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

/**
 * AuxGui register annotation.
 * @author WeathFolD
 */

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@SideOnly(Side.CLIENT)
public @interface RegAuxGui {}

@SideOnly(Side.CLIENT)
class RegAuxGuiImpl {

//    public RegAuxGui() {
//        super(RegAuxGui.class, "AuxGui");
//        this.setLoadStage(LoadStage.INIT);
//    }

    @StateEventCallback
    @SuppressWarnings("unchecked")
    private static void init(FMLInitializationEvent ev) {
        ReflectionUtils.getFields(RegAuxGui.class).forEach(it -> {
            try {
                AuxGui instance = (AuxGui) it.get(null);
                AuxGui.register(instance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        ReflectionUtils.getClasses(RegAuxGui.class).forEach(type -> {
            try {
                Constructor<AuxGui> ctor = (Constructor<AuxGui>) type.getConstructor();
                AuxGui.register(ctor.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
