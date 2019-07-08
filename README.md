### Elasticsearch Replication

This program replicates the content of mysql table to elasticsearch

### Requirements

JAVA 1.8+

MAVEN  

ELASTICSEARCH v6.6.2

### Build

Go to

`cd elasticsearch-sync-connector`

Run

`maven clean install`

Copy

`cp target/elasticsearch-connector-0.0.1-SNAPSHOT.one-jar.jar .`

### MySql Settings

Edit mysql configuration, then add this lines

```
[mysqld]
server-id=1
expire_logs_days=10
max_binlog_size=100M
binlog-format=row
log-bin = mysql-bin
```

The user, you plan to use for the BinaryLogClient, must have REPLICATION SLAVE privilege. To get binlog filename and position, he must be granted at least one of REPLICATION CLIENT or SUPER as well. To get table info of mysql server, he also need SELECT privilege on information_schema.COLUMNS. We suggest grant below privileges to the user:

`GRANT REPLICATION SLAVE, REPLICATION CLIENT, SELECT ON *.* TO 'user'@'host'`



### Usage

Set -reindex parameter to true to create index and index data to elasticsearch

`java -jar elasticsearch-connector-0.0.1-SNAPSHOT.one-jar.jar -mysql_host localhost -mysql_port 3306 -mysql_username root -mysql_password root -table_name businesses -elasticsearch_host elasticsearch-dev -elasticsearch_scheme http -elasticsearch_port 9200 -reindex true -file_mapping mapping.txt -index_name pbp -database_name databank`

Set -reindex to false to listen to mysql events

`java -jar elasticsearch-connector-0.0.1-SNAPSHOT.one-jar.jar -mysql_host localhost -mysql_port 3306 -mysql_username root -mysql_password root -table_name businesses -elasticsearch_host elasticsearch-dev -elasticsearch_scheme http -elasticsearch_port 9200 -reindex false -index_name pbp `


### Parameters


```
usage: java -jar elasticsearch-connector-0.0.1-SNAPSHOT.one-jar.jar [options here]

 -database_name,--databaseName <arg>                 Name of the database
 -elasticsearch_host,--elastisearchHost <arg>        Elasticsearch Host
                                                     Connection
 -elasticsearch_port,--elasticsearchPort <arg>       Elasticsearch Port
                                                     Connection
 -elasticsearch_scheme,--elasticSearchScheme <arg>   HTTP or HTTPS
 -file_mapping,--fileMapping <arg>                   File that contains
                                                     the mapping
 -index_name,--indexName <arg>                       Index name
 -mysql_host,--mysqlHost <arg>                       Mysql Host
 -mysql_password,--mysqlPassword <arg>               Mysql Password
 -mysql_port,--mysqlPort <arg>                       Mysql Port
 -mysql_username,--mysqlUsername <arg>               Mysql Username
 -reindex,--reindex <arg>                            Set to true to reindex again to elasticsearch(this will delete and create the index again)
-table_name,--tableName <arg>                        Table name
```
### Docker Configuration

Build the image

`sudo docker-compose build`

Run the container

`sudo docker-compose run -d --rm --name elasticsearch-connector elasticsearch-connector reindex`
