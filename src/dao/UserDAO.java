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

    public int getUserIdByEmail(String email) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        return -1;
    }

    public boolean transferMoney(int senderId, int receiverId, double amount) throws SQLException {
        Connection con = DBConnection.getConnection();
        con.setAutoCommit(false);
        try {
            PreparedStatement ps1 = con.prepareStatement("UPDATE users SET balance = balance - ? WHERE id=? AND balance >= ?");
            ps1.setDouble(1, amount);
            ps1.setInt(2, senderId);
            ps1.setDouble(3, amount);
            int senderUpdated = ps1.executeUpdate();

            if (senderUpdated == 0) {
                con.rollback();
                return false; // insufficient funds
            }

            PreparedStatement ps2 = con.prepareStatement("UPDATE users SET balance = balance + ? WHERE id=?");
            ps2.setDouble(1, amount);
            ps2.setInt(2, receiverId);
            ps2.executeUpdate();

            con.commit();
            return true;
        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

}
