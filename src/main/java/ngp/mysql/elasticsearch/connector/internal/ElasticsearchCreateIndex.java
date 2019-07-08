package ngp.mysql.elasticsearch.connector.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ngp.mysql.elasticsearch.connector.AppParams;
import ngp.mysql.elasticsearch.connector.connection.ElasticSearchConnection;


public class ElasticsearchCreateIndex {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchCreateIndex.class);
	
	private MysqlReindexWriter mysqlReindexWriter;
	
	public ElasticsearchCreateIndex() {}
	
	public void createIndex(AppParams params) 
		throws IOException {
		
		RestHighLevelClient client = ElasticSearchConnection.INSTANCE.getRestClient(params.getElasticSearchHost(),
				params.getElasticSearchPort(), params.getElasticsearchScheme());

		
		if (params.getIndexName() != null) {
			
			
			
			CreateIndexRequest request = new CreateIndexRequest(params.getIndexName()); 
			//request.mapping("_doc", getMapping(params.getFileMapping()),XContentType.JSON);
			request.source(getMapping(params.getFileMapping()),XContentType.JSON);
			
			
			
			ActionListener<CreateIndexResponse> listener =
			    new ActionListener<CreateIndexResponse>() {
			        @Override
			        public void onResponse(CreateIndexResponse createIndexResponse) {
			            if (createIndexResponse.isAcknowledged()) {
			            	LOGGER.info("index created");
			            	
			            	mysqlReindexWriter = new MysqlReindexWriter(params);
			        		
			        		try {
			        			mysqlReindexWriter.readDatabase();
			        		}
			        		catch(Exception e) {
			        			
			        		}
			            }
			        }
		
			        @Override
			        public void onFailure(Exception e) {
			            LOGGER.error(e.getMessage(), e);
			           
			        }
		    };
		    
		    try {
		    	
		    	client.indices().createAsync(request, RequestOptions.DEFAULT, listener);
		    }
		    catch(ElasticsearchException e) {
		    	if (e.status() == RestStatus.FOUND) {
		    		LOGGER.error(e.getMessage(), e);
		    	}
		    }
		    finally {
		    	
		    }
		}
		else {
			LOGGER.info("Please provide index name with parameter -index_name");
			System.exit(1);
		}
		

	}
	
	public void deleteIndex(AppParams params) {
		
		RestHighLevelClient client = ElasticSearchConnection.INSTANCE.getRestClient(params.getElasticSearchHost(),
				params.getElasticSearchPort(), params.getElasticsearchScheme());

		if (params.getIndexName() != null) {
			
			DeleteIndexRequest request = new DeleteIndexRequest(params.getIndexName());
			
			ActionListener<AcknowledgedResponse> listener =
			        new ActionListener<AcknowledgedResponse>() {
			    @Override
			    public void onResponse(AcknowledgedResponse deleteIndexResponse) {
			        if (deleteIndexResponse.isAcknowledged()) {
			        	LOGGER.info("index deleted");
			        }
			    }
			    
			    @Override
			    public void onFailure(Exception e) {
			        LOGGER.info(e.getMessage(), e);
			        
			    }
			};
			
			
		    try {
		    	
		    	client.indices().deleteAsync(request, RequestOptions.DEFAULT, listener);
		    }
		    catch(ElasticsearchException e) {
		    	LOGGER.error(e.getMessage(), e);
		    	
		    }
		    finally {
		    	
		    }
		}
		else {
			LOGGER.info("Please provide index name with parameter -index_name");
			System.exit(1);
		}
	}
	
	/* Read mapping form text
	 * 
	 * @param String file path
	 */
	private String getMapping(String file) {
		String filename = file;
		
		Path path = Paths.get(filename);
		List<String> allLines = null;
		String map = "";
		
		try {
			
			byte[] bytes = Files.readAllBytes(path);
			allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
			
			for (int i = 0; i < allLines.size(); i++) {
				map += allLines.get(i);
			}
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		//String str = Arrays.toString(allLines.stream().toArray(String[]::new));
		
		String str = map;
		
		str = str.replaceAll("[\\(\\)]", "");
		
		LOGGER.info("mapping: " + str);
		
		return str;
	}
	
}
