package constructpro.DAO;

import constructpro.DTO.User;
import constructpro.Database.ConnectionEstablish;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Locale;
import java.util.Vector;

public class UserDAO {
    Connection conn;
    PreparedStatement prepStatement;
    Statement statement;
    ResultSet resultSet;

    public UserDAO() {
        try {
            conn = new ConnectionEstablish().getConn();
            statement = conn.createStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addUserDAO(User userDTO, String userType) {
        try {
            String query = "SELECT * FROM users WHERE fullName=? AND location=? AND phone=? AND usertype=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, userDTO.getFullName());
            prepStatement.setString(2, userDTO.getLocation());
            prepStatement.setString(3, userDTO.getPhone());
            prepStatement.setString(4, userDTO.getUserType());
            resultSet = prepStatement.executeQuery();

            if (resultSet.next()) {
                JOptionPane.showMessageDialog(null, "User already exists");
            } else {
                addFunction(userDTO, userType);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addFunction(User userDTO, String userType) {
        try {
            String query = "INSERT INTO users (fullName, location, phone, username, password, usertype) VALUES (?, ?, ?, ?, ?, ?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, userDTO.getFullName());
            prepStatement.setString(2, userDTO.getLocation());
            prepStatement.setString(3, userDTO.getPhone());
            prepStatement.setString(4, userDTO.getUserName());
            prepStatement.setString(5, userDTO.getPassword()); // Plain text
            prepStatement.setString(6, userDTO.getUserType());
            prepStatement.executeUpdate();

            String msg = userType.equals("ADMINISTRATOR") ? "New administrator added." : "New employee added.";
            JOptionPane.showMessageDialog(null, msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void editUserDAO(User userDTO) {
        try {
            String query = "UPDATE users SET fullName=?, location=?, phone=?, usertype=? WHERE username=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, userDTO.getFullName());
            prepStatement.setString(2, userDTO.getLocation());
            prepStatement.setString(3, userDTO.getPhone());
            prepStatement.setString(4, userDTO.getUserType());
            prepStatement.setString(5, userDTO.getUserName());
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Updated Successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUserDAO(String username) {
        try {
            String query = "DELETE FROM users WHERE username=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, username);
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "User Deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getQueryResult() {
        try {
            String query = "SELECT * FROM users";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getUserDAO(String username) {
        try {
            String query = "SELECT fullName,username,location,phone,passWord,usertype FROM users WHERE username=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, username);
            resultSet = prepStatement.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet searchUsers(String searchTerm) {
        try {
            String query = "SELECT * FROM users WHERE fullName LIKE ? OR username LIKE ?";
            prepStatement = conn.prepareStatement(query);
            String term = "%" + searchTerm + "%";
            prepStatement.setString(1, term);
            prepStatement.setString(2, term);
            resultSet = prepStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void getFullName(User userDTO, String username) {
        try {
            String query = "SELECT fullName FROM users WHERE username=? LIMIT 1";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, username);
            resultSet = prepStatement.executeQuery();
            if (resultSet.next()) {
                userDTO.setFullName(resultSet.getString("fullName"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet getUserLogsDAO() {
        try {
            String query = "SELECT users.fullName, userlogs.username, in_time, out_time " +
                    "FROM userLogs INNER JOIN users ON userLogs.username = users.username";
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
        }
        return resultSet;
    }

    public void addUserLogin(User userDTO) {
        try {
            String query = "INSERT INTO userLogs (username, in_time, out_time) VALUES (?, ?, ?)";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, userDTO.getUserName());
            prepStatement.setString(2, userDTO.getInTime());
            prepStatement.setString(3, userDTO.getOutTime());
            prepStatement.executeUpdate();
        } catch (SQLException e) {
        }
    }

    public ResultSet getPassDAO(String username, String password) {
        try {
            String query = "SELECT password FROM users WHERE username=? AND password=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, username);
            prepStatement.setString(2, password);
            resultSet = prepStatement.executeQuery();
        } catch (SQLException ex) {
        }
        return resultSet;
    }

    public void changePass(String username, String password) {
        try {
            String query = "UPDATE users SET password=? WHERE username=?";
            prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, password);
            prepStatement.setString(2, username);
            prepStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Password has been changed.");
        } catch (SQLException ex) {
        }
    }

    public DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int colCount = metaData.getColumnCount();

        for (int col = 1; col <= colCount; col++) {
            columnNames.add(metaData.getColumnName(col).toUpperCase(Locale.ROOT));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (resultSet.next()) {
            Vector<Object> row = new Vector<>();
            for (int col = 1; col <= colCount; col++) {
                row.add(resultSet.getObject(col));
            }
            data.add(row);
        }
        return new DefaultTableModel(data, columnNames);
    }
}