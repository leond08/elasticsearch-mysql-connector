package ngp.mysql.elasticsearch.connector;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Parameters 
 * 
 * @author Falcon
 */
public class AppParams {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AppParams.class);
	private String mysql_host;
	private String mysql_username;
	private String mysql_password;
	private String elasticsearch_host;
	private String tablename;
	private int	   elasticsearch_port;
	private int mysql_port;
    private final int threadCount;
    private boolean reindex = false;
    private String fileMapping;
    private String indexName = null;
    private String elasticsearch_scheme;
    private String databaseName;
    
    AppParams(CommandLine cmd) {

        Properties p = new Properties();

        this.mysql_host = cmd.getOptionValue("mysql_host");
        this.mysql_username = cmd.getOptionValue("mysql_username");
        this.mysql_password = cmd.getOptionValue("mysql_password");
        this.mysql_port = new Integer(cmd.getOptionValue("mysql_port"));
        this.elasticsearch_host = cmd.getOptionValue("elasticsearch_host");
        this.elasticsearch_port = new Integer(cmd.getOptionValue("elasticsearch_port"));
        this.tablename = cmd.getOptionValue("table_name");
        this.reindex = Boolean.parseBoolean(cmd.getOptionValue("reindex"));
        this.fileMapping = cmd.getOptionValue("file_mapping");
        this.elasticsearch_scheme = cmd.getOptionValue("elasticsearch_scheme");
        this.databaseName = cmd.getOptionValue("database_name");

        int temp = 10;

        
        try {
            temp = new Integer(StringUtils.defaultIfEmpty(p.getProperty("thread_count"), cmd.getOptionValue("thread_count")));
        } catch (NumberFormatException nfe) { }
        
        if (temp < 1) {
            temp = 10;
        }
        
     
        this.indexName = cmd.getOptionValue("index_name");
       
        
        this.threadCount = temp; 

    }
    
    public String getMysqlHost() {
    	return mysql_host;
    }
    
    public String getMysqlUsername() {
    	return mysql_username;
    }
    
    public int getThreadCount() {
    	return threadCount;
    }
    
    public String getMysqlPassword() {
    	return mysql_password;
    }
    
    public int getMysqlPort() {
    	return mysql_port;
    }
    
    public String getElasticSearchHost() {
    	return elasticsearch_host;
    }
    
    public int getElasticSearchPort() { 
    	return elasticsearch_port;
    }
    
    public String getTableName() {
    	return tablename;
    }
    
    public boolean getReindex() {
    	return reindex;
    }
    
    public String getFileMapping() {
    	return fileMapping;
    }
    
    public String getIndexName() {
    	return indexName;
    }
    
    public String getElasticsearchScheme() {
    	return elasticsearch_scheme;
    }
    
    public String getMysqlDatabase() {
    	return databaseName;
    }
    
}
