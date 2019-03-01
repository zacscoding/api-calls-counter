package counter.xtra.http;

import counter.agent.proxy.HttpTrace;
import counter.agent.trace.TraceContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class ServletHttpTrace implements HttpTrace {

    @Override
    public boolean isTrace(Object req) {
        if (!(req instanceof HttpServletRequest)) {
            return false;
        }

        return true;
//        HttpServletRequest request = (HttpServletRequest) req;
//        String uri = request.getRequestURI();
    }

    @Override
    public void start(TraceContext ctx, Object req) {
        HttpServletRequest request = (HttpServletRequest) req;
        ctx.setMethod(request.getMethod());
        ctx.setRequestURI(request.getRequestURI());
        ctx.setRequestAt(System.currentTimeMillis());
    }
}
