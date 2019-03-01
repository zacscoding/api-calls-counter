package counter.agent.trace;

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
            System.out.println("countAPiCallsByServlet..");
            // start trace only first calls
            if (TraceContextManager.getContext() != null) {
                System.out.println("TraceContextManager is not null..");
                return;
            }

            if (HTTP_TRACE == null) {
                initHttp(request);
            }

            System.out.println("After init http.. :: " + HTTP_TRACE);

            if (!HTTP_TRACE.isTrace(request)) {
                System.out.println("HTTP_TRACE.isTrace(request) is returned false");
                return;
            }

            System.out.println("Start trace context..");
            TraceContext ctx = TraceContextManager.getOrCreateContext();
            HTTP_TRACE.start(ctx, request);
        } catch (Throwable t) {
            t.printStackTrace();
            // ignore
        }
    }

    /**
     * Count if called from Controller`s methods
     */
    public static void countApiCallsByControllerMethods(String urlPattern) {
        try {
            TraceContext context = TraceContextManager.disposeContext();
            System.out.println("## TraceMain::counterApiCallsByControllerMethods() context : " + context
                + ", urlPattern : " + urlPattern);

            if (context == null) {
                return;
            }

            context.setUrlPattern(urlPattern);
            ApiLogCollector.INSTANCE.pushLog(context);
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