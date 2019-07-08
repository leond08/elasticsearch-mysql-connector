package ngp.mysql.elasticsearch.connector;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Main Class
 *
 *	@author falcon
 */
public class Main {
	
	private final AppParams params;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
    public static void main( String[] args ) {
    	Main m = new Main(args);
        m.execute();
    }
    
    public Main(String[] args) {
    	this.params = getParameters(args);
	}
   
    public void execute() {
    	JobPoller poller = new JobPoller(params);
    	Thread t = new Thread(poller);
    	t.start();
    }
    
	public AppParams getParameters(String args[]) {
        CommandLineParser parser = new BasicParser();
        HelpFormatter formatter = new HelpFormatter();
        Options options = createOptions();
        CommandLine cmd = null;
        AppParams result = null;
        try {
            cmd = parser.parse(options, args);
            result = new AppParams(cmd);
        } catch (Exception e) {
        	LOGGER.error("Unable to convert", e);
        	
            formatter.printHelp("java -jar elasticsearch-connector-0.0.1-SNAPSHOT.one-jar.jar  [options here]", options);
            
            System.exit(1);
        }

        return result;
    }
    
    public Options createOptions(){
        Options options = new Options();

        Option opt = null;
        
        opt = new Option("mysql_host", "mysqlHost", true, "Mysql Host");
        options.addOption(opt);
        
        opt = new Option("mysql_username", "mysqlUsername", true, "Mysql Username");
        options.addOption(opt);
        
        opt = new Option("mysql_password", "mysqlPassword", true, "Mysql Password");
        options.addOption(opt);
        
        opt = new Option("mysql_port", "mysqlPort", true, "Mysql Port");
        options.addOption(opt);
        
        opt = new Option("elasticsearch_host", "elastisearchHost", true, "Elasticsearch Host Connection");
        options.addOption(opt);
        
        opt = new Option("elasticsearch_port", "elasticsearchPort", true, "Elasticsearch Port Connection");
        options.addOption(opt);
        
        opt = new Option("table_name", "tableName", true, "Table name");
        options.addOption(opt);
        
        opt = new Option("reindex", "reindex", true, "Set to true to reindex data to elasticsearch");
        options.addOption(opt);
        
        opt = new Option("file_mapping", "fileMapping", true, "File that contains the mapping");
        options.addOption(opt);
        
        opt = new Option("index_name", "indexName", true, "Index name");
        options.addOption(opt);
        
        opt = new Option("elasticsearch_scheme", "elasticSearchScheme", true, "HTTP or HTTPS");
        options.addOption(opt);
        
        opt = new Option("database_name", "databaseName", true, "Name of the database");
        options.addOption(opt);

        return options;
    }
}
