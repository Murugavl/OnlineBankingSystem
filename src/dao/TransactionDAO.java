package dao;
import db.DBConnection;
import java.sql.*;
public class TransactionDAO
{
    public void recordTransaction(int userId, String type, double amount) throws SQLException
    {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO transactions(user_id, type, amount) VALUES(?,?,?)");
            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.executeUpdate();
    }
    public void recordTransfer(int senderId, int receiverId, double amount) throws SQLException {
        recordTransaction(senderId, "Transfer Sent", amount);
        recordTransaction(receiverId, "Transfer Received", amount);
    }

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



}
