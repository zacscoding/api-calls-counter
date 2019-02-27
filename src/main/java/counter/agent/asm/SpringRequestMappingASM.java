package counter.agent.asm;

import counter.agent.ClassDesc;
import counter.agent.trace.TraceMain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class SpringRequestMappingASM implements ASM {

    // spring controller annotations
    public static final String[] controllerAnnotations = new String[]{
        "Lorg/springframework/stereotype/Controller;",
        "Lorg/springframework/web/bind/annotation/RestController;"
    };

    // spring RequestMapping annotations
    private static final Set<String> requestMappingAnnotations = new HashSet<String>();

    static {
        requestMappingAnnotations.add("Lorg/springframework/web/bind/annotation/RequestMapping;");
        requestMappingAnnotations.add("Lorg/springframework/web/bind/annotation/GetMapping;");
        requestMappingAnnotations.add("Lorg/springframework/web/bind/annotation/PostMapping;");
        requestMappingAnnotations.add("Lorg/springframework/web/bind/annotation/PutMapping;");
        requestMappingAnnotations.add("Lorg/springframework/web/bind/annotation/DeleteMapping;");
        requestMappingAnnotations.add("Lorg/springframework/web/bind/annotation/PatchMapping;");
    }

    public static boolean containsRequestMapping(String desc) {
        return requestMappingAnnotations.contains(desc);
    }

    @Override
    public ClassVisitor transform(ClassVisitor cv, String className, ClassDesc classDesc) {
        if (classDesc.annotation == null) {
            return cv;
        }

        for (int i = 0; i < controllerAnnotations.length; i++) {
            if (classDesc.annotation.indexOf(controllerAnnotations[i]) > -1) {
                if (className.equals("org/springframework/boot/autoconfigure/web/servlet/error/BasicErrorController")) {
                    continue;
                }

                System.out.println("## Found target class : " + className);
                return new SpringRequestMappingCV(cv, className);
            }
        }

        return cv;
    }
}

/**
 * ClassVisitor
 */
class SpringRequestMappingCV extends ClassVisitor implements Opcodes {

    private String className;
    private String urlPattern;

    public SpringRequestMappingCV(ClassVisitor cv, String className) {
        super(ASM5, cv);
        this.className = className;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(desc, visible);

        if (av != null && SpringRequestMappingASM.containsRequestMapping(desc)) {
            System.out.println("## Found RequestMapping in CV : " + className);
            return new SpringRequestMappingCVAV(av);
        }

        return av;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature,
        String[] exceptions) {
        System.out.println("## Check visitMethod.. in CV : " + methodName);

        MethodVisitor mv = super.visitMethod(access, methodName, desc, signature, exceptions);
        if (mv == null) {
            return mv;
        }

        if (methodName.startsWith("<")) {
            return mv;
        }

        System.out.println("## Found target method : " + methodName + desc
            + ", class method url : " + urlPattern);

        return new SpringRequestMappingMV(mv, urlPattern);
    }

    class SpringRequestMappingCVAV extends AnnotationVisitor implements Opcodes {

        public SpringRequestMappingCVAV(AnnotationVisitor av) {
            super(ASM5, av);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            AnnotationVisitor av = super.visitArray(name);

            if (av == null || name == null) {
                return av;
            }

            if (name.equals("value") || name.equals("path")) {
                System.out.println("## SpringRequestMappingCVAV::visitArray() name : " + name);
                return new SpringRequestMappingCVAVAV(av);
            }

            return av;
        }
    }

    class SpringRequestMappingCVAVAV extends AnnotationVisitor implements Opcodes {

        public SpringRequestMappingCVAVAV(AnnotationVisitor av) {
            super(ASM5, av);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            System.out.print("## SpringRequestMappingCVAVAV::visit() name : " + name + ", value : " + value);

            if (value != null) {
                String toStringValue = value.toString();
                if (toStringValue != null) {
                    urlPattern = toStringValue;
                }
            }
        }
    }
}

class SpringRequestMappingMV extends MethodVisitor implements Opcodes {

    private String urlPattern;
    private static final String TRACE_MAIN = TraceMain.class.getName().replace('.', '/');
    private static final String METHOD_NAME = "counterApiCallsByControllerMethods";
    private static final String METHOD_SIGNATURE = "(Ljava/lang/String;)V";

    public SpringRequestMappingMV(MethodVisitor mv, String urlPattern) {
        super(ASM5, mv);
        if (urlPattern == null) {
            urlPattern = "";
        }
        this.urlPattern = urlPattern;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(desc, visible);

        if (SpringRequestMappingASM.containsRequestMapping(desc)) {
            System.out.println("## Found requestMapping annotations in MV. desc : " + desc);
            return new SpringRequestMappingMVAV(av);
        }

        return av;
    }

    @Override
    public void visitCode() {
        System.out.println("## SpringRequestMappingMV::visitCode() is called..");
        mv.visitLdcInsn(urlPattern);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, TRACE_MAIN, METHOD_NAME, METHOD_SIGNATURE, false);
        mv.visitCode();
    }

    class SpringRequestMappingMVAV extends AnnotationVisitor implements Opcodes {

        public SpringRequestMappingMVAV(AnnotationVisitor av) {
            super(ASM5, av);
            System.out.println("## SpringRequestMappingMVAV() is called..");
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            AnnotationVisitor av = super.visitArray(name);

            if (av == null || name == null) {
                return av;
            }

            System.out.println("## SpringRequestMappingMVAV::visitArray() name : " + name);

            if (name.equals("value") || name.equals("path")) {
                return new SpringRequestMappingMVAVAV(av);
            }

            return av;
        }
    }

    class SpringRequestMappingMVAVAV extends AnnotationVisitor implements Opcodes {

        public SpringRequestMappingMVAVAV(AnnotationVisitor av) {
            super(ASM5, av);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);

            if (value instanceof String) {
                urlPattern += (String) value;
                System.out.println("## >> Url pattern : " + urlPattern);
            } else {
                System.out.println("## >> Cannot cast to String.. " + value.getClass().getName());
            }
        }
    }
}
