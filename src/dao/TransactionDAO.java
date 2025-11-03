package dao;

import db.DBConnection;
import java.sql.*;

public class TransactionDAO {

    // Record deposit/withdraw/transfer transactions
    public void recordTransaction(int userId, String type, double amount) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO transactions(user_id, type, amount, date) VALUES(?,?,?,NOW())");
        ps.setInt(1, userId);
        ps.setString(2, type);
        ps.setDouble(3, amount);
        ps.executeUpdate();
    }

    // Record both sender and receiver sides for transfer
    public void recordTransfer(int senderId, int receiverId, double amount) throws SQLException {
        recordTransaction(senderId, "Transfer Sent", amount);
        recordTransaction(receiverId, "Transfer Received", amount);
    }

    // Display latest 5 transactions (in ascending order by date)
    public void showMiniStatement(int userId) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT type, amount, date FROM transactions WHERE user_id=? ORDER BY date ASC LIMIT 5");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        System.out.println("\n--- Mini Statement ---");
        while (rs.next()) {
            System.out.println(rs.getString("date") + " | " +
                    rs.getString("type") + " | " +
                    rs.getDouble("amount"));
        }
    }

    // Delete all transactions of a user (used during account closure)
    public void deleteUserTransactions(int userId) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM transactions WHERE user_id=?");
        ps.setInt(1, userId);
        ps.executeUpdate();
    }
}
