package main;
import dao.*;
import java.util.*;

public class BankingApp {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        TransactionDAO transDAO = new TransactionDAO();

        System.out.println("WELCOME TO VEL's BANK");

        while (true) {
            System.out.println("\n1. Register\n2. Login\n3. Exit");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Enter Name: ");
                String name = sc.nextLine();
                System.out.print("Enter Email: ");
                String email = sc.nextLine();
                System.out.print("Enter Pin: ");
                String pass = sc.nextLine();

                String hashedPass = userDAO.hashPassword(pass);
                String accountNumber = userDAO.generateAccountNumber();

                if (userDAO.register(name, email, hashedPass, accountNumber)) {
                    System.out.println("\nRegistration Successful!");
                    System.out.println("Your Account Number: " + accountNumber);
                    System.out.println("\n1. Back to Login\n2. Exit");
                    int next = sc.nextInt();
                    sc.nextLine();
                    if (next == 1) continue;
                    else break;
                } else {
                    System.out.println("Registration failed! Try again.");
                }
            }

            else if (choice == 2) {
                System.out.print("Enter Email: ");
                String email = sc.nextLine();
                System.out.print("Enter Pin: ");
                String pass = sc.nextLine();

                String hashedPass = userDAO.hashPassword(pass);
                int userId = userDAO.login(email, hashedPass);

                if (userId != -1) {
                    System.out.println("Login Successful!");

                    while (true) {
                        System.out.println("\n1. View Balance\n2. Deposit\n3. Withdraw\n4. Transfer\n5. Mini Statement\n6. Change PIN\n7. Close Account\n8. Logout");
                        int opt = sc.nextInt();

                        if (opt == 1)
                            System.out.println("Balance: " + userDAO.getBalance(userId));

                        else if (opt == 2) {
                            System.out.print("Enter amount: ");
                            double amt = sc.nextDouble();
                            double bal = userDAO.getBalance(userId) + amt;
                            userDAO.updateBalance(userId, bal);
                            transDAO.recordTransaction(userId, "Deposit", amt);
                            System.out.println("Deposit successful!");
                        }

                        else if (opt == 3) {
                            System.out.print("Enter amount: ");
                            double amt = sc.nextDouble();
                            double bal = userDAO.getBalance(userId);
                            if (amt <= bal) {
                                userDAO.updateBalance(userId, bal - amt);
                                transDAO.recordTransaction(userId, "Withdraw", amt);
                                System.out.println("Withdraw successful!");
                            } else System.out.println("Insufficient Balance!");
                        }

                        else if (opt == 4) {
                            sc.nextLine();
                            System.out.print("Enter receiver account number: ");
                            String receiverAcc = sc.nextLine();
                            int receiverId = userDAO.getUserIdByAccountNumber(receiverAcc);
                            if (receiverId == -1) {
                                System.out.println("Receiver not found!");
                                continue;
                            }
                            System.out.print("Enter amount to transfer: ");
                            double amt = sc.nextDouble();
                            if (userDAO.transferMoney(userId, receiverId, amt)) {
                                transDAO.recordTransfer(userId, receiverId, amt);
                                System.out.println("Transfer successful!");
                            } else System.out.println("Transfer failed! Insufficient balance.");
                        }

                        else if (opt == 5)
                            transDAO.showMiniStatement(userId);

                        else if (opt == 6) {
                            sc.nextLine();
                            System.out.print("Enter old PIN: ");
                            String oldPin = sc.nextLine();
                            String hashedOld = userDAO.hashPassword(oldPin);

                            if (userDAO.verifyPassword(userId, hashedOld)) {
                                System.out.print("Enter new PIN: ");
                                String newPin = sc.nextLine();
                                String hashedNew = userDAO.hashPassword(newPin);
                                userDAO.changePin(userId, hashedNew);
                                System.out.println("PIN changed successfully!");
                            } else {
                                System.out.println("Incorrect old PIN!");
                            }
                        }

                        else if (opt == 7) {
                            sc.nextLine();
                            System.out.print("Are you sure you want to close your account? (yes/no): ");
                            String confirm = sc.nextLine();
                            if (confirm.equalsIgnoreCase("yes")) {
                                userDAO.closeAccount(userId);
                                transDAO.deleteUserTransactions(userId);
                                System.out.println("Account closed successfully. Goodbye!");
                                break;
                            }
                        }

                        else break;
                    }
                } else System.out.println("Invalid credentials!");
            }

            else {
                System.out.println("Goodbye!");
                break;
            }
        }
        sc.close();
    }
}
