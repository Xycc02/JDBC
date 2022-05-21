package com.xyc.PreparedStatementQueryTest;

/**
 * @auther xuyuchao
 * @create 2022-03-14-22:06
 */
public class userTable {
    private String user;
    private String password;
    private int balance;

    public userTable(String user, String password, int balance) {
        this.user = user;
        this.password = password;
        this.balance = balance;
    }

    public userTable() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    @Override
    public String toString() {
        return "userTable{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                '}';
    }
}
