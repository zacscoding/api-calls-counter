package counter.agent;

import java.util.Arrays;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class ClassDesc {

    public int version;
    public int access;
    public String name;
    public String signature;
    public String superName;
    public String[] interfaces;
    public String annotation;
    public Class classBeingRedefined;

    public void set(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    @Override
    public String toString() {
        return "ClassDesc{" +
            "version=" + version +
            ", access=" + access +
            ", name='" + name + '\'' +
            ", signature='" + signature + '\'' +
            ", superName='" + superName + '\'' +
            ", interfaces=" + Arrays.toString(interfaces) +
            ", annotation='" + annotation + '\'' +
            ", classBeingRedefined=" + classBeingRedefined +
            '}';
    }
}
