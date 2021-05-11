package utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtils {

    /**
     * Create the database connection
     * @param db_name - Assign the database name
     */
    public static Connection getConnection(String db_name) {
        Connection conn = null;
        try {
            InputStream is = DbUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");

            Properties pros = new Properties();
            pros.load(is);

            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty(db_name);
            String driver = pros.getProperty("driver");

            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException | IOException e) {
            System.out.println("Error happens while getting the database connection");
        }

        return conn;
    }
    /**
     * Close the database connection
     * @param conn - Assign the database connection that need to be closed
     */
    public static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error happens while closing the database connection");
        }
    }
}
