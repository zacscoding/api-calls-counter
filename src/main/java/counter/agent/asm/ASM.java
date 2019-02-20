package counter.agent.asm;

import org.objectweb.asm.ClassVisitor;
import counter.agent.ClassDesc;

/**
 * @GitHub : https://github.com/zacscoding
 */
public interface ASM {

    ClassVisitor transform(ClassVisitor cv, String className, ClassDesc classDesc);
}
