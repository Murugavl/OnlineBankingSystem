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
            System.out.println("\n1. Register\n2. Login\n3. Forgot PIN\n4. Exit");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Enter Name: ");
                String name = sc.nextLine();
                System.out.print("Enter Email: ");
                String email = sc.nextLine();

                int pin;
                while (true) {
                    System.out.print("Enter a 4-digit PIN: ");
                    pin = sc.nextInt();
                    if (String.valueOf(pin).length() == 4) {
                        if (userDAO.isPinUnique(pin)) break;
                        else System.out.println("PIN already in use. Try a different one.");
                    } else {
                        System.out.println("PIN must be exactly 4 digits.");
                    }
                }

                String accountNumber = userDAO.generateAccountNumber();
                if (userDAO.register(name, email, pin, accountNumber)) {
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
                System.out.print("Enter PIN: ");
                int pin = sc.nextInt();

                int userId = userDAO.login(email, pin);
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
                            System.out.print("Enter old PIN: ");
                            int oldPin = sc.nextInt();
                            if (userDAO.verifyPassword(userId, oldPin)) {
                                int newPin;
                                while (true) {
                                    System.out.print("Enter new 4-digit PIN: ");
                                    newPin = sc.nextInt();
                                    if (String.valueOf(newPin).length() == 4) {
                                        if (userDAO.isPinUnique(newPin)) break;
                                        else System.out.println("PIN already in use. Try again.");
                                    } else System.out.println("PIN must be 4 digits.");
                                }
                                userDAO.changePin(userId, newPin);
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

            else if (choice == 3) {
                System.out.print("Enter your registered Email: ");
                String email = sc.nextLine();
                System.out.print("Enter your Account Number: ");
                String acc = sc.nextLine();

                if (userDAO.verifyEmailAndAccount(email, acc)) {
                    int newPin;
                    while (true) {
                        System.out.print("Enter new 4-digit PIN: ");
                        newPin = sc.nextInt();
                        if (String.valueOf(newPin).length() == 4) {
                            if (userDAO.isPinUnique(newPin)) break;
                            else System.out.println("PIN already in use. Try again.");
                        } else System.out.println("PIN must be exactly 4 digits.");
                    }
                    userDAO.resetPin(email, acc, newPin);
                    System.out.println("PIN reset successful!");
                } else {
                    System.out.println("Invalid email or account number!");
                }
            }

            else {
                System.out.println("Goodbye!");
                break;
            }
        }
        sc.close();
    }
}
