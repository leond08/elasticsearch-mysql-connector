package ngp.mysql.elasticsearch.connector;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ngp.mysql.elasticsearch.connector.internal.ElasticsearchCreateIndex;
import ngp.mysql.elasticsearch.connector.internal.MysqlEventListen;
import ngp.mysql.elasticsearch.connector.internal.MysqlReindexWriter;

public class JobPoller implements Runnable {
	
	private final AppParams params;
	
	private MysqlEventListen mysqlEventListen;
	
	private ElasticsearchCreateIndex index; 
	
	private MysqlReindexWriter mysqlReindexWriter;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobPoller.class);
	
	public JobPoller(AppParams params) {
		this.params = params;
	}
	
	public void run() {
		
		if (this.params.getReindex()) {
			LOGGER.info("Reindexing data...");
			
			deleteIndex(this.params); 
			createIndex(this.params); 
			
		}
		else {
			
			LOGGER.info("listening to mysql events...");
			process(this.params);
			
		}
	}
	
	public void process(AppParams params) {
		mysqlEventListen = new MysqlEventListen();
		
		try {
			mysqlEventListen.listen(params);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void createIndex(AppParams params) {
		
		index = new ElasticsearchCreateIndex();
		
		try {
			
			index.createIndex(params);
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void deleteIndex(AppParams params) {
		
		index = new ElasticsearchCreateIndex();
		
		index.deleteIndex(params);
		
	}

}
