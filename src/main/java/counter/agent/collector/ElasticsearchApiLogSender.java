package counter.agent.collector;

import com.google.gson.Gson;
import counter.agent.trace.TraceContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

/**
 * Send trace context to elasticsearch
 *
 * @GitHub : https://github.com/zacscoding
 */
public class ElasticsearchApiLogSender implements ApiLogSender {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private Gson gson;
    private RestClient restClient;
    private boolean error;

    public ElasticsearchApiLogSender() {
        initialize();
    }

    @Override
    public void sendLogs(List<TraceContext> contexts) {
        if (error) {
            return;
        }

        String indexName = "api-log-" + LocalDate.now().format(FORMATTER);
        StringBuilder requestBodyBuilder = new StringBuilder(contexts.size() * 20);

        for (TraceContext traceContext : contexts) {
            IndexRequest indexRequest = new IndexRequest(indexName, gson.toJson(traceContext));
            requestBodyBuilder.append(indexRequest.toJsonBulkRequest());
        }

        try {
            System.out.println(requestBodyBuilder);
            Map<String, String> params = Collections.emptyMap();
            HttpEntity entity = new NStringEntity(requestBodyBuilder.toString(), ContentType.APPLICATION_JSON);
            Response response = restClient.performRequest("POST", "_bulk", params, entity);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void initialize() {
        this.gson = new Gson();
        String elasticsearchHost = System.getProperty("counter.elasticsearch.host");
        if (elasticsearchHost == null || elasticsearchHost.length() < 1) {
            error = true;
        }

        try {
            StringTokenizer tokenizer = new StringTokenizer(elasticsearchHost, ",");

            int tokenSize = tokenizer.countTokens();
            HttpHost[] httpHosts = new HttpHost[tokenSize];

            for (int i = 0; tokenizer.hasMoreTokens(); i++) {
                URL url = new URL(tokenizer.nextToken());
                httpHosts[i] = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            }

            this.restClient = RestClient.builder(httpHosts).build();
        } catch (MalformedURLException e) {
            error = true;
        }
    }

    public static class IndexRequest {

        private String indexName;
        private String document;

        public IndexRequest(String indexName, String document) {
            this.indexName = indexName;
            this.document = document;
        }

        public String toJsonBulkRequest() {
            return "{ \"index\" : { \"_index\" : \"" + indexName + "\", \"_type\" : \"_doc\"} } \n"
                + document + "\n";
        }
    }
}