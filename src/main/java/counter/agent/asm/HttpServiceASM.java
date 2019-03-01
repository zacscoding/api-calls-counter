package counter.agent.asm;

import counter.agent.ClassDesc;
import counter.agent.trace.TraceMain;
import java.util.HashSet;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class HttpServiceASM implements ASM {

    public HashSet<String> servlets = new HashSet<String>();

    public HttpServiceASM() {
        servlets.add("javax/servlet/http/HttpServlet");
        servlets.add("weblogic/servlet/jsp/JspBase");
    }

    @Override
    public ClassVisitor transform(ClassVisitor cv, String className, ClassDesc classDesc) {
        // check servlet class
        if (servlets.contains(className)) {
            return new HttpServiceCV(cv, className);
        }

        // check filter interface
        if (classDesc.interfaces != null) {
            for (int i = 0; i < classDesc.interfaces.length; i++) {
                if ("javax/servlet/Filter".equals(classDesc.interfaces[i])) {
                    return new HttpServiceCV(cv, className);
                }
            }
        }

        return cv;
    }
}


/* ======================================== ClassVisitor ======================================== */
class HttpServiceCV extends ClassVisitor implements Opcodes {

    private static String TARGET_SERVICE = "service";
    private static String TARGET_DOFILTER = "doFilter";
    private static String TARGET_SIGNATURE = "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;";
    private String className;

    public HttpServiceCV(ClassVisitor cv, String className) {
        super(ASM5, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null) {
            return mv;
        }

        if (desc.startsWith(TARGET_SIGNATURE)) {
            if (TARGET_SERVICE.equals(name)) {
                return new HttpServiceMV(access, desc, mv, true);
            } else if (TARGET_DOFILTER.equals(name)) {
                return new HttpServiceMV(access, desc, mv, false);
            }
        }

        return mv;
    }
}

class HttpServiceMV extends LocalVariablesSorter implements Opcodes {

    private static final String TRACE_MAIN = TraceMain.class.getName().replace('.', '/');
    private static final String START_METHOD = "countApiCallsByServlet";
    private static final String START_SIGNATURE = "(Ljava/lang/Object;)V";
    private static final String END_METHOD = "disposeContext";
    private static final String END_SIGNATURE = "()V";

    private Label startFinally = new Label();
    private boolean isServlet;

    protected HttpServiceMV(int access, String desc, MethodVisitor mv, boolean isServlet) {
        super(ASM5, access, desc, mv);
        this.isServlet = isServlet;
    }

    @Override
    public void visitCode() {
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACE_MAIN, START_METHOD, START_SIGNATURE, false);
        mv.visitLabel(startFinally);
        mv.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACE_MAIN, END_METHOD, END_SIGNATURE, false);
        }
        mv.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        Label labelCatch = new Label();
        mv.visitTryCatchBlock(startFinally, labelCatch, labelCatch, null);
        mv.visitLabel(labelCatch);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACE_MAIN, END_METHOD, END_SIGNATURE, false);
        // throw
        mv.visitInsn(ATHROW);
        mv.visitMaxs(maxStack + 1, maxLocals + 2);
    }
}