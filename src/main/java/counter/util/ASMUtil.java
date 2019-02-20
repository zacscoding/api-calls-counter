package counter.util;

import org.objectweb.asm.Opcodes;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class ASMUtil {

    /**
     * Check interface or not
     */
    public static boolean isInterface(int access) {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }
}
