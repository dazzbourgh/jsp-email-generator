package borisevich.emailgenerator.db;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.*;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * Created by Leonid on 23.08.2016.
 */

public class DBConnector {
    static final Logger LOGGER = Logger.getLogger(DBConnector.class.getName());
    // JDBC driver name and database URL
    static final String JDBC_DRIVER="com.mysql.jdbc.Driver";
    static final String DB_URL="jdbc:mysql://localhost/emails";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "qwerty123";

    private Connection connection;
    private ResultSet resultSet;
    private Statement statement;

    private static PoolProperties poolProperties = new PoolProperties();
    private static DataSource datasource;

    static {
        // Register JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
            LOGGER.info("Registered JDBC driver successfully");
        }catch(ClassNotFoundException e){
            LOGGER.info("Failed to register JDBC driver: " + e.getMessage());
        }
    }

    public DBConnector(){}

    public static void setPoolProperties(){
        poolProperties.setUrl(DB_URL);
        poolProperties.setDriverClassName(JDBC_DRIVER);
        poolProperties.setUsername(USER);
        poolProperties.setPassword(PASS);
        poolProperties.setJmxEnabled(true);
        poolProperties.setTestWhileIdle(false);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setTestOnReturn(false);
        poolProperties.setValidationInterval(30000);
        poolProperties.setTimeBetweenEvictionRunsMillis(30000);
        poolProperties.setMaxActive(10);
        poolProperties.setInitialSize(1);
        poolProperties.setMaxWait(10000);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setMinEvictableIdleTimeMillis(30000);
        poolProperties.setMinIdle(1);
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        datasource = new DataSource();
        datasource.setPoolProperties(poolProperties);
    }

    public ResultSet executeStatement(String sqlCommand) throws SQLException, NullPointerException{
        // Open a connection
        //connection = DriverManager.getConnection(DB_URL, USER, PASS);
        connection = datasource.getConnection();
        // Execute SQL query
        statement = connection.createStatement();
        resultSet = statement.executeQuery(sqlCommand);

        return resultSet;
    }

    public void close() throws SQLException{
        resultSet.close();
        statement.close();
        connection.close();
    }
}
