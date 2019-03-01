package counter.agent.collector;

import counter.agent.trace.TraceContext;
import java.util.List;

/**
 * @GitHub : https://github.com/zacscoding
 */
public interface ApiLogSender {

    void sendLogs(List<TraceContext> contexts);
}
