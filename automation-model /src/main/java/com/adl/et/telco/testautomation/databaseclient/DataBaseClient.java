package com.adl.et.telco.testautomation.databaseclient;

import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseClient {

	public static final Logger logger = LoggerFactory.getLogger(DataBaseClient.class);


    /**
     * This method is used to create connection
     *
     * @param jdbcurl
     * @param dbname
     * @param dbdriver
     * @param dbusername
     * @param dbpassword
     * @return connection
     */
    public Connection setConnection(String jdbcurl,String dbname,String dbdriver,String dbusername,String dbpassword) throws SQLException {

        Connection conn = null;
        String url = jdbcurl;
        String dbName = dbname;
        String driver = dbdriver;
        String userName = dbusername;
        String password = dbpassword;

        try
        {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url+dbName,userName,password);
            logger.debug("Connected");
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }

        return conn;
    }



    /**
     * This method is used to extract data set from DB
     *
     * @param con
     * @param query
     * @return resultset
     */
    @Step
    public static ResultSet getDbQuery(Connection con, String query) throws SQLException {

        Connection conn = null;
        Statement stmt = null;

        ResultSet resultSet = null;

        stmt = conn.createStatement();
        resultSet = stmt.executeQuery(query);

        return resultSet;
    }

}
