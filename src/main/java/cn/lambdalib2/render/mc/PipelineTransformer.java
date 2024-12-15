package cn.lambdalib2.render.mc;

import cn.lambdalib2.CorePlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class PipelineTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.client.renderer.RenderGlobal")) {
            ClassWriter cw = new ClassWriter(0);
            ClassVisitor cv = new EntityRendererVisitor(cw);
            ClassReader cr = new ClassReader(bytes);
            System.out.println(("Transforming RenderGlobal"));

            cr.accept(cv, 0);

            return cw.toByteArray();
        }

        return bytes;
    }

    private class EntityRendererVisitor extends ClassVisitor {

        public EntityRendererVisitor(ClassVisitor cv) {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature,
                                         String[] exceptions) {
            // net/minecraft/client/renderer/RenderGlobal/renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V
            // buy/a (Lvg;Lbxy;F)V

            MethodVisitor smv = super.visitMethod(access, name, desc, signature, exceptions);

            String testName, testDesc;
            if (CorePlugin.isDeobfEnabled()) {
                testName = "a";
                testDesc = "(Lvg;Lbxy;F)V";
            } else {
                testName = "renderEntities";
                testDesc = "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V";
            }

            if (name.equals(testName) && desc.equals(testDesc)) {
                System.out.println(("Transforming RenderGlobal.renderEntities"));
                return new MethodVisitor(Opcodes.ASM5, smv) {

                    @Override
                    public void visitCode() {
                        super.visitCode();

                        // FLOAD 3
                        // INVOKESTATIC cn/lambdalib/core/RenderEventDispatch beginRenderEntities(f)V;
                        this.visitVarInsn(FLOAD, 3);
                        this.visitMethodInsn(INVOKESTATIC, Type.getInternalName(RenderEventDispatch.class), "beginRenderEntities",
                                Type.getMethodDescriptor(Type.getType(Void.TYPE), Type.getType(float.class)), false);
                    }

                    @Override
                    public void visitInsn(int opcode) {
                        if (opcode == RETURN) {
                            // FLOAD 3
                            // INVOKESTATIC cn/lambdalib/core/RenderEventDispatch endRenderEntities(f)V;
                            this.visitVarInsn(FLOAD, 3);
                            this.visitMethodInsn(INVOKESTATIC, Type.getInternalName(RenderEventDispatch.class), "endRenderEntities",
                                    Type.getMethodDescriptor(Type.getType(Void.TYPE), Type.getType(float.class)), false);
                        }

                        super.visitInsn(opcode);
                    }
                };
            }

            return smv;
        }

    }

}
