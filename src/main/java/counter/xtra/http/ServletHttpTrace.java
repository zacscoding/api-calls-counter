package counter.xtra.http;

import counter.agent.proxy.HttpTrace;
import counter.agent.trace.TraceContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class ServletHttpTrace implements HttpTrace {

    @Override
    public void start(TraceContext ctx, Object req) {
        HttpServletRequest request = (HttpServletRequest) req;

        ctx.setHttpMethod(request.getMethod());
        ctx.setRequestAt(System.currentTimeMillis());
    }
}
