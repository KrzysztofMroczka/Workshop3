package pl.coderslab.utils;


import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static final String CREATE_USER_QUERY = "INSERT INTO users(username, email, password) VALUES (?,?,?)";
    public static final String READ_USER_ON_ID_QUERY = "SELECT id, username, email, password FROM users WHERE id = ?";
    public static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, username = ?, password = ? WHERE id = ?";
    public static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    public static final String FIND_ALL_QUERY = "SELECT id, username, email, password FROM users WHERE column LIKE ?";
    public static final String[] COLUMNS = {"id", "username", "email", "password"};


    public static User create(User user) {
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashPassword(user.getPassword()));

            statement.executeUpdate();

            ResultSet result = statement.getGeneratedKeys();
            if (result.next()) {
                int id = result.getInt(1);
                user.setId(id);
                System.out.println("Inserted ID for " + user.getUserName() + " is: " + id);
                return user;
            }

        } catch (SQLException e) {
            System.out.println("SQLException in method create()");
//            e.printStackTrace();
        }
        return null;
    }


    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static User read(int userId) {

        try (Connection connection = DbUtil.getConnection()) {
            User user = null;
            PreparedStatement statement = connection.prepareStatement(READ_USER_ON_ID_QUERY);
            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                user = getUser(result);
            }

            return user;

        } catch (SQLException e) {
            System.out.println("SQLException in method read()");
//            e.printStackTrace();
        }
        return null;
    }

    //creating pl.coderslab.entity.User object from ResultSet
    private static User getUser(ResultSet result) throws SQLException {
        User user;
        int id = result.getInt(1);
        String username = result.getString(2);
        String email = result.getString(3);
        String password = result.getString(4);

        user = new User(username, email, password);
        user.setId(id);
        return user;
    }


    public static boolean update(User user) {
/*        pl.coderslab.entity.User oldUser = read(user.getId());
        if (user.equals(oldUser)) {
            return;
        }
*/
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(UPDATE_USER_QUERY);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUserName());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.setInt(4, user.getId());

            if (statement.executeUpdate() > 0) {
                return true;
            }

        } catch (SQLException e) {
            System.out.println("SQLException caught in method update()");
        }
        return false;
    }

    public static boolean delete(int userId) {
/*        if (read(userId) == null) {
            System.out.println("User under id " + userId + " doesn't exist. Nothing to delete.");
            return false;
        }
*/

        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(DELETE_USER_QUERY);
            statement.setInt(1, userId);
            statement.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("SQLException caught in method delete()");
        }

        return false;
    }

    public static List<User> findAll() {
        return findAll("id", "%");
    }

    public static List<User> findAll(String column, String question) {
        //checking if String column is a name of actual column
        boolean wrongColumn = true;
        for (String col : COLUMNS) {
            if (col.equals(column)) {
                wrongColumn = false;
                break;
            }
        }
        if (wrongColumn) {
            return null;
        }

        //getting the results
        List<User> users = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY.replace("column", column));
            statement.setString(1, question);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                users.add(getUser(result));
            }

            return users;

        } catch (SQLException e) {
            System.out.println("SQLException caught in method findAll()");
        }

        return null;
    }

}
