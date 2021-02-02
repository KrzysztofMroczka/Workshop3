package pl.coderslab.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class DbUtil {
    private static DataSource dataSource;

    public DbUtil() throws SQLException {
    }

    public static Connection getConnection() throws SQLException {
        return getInstance().getConnection();   }
    private static DataSource getInstance() {
        if (dataSource == null) {
            try {
                Context initContext = new InitialContext();
                Context envContext = (Context)initContext.lookup("java:/comp/env");
                dataSource = (DataSource)envContext.lookup("jdbc/users");
            } catch (NamingException e) { e.printStackTrace(); }
        }
        return dataSource;
    }

    Connection conn =
            DriverManager.getConnection("jdbc:mysql://localhost:3306/"
                            + "users?useSSL=false",
                    "root",
                    "coderslab");



    public static void main(String[] args) throws SQLException {
        Connection conn = getConnection();
    }
}

