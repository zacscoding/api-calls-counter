package counter.agent.trace;

import com.google.gson.Gson;
import counter.agent.collector.ApiLogCollector;
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
    public static void countApiCallsByServlet(Object request) {
        try {
            // start trace only first calls
            if (TraceContextManager.getContext() != null) {
                return;
            }

            if (HTTP_TRACE == null) {
                initHttp(request);
            }

            if (!HTTP_TRACE.isTrace(request)) {
                return;
            }

            TraceContext ctx = TraceContextManager.getOrCreateContext();
            HTTP_TRACE.start(ctx, request);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            // ignore
        }
    }

    /**
     * Count if called from Controller`s methods
     */
    public static void countApiCallsByControllerMethods(String urlPattern) {
        try {
            TraceContext context = TraceContextManager.disposeContext();

            if (context == null) {
                return;
            }

            context.setUrlPattern(urlPattern);
            ApiLogCollector.INSTANCE.pushLog(context);
        } catch (Exception e) {
            // ignore
        }
    }

    public static void disposeContext() {
        TraceContextManager.disposeContext();
    }

    private static void initHttp(Object request) {
        synchronized (LOCK) {
            if (HTTP_TRACE == null) {
                HTTP_TRACE = HttpTraceFactory.create(request.getClass().getClassLoader(), request);
            }
        }
    }
}