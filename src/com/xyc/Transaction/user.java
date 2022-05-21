package com.xyc.Transaction;

/**
 * @auther xuyuchao
 * @create 2022-03-20-15:18
 */
public class user {
    private String user;
    private String password;
    private int balance;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "user{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public user(String user, String password, int balance) {
        this.user = user;
        this.password = password;
        this.balance = balance;
    }

    public user() {
    }
}
