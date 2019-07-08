package ngp.mysql.elasticsearch.connector.internal;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkRequest;

import ngp.mysql.elasticsearch.connector.AppParams;

public abstract class BaseIndexWriter {
	
	public void addDocument(BulkRequest bulkRequest) 
		throws IOException {
		
	}
	
	public void updateDocument(BulkRequest bulkRequest)
		throws IOException {
		
	}
	
	public void deleteDocument(BulkRequest bulkRequest) 
		throws IOException {
		
	}
	
}
