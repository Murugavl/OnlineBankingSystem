package model;

public class User
{
    private int id;
    private String name;
    private String email;
    private String password;
    private double balance;

    public User(int id, String name, String email, String password, double balance)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }
}
