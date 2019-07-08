package ngp.mysql.elasticsearch.connector.connection;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;


public enum ElasticSearchConnection {
	INSTANCE;
	
	private RestHighLevelClient restClient;
	
	public RestHighLevelClient getRestClient(String host, int port, String scheme) {
		restClient = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost(host, port, scheme)));
		
		return restClient;
	}
	
	
}
