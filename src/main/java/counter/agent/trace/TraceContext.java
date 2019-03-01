package counter.agent.trace;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class TraceContext {

    private String method;
    private String requestURI;
    private String urlPattern;
    private long requestAt;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public long getRequestAt() {
        return requestAt;
    }

    public void setRequestAt(long requestAt) {
        this.requestAt = requestAt;
    }

    @Override
    public String toString() {
        return "TraceContext{" +
            "method='" + method + '\'' +
            ", requestURI='" + requestURI + '\'' +
            ", urlPattern='" + urlPattern + '\'' +
            ", requestAt=" + requestAt +
            '}';
    }
}
