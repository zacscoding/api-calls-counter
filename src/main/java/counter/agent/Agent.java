package counter.agent;

import java.lang.instrument.Instrumentation;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class Agent {

    private static Instrumentation instrumentation;

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        if (Agent.instrumentation != null) {
            return;
        }

        try {
            Agent.instrumentation = inst;
            Agent.instrumentation.addTransformer(new AgentTransformer());
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
