package ngp.mysql.elasticsearch.connector.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ngp.mysql.elasticsearch.connector.AppParams;

public class MysqlReindexWriter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MysqlReindexWriter.class);
	private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String mysqlHost;
    private String mysqlUsername;
    private String mysqlPassword;
    private String mysqlDatabase;
    private String indexName;
    private ElasticsearchIndexWriter elasticsearchIndexWriter;
    private AppParams parameters;
    private String tableName;
	
	public MysqlReindexWriter(AppParams params) {
		this.mysqlHost = params.getMysqlHost();
		this.mysqlUsername = params.getMysqlUsername();
		this.mysqlPassword = params.getMysqlPassword();
		this.mysqlDatabase = params.getMysqlDatabase();
		this.indexName = params.getIndexName();
		this.parameters = params;
		this.tableName = params.getTableName();
	}
	
	public void readDatabase() {
		
		elasticsearchIndexWriter = new ElasticsearchIndexWriter(parameters);
		
		try {


			Class.forName("com.mysql.jdbc.Driver");
			
			// Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://"+ mysqlHost + "/" + mysqlDatabase + "?"
                            + "user="+mysqlUsername+"&password="+mysqlPassword);
            
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select id,pbn,business_name,business_setup,status from " + tableName);
            
            while(resultSet.next()) {
            	//Sample index
            	IndexRequest indexRequest = new IndexRequest(indexName, "_doc", resultSet.getString("id"))
                		.source(getMapping(resultSet.getString("id"),resultSet.getString("pbn"),
                				resultSet.getString("business_name"), resultSet.getString("business_setup"),
                				resultSet.getString("status")));
            	
            	elasticsearchIndexWriter.processRequestSingle(indexRequest);
   
            }
            
            //elasticsearchIndexWriter.processRequest(bulkRequest);
            
			
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			
		}
		
	}
	
	static Map<String, Object> getMapping(String id, String pbn,
			String businessName, String businessSetup, String status ) {
		
        Map<String, Object> map = new HashMap<>();
        // Sample mapping
        map.put("id", checkNull(id));
        map.put("pbn", checkNull(pbn));
        map.put("business_name", checkNull(businessName));
        map.put("status", checkNull(status));
        map.put("business_setup", checkNull(businessSetup));


        return map;
    }
	
	static String checkNull(String text) {
		
		if (text == null || text == "null" || text.isEmpty()) {
			
			text = "";
			
			return text;
		}
		
		return text;
	}
	
}
