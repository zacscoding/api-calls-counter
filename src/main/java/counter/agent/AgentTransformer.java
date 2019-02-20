package counter.agent;

import counter.agent.asm.ASM;
import counter.agent.asm.AgentClassWriter;
import counter.agent.asm.SpringRequestMappingASM;
import counter.util.ASMUtil;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class AgentTransformer implements ClassFileTransformer {

    protected static List<ASM> asms = new ArrayList<ASM>();
    public static ThreadLocal<ClassLoader> hookingContext = new ThreadLocal<ClassLoader>();

    static {
        reload();
    }

    public static void reload() {
        List<ASM> temps = new ArrayList<ASM>();
        temps.add(new SpringRequestMappingASM());
        asms = temps;
    }


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        try {
            hookingContext.set(loader);
            if (className == null) {
                return null;
            }

            // adds class descriptions
            final ClassDesc classDesc = new ClassDesc();
            ClassReader cr = new ClassReader(classfileBuffer);
            cr.accept(new ClassVisitor(Opcodes.ASM5) {
                public void visit(int version, int access, String name, String signature, String superName,
                    String[] interfaces) {
                    classDesc.set(version, access, name, signature, superName, interfaces);
                    super.visit(version, access, name, signature, superName, interfaces);
                }

                @Override
                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                    classDesc.annotation += desc;
                    return super.visitAnnotation(desc, visible);
                }
            }, 0);

            // check interface
            if (ASMUtil.isInterface(classDesc.access)) {
                return null;
            }

            classDesc.classBeingRedefined = classBeingRedefined;
            ClassWriter cw = new AgentClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = cw;
            List<ASM> workAsms = asms;

            for (int i = workAsms.size() - 1; i >= 0; i--) {
                cv = workAsms.get(i).transform(cv, className, classDesc);
                if (cv != cw) {
                    cr = new ClassReader(classfileBuffer);
                    cr.accept(cv, ClassReader.EXPAND_FRAMES);
                    classfileBuffer = cw.toByteArray();
                    cv = cw = new AgentClassWriter(ClassWriter.COMPUTE_FRAMES);
                }
            }

            return classfileBuffer;
        } catch (Throwable r) {

        } finally {
            hookingContext.set(null);
        }

        return null;
    }
}
