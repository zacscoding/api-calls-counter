package counter.agent.proxy;

/**
 * Get
 *
 * @GitHub : https://github.com/zacscoding
 */
public class HttpTraceFactory {

    private static final String HTTP_TRACE = "tracker.xtra.http.HttpTrace";
    private static final String HTTP_TRACE3 = "tracker.xtra.http.HttpTrace3";

    /**
     * Create HttpTrace instance
     */
    public static HttpTrace create(ClassLoader parent, Object requestObj) {
        try {
            ClassLoader loader = LoaderManager.getHttpLoader(parent);
            // check above servlet version 3.x or not
            boolean isServlet3 = true;
            try {
                requestObj.getClass().getMethod("getParts");
            } catch (Exception e) {
                isServlet3 = false;
            }

            Class c = null;
            if (isServlet3) {
                // c = loader.loadClass(HTTP_TRACE3, true, loader);
                c = Class.forName(HTTP_TRACE3, true, loader);
            } else {
                c = Class.forName(HTTP_TRACE, true, loader);
            }

            return (HttpTrace) c.newInstance();
        } catch (Throwable e) {
            return null;
        }
    }
}
