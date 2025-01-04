package com.adl.et.telco.testautomation.databaseclient;

import com.adl.et.telco.testautomation.configurations.DataExtractor;
import com.adl.et.telco.testautomation.configurations.xmlmappers.ConfigServiceData;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
//@PropertySource({"classpath:application.properties"})
public class DBConfig {

    public static final Logger logger = LoggerFactory.getLogger(DBConfig.class);

    private static ConfigServiceData configServiceData;

    static {
        try {
            configServiceData = DataExtractor.getConfigDataService();
        } catch (Exception ex) {
            logger.error("error occurred while trying to initialize configServiceData");
        }
    }

    public DataSource ccbsDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(configServiceData.getDbProperty("ccbs-driver"));
        dataSource.setUrl(configServiceData.getDbProperty("ccbs-url"));
        dataSource.setUsername(configServiceData.getDbProperty("ccbs-username"));
        dataSource.setPassword(configServiceData.getDbProperty("ccbs-password"));
        logger.debug("Returning datasource " + dataSource);

        return dataSource;
    }

    public DataSource jdbcDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(configServiceData.getDbProperty("jdbc-driver"));
        dataSource.setUrl(configServiceData.getDbProperty("jdbc-url"));
        dataSource.setUsername(configServiceData.getDbProperty("jdbc-username"));
        dataSource.setPassword(configServiceData.getDbProperty("jdbc-password"));
        logger.debug("Returning datasource " + dataSource);

        return dataSource;
    }

    public JdbcTemplate initJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public MongoClient ccbMongo() {

        MongoClient client = null;

        try {
            ConnectionString connectionString = new ConnectionString(configServiceData.getDbProperty("ccbs-mongo-url"));
            logger.info("connectionString = {} ",connectionString);

            MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString).applyToSslSettings(block -> {
                        block.invalidHostNameAllowed(true);
                        block.enabled(true);
                    })
                    .build();

            client = MongoClients.create(mongoClientSettings);

            logger.info("ccbMongo created " + client);

        } catch (Exception ex) {
            logger.error("Error while creating mongo connection " + ex);
        }
        return client;
    }

    public MongoTemplate initMongoTemplate(MongoClient client, String dbName) {
        return new MongoTemplate(client, dbName);
    }
}
