package ngp.mysql.elasticsearch.connector.connection;

import java.io.IOException;

import com.github.shyiko.mysql.binlog.BinaryLogClient;

public enum MysqlConnection {
	INSTANCE;
	
	private BinaryLogClient client;
	
	public BinaryLogClient getClient(String host, int port, String username, String password) {
		
		client = new BinaryLogClient(host, port, username, password);
		
		return client;
	}
	
	public void connect() {
		try {
			client.connect();
		} catch (IOException e) {}
	}

}
