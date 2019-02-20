package counter.agent.proxy;

import counter.agent.trace.TraceContext;

/**
 * @GitHub : https://github.com/zacscoding
 */
public interface HttpTrace {

    /**
     * Start trace context
     * -> settings HttpMethod & request time
     */
    void start(TraceContext ctx, Object req);
}
