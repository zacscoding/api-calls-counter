package counter.agent.trace;

/**
 * Managed TraceContext instance depends on thread
 *
 * @GitHub : https://github.com/zacscoding
 */
public class TraceContextManager {

    private static ThreadLocal<TraceContext> contexts = new ThreadLocal<TraceContext>();

    /**
     * Get or create TraceContext from current thread
     */
    public static TraceContext getOrCreateContext() {
        TraceContext ctx = null;

        if ((ctx = contexts.get()) == null) {
            ctx = new TraceContext();
            contexts.set(ctx);
        }

        return contexts.get();
    }

    /**
     * Setting TraceContext to current thread
     */
    public static void setTraceContext(TraceContext ctx) {
        contexts.set(ctx);
    }

    /**
     * Getting TraceContext from current thread
     *
     * @return TraceContext instance or null
     */
    public static TraceContext getContext() {
        return contexts.get();
    }

    /**
     * Dispose TraceContext
     */
    public static TraceContext disposeContext() {
        TraceContext context = contexts.get();

        if (context != null) {
            contexts.set(null);
        }

        return context;
    }
}
