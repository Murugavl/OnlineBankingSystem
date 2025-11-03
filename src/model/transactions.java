package model;

import java.time.LocalDateTime;

public class transactions
{
    private int id;
    private int userId;
    private String type;
    private double amount;
    private LocalDateTime date;

    public transactions(int id, int userId, String type, double amount, LocalDateTime date)
    {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    // Getters only
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
}
