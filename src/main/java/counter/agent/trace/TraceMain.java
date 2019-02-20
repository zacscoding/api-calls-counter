package counter.agent.trace;

import counter.agent.proxy.HttpTrace;
import counter.agent.proxy.HttpTraceFactory;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class TraceMain {

    private static Object LOCK = new Object();
    private static HttpTrace HTTP_TRACE = null;

    /**
     * Count if called from HttpServlet or Filters
     */
    public static void counterApiCallsByServlet(Object request) {
        try {
            // start trace only first calls
            if (TraceContextManager.getContext() != null) {
                return;
            }

            if (HTTP_TRACE == null) {
                initHttp(request);
            }

            TraceContext ctx = TraceContextManager.getOrCreateContext();
            HTTP_TRACE.start(ctx, request);
        } catch (Throwable t) {
            // ignore
        }
    }

    /**
     * Count if called from Controller`s methods
     */
    public static void counterApiCallsByControllerMethods(String urlPattern) {
        try {
            TraceContext context = TraceContextManager.disposeContext();
            System.out.println("TraceMain::counterApiCallsByControllerMethods() context : " + context);
            if (context == null) {
                return;
            }

            context.setUrlPattern(urlPattern);
            System.out.println("## Trace api calls... : " + context);
            // TODO :: push collector
        } catch (Exception e) {
            // ignore
        }
    }

    private static void initHttp(Object request) {
        synchronized (LOCK) {
            if (HTTP_TRACE == null) {
                HTTP_TRACE = HttpTraceFactory.create(request.getClass().getClassLoader(), request);
            }
        }
    }
}
