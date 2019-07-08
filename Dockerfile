FROM maven:3-jdk-8-alpine

# Copy existing application directory contents

COPY docker-entrypoint.sh /docker-entrypoint.sh

RUN chmod +x /docker-entrypoint.sh

COPY . /usr/src/elasticsearch

WORKDIR /usr/src/elasticsearch

RUN mvn install

RUN cp target/elasticsearch-connector-0.0.1-SNAPSHOT.one-jar.jar .

# COPY target/elasticsearch-connector-0.0.1-SNAPSHOT.one-jar.jar .

# ENTRYPOINT ["/docker-entrypoint.sh"]

# ENTRYPOINT ["java","-jar","elasticsearch-connector-0.0.1-SNAPSHOT.one-jar.jar","-mysql_host", "db","-mysql_port", "3306","-mysql_username", "root","-mysql_password", "root","-table_name", "businesses","-elasticsearch_host", "elasticsearch","-elasticsearch_scheme", "http","-elasticsearch_port", "9200","-file_mapping", "mapping.txt","-index_name", "pbp","-database_name", "databank","-reindex"]
