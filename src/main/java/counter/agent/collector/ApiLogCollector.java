package counter.agent.collector;

import counter.agent.trace.TraceContext;
import java.time.Duration;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.scheduler.Schedulers;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class ApiLogCollector {

    public static ApiLogCollector INSTANCE = new ApiLogCollector();

    private EmitterProcessor emitterProcessor;
    private FluxSink<TraceContext> fluxSink;
    private Flux<TraceContext> flux;
    private ApiLogSender apiLogSender;
    private SubscribeTask task;

    private ApiLogCollector() {
        this.emitterProcessor = EmitterProcessor.create();
        this.fluxSink = emitterProcessor.sink(OverflowStrategy.DROP);
        this.flux = emitterProcessor.publishOn(Schedulers.elastic());
        this.apiLogSender = new ElasticsearchApiLogSender();
        this.task = new SubscribeTask(flux, apiLogSender);
        task.setDaemon(true);
        task.start();
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> task.destroy())
        );
    }

    public void pushLog(TraceContext context) {
        fluxSink.next(context);
    }

    private static class SubscribeTask extends Thread {

        private Flux<TraceContext> flux;
        private ApiLogSender apiLogSender;
        private Disposable disposable;

        public SubscribeTask(Flux<TraceContext> flux, ApiLogSender apiLogSender) {
            this.flux = flux;
            this.apiLogSender = apiLogSender;
        }

        @Override
        public void run() {
            int maxSize = Integer.parseInt(System.getProperty("counter.buffer.size", "100"));
            int seconds = Integer.parseInt(System.getProperty("counter.buffer.seconds", "10"));
            disposable = flux.bufferTimeout(maxSize, Duration.ofSeconds(seconds), Schedulers.elastic())
                .filter(values -> values != null && !values.isEmpty())
                .subscribe(values -> apiLogSender.sendLogs(values));
        }

        public void destroy() {
            if (disposable != null) {
                disposable.dispose();
            }
        }
    }
}
