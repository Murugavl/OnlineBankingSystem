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
}
