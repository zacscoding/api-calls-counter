package counter.agent.trace;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class TraceContext {

    private String httpMethod;
    private String urlPattern;
    private long requestAt;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
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
}
