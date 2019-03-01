package counter.agent.collector;

import com.google.gson.Gson;
import counter.agent.trace.TraceContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class ElasticsearchApiLogSender implements ApiLogSender {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private Gson gson;
    private RestHighLevelClient restHighLevelClient;

    public ElasticsearchApiLogSender() {
        initialize();
    }

    @Override
    public void sendLogs(List<TraceContext> contexts) {
        try {
            String indexName = "controller-log-" + LocalDate.now().format(FORMATTER);
            BulkRequest request = new BulkRequest();
            for (TraceContext context : contexts) {
                request.add(
                    new IndexRequest(indexName, "doc").source(gson.toJson(context), XContentType.JSON)
                );
            }

            restHighLevelClient.bulkAsync(request, new ActionListener<BulkResponse>() {
                @Override
                public void onResponse(BulkResponse response) {
                }

                @Override
                public void onFailure(Exception e) {
                }
            });


        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void initialize() {
        this.gson = new Gson();
        this.restHighLevelClient = new RestHighLevelClient(
            RestClient.builder(new HttpHost("127.0.0.1", 9200, "http"))
        );
    }
}
