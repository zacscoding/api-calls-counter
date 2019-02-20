package counter.agent.proxy;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import counter.agent.Agent;
import counter.util.BytesClassLoader;
import counter.util.FileUtil;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class LoaderManager {

    private static Map<Integer, ClassLoader> loaderMaps = new HashMap<Integer, ClassLoader>();

    /**
     * Create jar loader from counter.http.jar
     */
    public static ClassLoader getHttpLoader(ClassLoader parent) {
        return createLoader(parent, "counter.http");
    }

    /**
     * Create JarClassLoader
     *
     * @param parent parent class loader
     * @param key    jar name
     */
    private synchronized static ClassLoader createLoader(ClassLoader parent, String key) {
        int hashKey = (parent == null ? 0 : System.identityHashCode(parent));
        ClassLoader loader = loaderMaps.get(hashKey);

        if (loader == null) {
            try {
                byte[] bytes = deployJarBytes(key);
                if (bytes != null) {
                    loader = new BytesClassLoader(bytes, parent);
                    loaderMaps.put(hashKey, loader);
                }
            } catch (Throwable t) {
            }
        }

        return loader;
    }

    /**
     * jar 파일 => byte를 얻는 메소드
     */
    private static byte[] deployJarBytes(String jarname) {
        try {
            InputStream is = Agent.class.getResourceAsStream("/" + jarname + ".jar");
            byte[] newBytes = FileUtil.readAll(is);
            is.close();
            return newBytes;
        } catch (Throwable t) {
            return null;
        }
    }

    private static int len(byte[] arr) {
        return arr == null ? 0 : arr.length;
    }
}
