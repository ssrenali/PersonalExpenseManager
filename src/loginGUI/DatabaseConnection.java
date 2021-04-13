package loginGUI;

import java.sql.Connection;
import java.sql.DriverManager;



/**
 * DatabaseConnection class creates a connection with the Personal Expense Manager Java
 * program to the MySQL database
 *
 * @author Serena Li
 * @since 2021-04-13
 */
public class DatabaseConnection {

    public static Connection databaseLink;


    /**
     * gets MySQL connection from localhost database and returns as a Connection link.
     * @return databaseLink
     */
    public static Connection getConnection() {
        String databaseName = "pem";
        String databaseUser = "ssrenali";
        String databasePassword = "94080059qQ";
        String url = "jdbc:mysql://localhost/" + databaseName;

        try {
            // register mySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // create connection using driver manager
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;

    }


}
