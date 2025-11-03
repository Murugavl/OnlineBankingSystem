package dao;

import db.DBConnection;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class UserDAO {

    // Generate unique 16-digit account number
    public String generateAccountNumber() throws SQLException {
        Connection con = DBConnection.getConnection();
        Random random = new Random();
        String accountNumber;

        while (true) {
            long randomPart = (long) (random.nextDouble() * 1000000000000L);
            accountNumber = "2005" + String.format("%012d", randomPart);

            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE account_number=?");
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) break;
        }
        return accountNumber;
    }

    // Hash password using SHA-256
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Register user
    public boolean register(String name, String email, String password, String accountNumber) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO users(name,email,password,account_number,balance) VALUES(?,?,?,?,0)");
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setString(3, password);
        ps.setString(4, accountNumber);
        return ps.executeUpdate() > 0;
    }

    // Login user
    public int login(String email, String hashedPassword) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE email=? AND password=?");
        ps.setString(1, email);
        ps.setString(2, hashedPassword);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        return -1;
    }

    // Verify password before changing PIN
    public boolean verifyPassword(int userId, String hashedPassword) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE id=? AND password=?");
        ps.setInt(1, userId);
        ps.setString(2, hashedPassword);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    // Change PIN
    public boolean changePin(int userId, String newHashedPin) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE users SET password=? WHERE id=?");
        ps.setString(1, newHashedPin);
        ps.setInt(2, userId);
        return ps.executeUpdate() > 0;
    }

    // Close account
    public boolean closeAccount(int userId) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE id=?");
        ps.setInt(1, userId);
        return ps.executeUpdate() > 0;
    }

    // Get balance
    public double getBalance(int userId) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT balance FROM users WHERE id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getDouble("balance");
        return 0;
    }

    // Update balance
    public boolean updateBalance(int userId, double newBalance) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE users SET balance=? WHERE id=?");
        ps.setDouble(1, newBalance);
        ps.setInt(2, userId);
        return ps.executeUpdate() > 0;
    }

    // Get userId by account number
    public int getUserIdByAccountNumber(String accountNumber) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE account_number=?");
        ps.setString(1, accountNumber);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        return -1;
    }

    // Transfer money between users
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
                return false;
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
