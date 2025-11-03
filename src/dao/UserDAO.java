package dao;

import db.DBConnection;

import java.sql.*;

public class UserDAO
{
    public boolean register(String name, String email, String password) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO users(name,email,password,balance) VALUES(?,?,?,0)");
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, password);
        return ps.executeUpdate() > 0;
    }

    public int login(String email, String password) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE email=? AND password=?");
        ps.setString(1, email);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        return -1;
    }

    public double getBalance(int userId) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT balance FROM users WHERE id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDouble("balance");
        return 0;
    }

    public boolean updateBalance(int userId, double newBalance) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE users SET balance=? WHERE id=?");
        ps.setDouble(1, newBalance);
        ps.setInt(2, userId);
        return ps.executeUpdate() > 0;
    }
}
