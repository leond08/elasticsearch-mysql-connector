package ngp.mysql.elasticsearch.connector.internal;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

import ngp.mysql.elasticsearch.connector.AppParams;
import ngp.mysql.elasticsearch.connector.connection.MysqlConnection;

/*
 *
 * @author falcon
 */
public class MysqlEventListen {

	Map<String, Long> tableMap = new HashMap<String, Long>();
	private ElasticsearchIndexWriter elasticsearchIndexWriter;

	private static final Logger LOGGER = LoggerFactory.getLogger(MysqlEventListen.class);

	public MysqlEventListen() { }

	public void listen(AppParams params) throws Throwable {

		elasticsearchIndexWriter = new ElasticsearchIndexWriter(params);

		BinaryLogClient client = MysqlConnection.INSTANCE.getClient(params.getMysqlHost(), params.getMysqlPort(),
				params.getMysqlUsername(), params.getMysqlPassword());

		BulkRequest bulkRequest = new BulkRequest();

		 client.registerEventListener(new EventListener() {

			@Override
			public void onEvent(Event event) {
				EventData data = event.getData();

				if (data instanceof TableMapEventData) {
					TableMapEventData tableData = (TableMapEventData)data;
	                tableMap.put(tableData.getTable(), tableData.getTableId());

				} else if(data instanceof WriteRowsEventData) {
	                WriteRowsEventData eventData = (WriteRowsEventData)data;

	                if(eventData.getTableId() == tableMap.get(params.getTableName())) {
	                	LOGGER.info("indexing data...");
	                    for(Object[] result: eventData.getRows()) {

	                        bulkRequest.add(new IndexRequest(params.getIndexName(), "_doc", java.lang.String.valueOf(result[0]))
	                        		.source(getMapping(result)));

	                        try {

							    					if (!bulkRequest.requests().isEmpty()) {
							    						elasticsearchIndexWriter.processRequest(bulkRequest);
							    					}

							    				} catch (IOException e) {

							    				}

	                    }

	                }
	            } else if(data instanceof UpdateRowsEventData) {
	                UpdateRowsEventData eventData = (UpdateRowsEventData)data;

	                if(eventData.getTableId() == tableMap.get(params.getTableName())) {
	                	LOGGER.info("updating index...");
	                    for(Map.Entry<Serializable[], Serializable[]> row : eventData.getRows()) {

	                    	bulkRequest.add(new UpdateRequest(params.getIndexName(), "_doc", java.lang.String.valueOf(row.getValue()[0]))
	                    			.doc(getMapping(row.getValue())));

	                    	try {

						    					if (!bulkRequest.requests().isEmpty()) {
						    						elasticsearchIndexWriter.processRequest(bulkRequest);
						    					}

						    				} catch (IOException e) {

						    				}

	                    }

	                }
	            } else if(data instanceof DeleteRowsEventData) {
	                DeleteRowsEventData eventData = (DeleteRowsEventData)data;

	                if(eventData.getTableId() == tableMap.get(params.getTableName())) {
	                	LOGGER.info("deleting index...");
	                    for(Object[] result: eventData.getRows()) {
	                    	bulkRequest.add(new DeleteRequest(params.getIndexName(), "_doc", java.lang.String.valueOf(result[0])));

	                    	try {

						    					if (!bulkRequest.requests().isEmpty()) {
						    						elasticsearchIndexWriter.processRequest(bulkRequest);
						    					}

						    				} catch (IOException e) {

						    				}
	                    }

	                }
	            }
			}
         });

		 client.connect();
	}

	static Map<String, Object> getMapping(Object[] data) {

        Map<String, Object> map = new HashMap<>();


        map.put("id", checkNull(String.valueOf(data[0])));
        map.put("pbn", checkNull(String.valueOf(data[1])));
        map.put("business_name", checkNull(String.valueOf(data[2])));
        map.put("status", checkNull(String.valueOf(data[6])));
        map.put("business_setup", checkNull(String.valueOf(data[20])));

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
