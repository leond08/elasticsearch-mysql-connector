package ngp.mysql.elasticsearch.connector.internal;

import java.io.IOException;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ngp.mysql.elasticsearch.connector.AppParams;
import ngp.mysql.elasticsearch.connector.connection.ElasticSearchConnection;


/*
 * 
 * @author falcon
 */
public class ElasticsearchIndexWriter extends BaseIndexWriter {
	
	private RestHighLevelClient client;
	private String elasticSearchHost;
	private int elasticSearchPort;
	private String elasticSearchScheme;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndexWriter.class);
	
	public ElasticsearchIndexWriter(AppParams params) {
		this.elasticSearchHost = params.getElasticSearchHost();
		this.elasticSearchPort = params.getElasticSearchPort();
		this.elasticSearchScheme = params.getElasticsearchScheme();
	}
	
	public void processRequest(BulkRequest request) 
			throws IOException {
				
			client = ElasticSearchConnection.INSTANCE.getRestClient(elasticSearchHost,
					elasticSearchPort, elasticSearchScheme);
			
			ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
			    @Override
			    public void onResponse(BulkResponse BulkResponse) {
			    	
			        LOGGER.info("Done");
			        
			    }

			    @Override
			    public void onFailure(Exception e) {
			        LOGGER.error(e.getMessage(), e);
			    }
			};
			
			try {
				client.bulkAsync(request, RequestOptions.DEFAULT, listener);
			}finally {
				//client.close();
			}
			
		}


	public void processRequestSingle(IndexRequest request) 
			throws IOException {
				
			client = ElasticSearchConnection.INSTANCE.getRestClient(elasticSearchHost,
					elasticSearchPort, elasticSearchScheme);
			
			ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
			    @Override
			    public void onResponse(IndexResponse indexResponse) {
			    	
			        LOGGER.info("Done");
			        
			    }

			    @Override
			    public void onFailure(Exception e) {
			        LOGGER.error(e.getMessage(), e);
			    }
			};
			
			try {
				client.indexAsync(request, RequestOptions.DEFAULT, listener);
			}finally {
				//client.close();
			}
			
		}

}
