package main;
import dao.*;
import java.util.*;

public class BankingApp
{
    public static void main(String[] args) throws Exception
    {
        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        TransactionDAO transDAO = new TransactionDAO();

        System.out.println("1. Register\n2. Login");
        int choice = sc.nextInt(); sc.nextLine();

        int userId = -1;
        if (choice == 1)
        {
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine();
            if (userDAO.register(name, email, pass))
                System.out.println("Registration Successful!");
        }
        else
        {
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine();
            userId = userDAO.login(email, pass);
            if (userId != -1)
            {
                System.out.println("Login Successful!");
                while (true)
                {
                    System.out.println("\n1. View Balance\n2. Deposit\n3. Withdraw\n4. Transfer\n5. Exit");
                    int opt = sc.nextInt();
                    if (opt == 1)
                        System.out.println("Balance: " + userDAO.getBalance(userId));
                    else if (opt == 2)
                    {
                        System.out.print("Enter amount: ");
                        double amt = sc.nextDouble();
                        double bal = userDAO.getBalance(userId) + amt;
                        userDAO.updateBalance(userId, bal);
                        transDAO.recordTransaction(userId, "Deposit", amt);
                    }
                    else if (opt == 3)
                    {
                        System.out.print("Enter amount: ");
                        double amt = sc.nextDouble();
                        double bal = userDAO.getBalance(userId);
                        if (amt <= bal)
                        {
                            userDAO.updateBalance(userId, bal - amt);
                            transDAO.recordTransaction(userId, "Withdraw", amt);
                        } else System.out.println("Insufficient Balance!");
                    }
                    else if (opt == 4)
                    {
                        sc.nextLine();
                        System.out.print("Enter receiver email: ");
                        String receiverEmail = sc.nextLine();
                        int receiverId = userDAO.getUserIdByEmail(receiverEmail);
                        if (receiverId == -1)
                        {
                            System.out.println("Receiver not found!");
                            continue;
                        }
                        System.out.print("Enter amount to transfer: ");
                        double amt = sc.nextDouble();
                        if (userDAO.transferMoney(userId, receiverId, amt))
                        {
                            transDAO.recordTransfer(userId, receiverId, amt);
                            System.out.println("Transfer successful!");
                        }
                        else
                            System.out.println("Transfer failed! Insufficient balance.");
                    }
                    else break;
                }
            }
            else System.out.println("Invalid credentials!");
        }
    }
}
