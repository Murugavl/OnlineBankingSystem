package dao;

import db.DBConnection;
import java.sql.*;
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

    // Register user
    public boolean register(String name, String email, int pin, String accountNumber) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(name,email,password,account_number,balance) VALUES(?,?,?,?,0)");
        ps.setString(1, name);
        ps.setString(2, email);
        ps.setInt(3, pin);
        ps.setString(4, accountNumber);
        return ps.executeUpdate() > 0;
    }

    // Login user
    public int login(String email, int pin) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE email=? AND password=?");
        ps.setString(1, email);
        ps.setInt(2, pin);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("id");
        return -1;
    }

    // Verify current PIN before changing
    public boolean verifyPassword(int userId, int pin) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE id=? AND password=?");
        ps.setInt(1, userId);
        ps.setInt(2, pin);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    // Change PIN
    public boolean changePin(int userId, int newPin) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE users SET password=? WHERE id=?");
        ps.setInt(1, newPin);
        ps.setInt(2, userId);
        return ps.executeUpdate() > 0;
    }

    // Forgot PIN verification (check if email and account match)
    public boolean verifyEmailAndAccount(String email, String accountNumber) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE email=? AND account_number=?");
        ps.setString(1, email);
        ps.setString(2, accountNumber);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    // Reset PIN after verification
    public boolean resetPin(String email, String accountNumber, int newPin) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET password=? WHERE email=? AND account_number=?");
        ps.setInt(1, newPin);
        ps.setString(2, email);
        ps.setString(3, accountNumber);
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
            PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE users SET balance = balance - ? WHERE id=? AND balance >= ?");
            ps1.setDouble(1, amount);
            ps1.setInt(2, senderId);
            ps1.setDouble(3, amount);
            int senderUpdated = ps1.executeUpdate();

            if (senderUpdated == 0) {
                con.rollback();
                return false;
            }

            PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE users SET balance = balance + ? WHERE id=?");
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

    // Check if PIN is unique
    public boolean isPinUnique(int pin) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM users WHERE password=?");
        ps.setInt(1, pin);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) == 0;
        return false;
    }
}
