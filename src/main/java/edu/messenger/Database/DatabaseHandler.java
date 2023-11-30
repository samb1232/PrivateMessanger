package edu.messenger.Database;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class DatabaseHandler {
    private static Connection dbConnection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static final Lock lock = new ReentrantLock();

    private DatabaseHandler() {
    }

    public static void getDbConnection() throws ClassNotFoundException, SQLException {
        lock.lock();
        String connectionString = "jdbc:sqlite:TEST.s3db";
        Class.forName("org.sqlite.JDBC");
        dbConnection = DriverManager.getConnection(connectionString);
        statement = dbConnection.createStatement();
        System.out.println("База подключена");
        lock.unlock();
    }

    public static void createDatabase() throws SQLException {
        lock.lock();
        statement = dbConnection.createStatement();
        statement.execute("DROP TABLE if exists 'users'");
        statement.execute("DROP TABLE if exists 'chat'");
        statement.execute("CREATE TABLE if not exists 'users' "
                + "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'nickname' text, 'password' text, 'location', text);");
        statement.execute("CREATE TABLE if not exists 'chat' "
                + "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'user1' text, 'user2' text, 'string', text);");
        System.out.println("Базы данных созданы");
        lock.unlock();
    }

    public static void changeDialogue(String user1, String user2, String text) throws SQLException {
        lock.lock();
        if (user1.hashCode() > user2.hashCode()) {
            String temp = user1;
            user1 = user2;
            user2 = temp;
        }
        String dialogue = "";
        resultSet = statement.executeQuery("SELECT * FROM 'dialogue'");
        while (resultSet.next()) {
            String name1 = resultSet.getString("user1");
            String name2 = resultSet.getString("user2");
            if ((name1.equals(user1)) && (name2.equals(user2))) {
                dialogue = resultSet.getString("string") + "\n" + text;
            }
        }
        statement.execute("UPDATE 'chat' SET 'string' = '"
                + dialogue + "' WHERE user1 = '" + user1 + "' AND user2 = '" + user2 + "';");
        System.out.println("Диалог был изменен");
        lock.unlock();
    }

    public static boolean signUpDialogue(String user1, String user2, String text) throws SQLException {
        lock.lock();
        resultSet = statement.executeQuery("SELECT * FROM 'chat'");
        if (user1.hashCode() > user2.hashCode()) {
            String temp = user1;
            user1 = user2;
            user2 = temp;
        }
        while (resultSet.next()) {
            String name1 = resultSet.getString("user1");
            String name2 = resultSet.getString("user2");
            if ((name1.equals(user1)) && (name2.equals(user2))) {
                return false;
            }
        }
        statement.execute("INSERT INTO 'chat' ('user1', 'user2', 'string') "
                + "VALUES " + "('" + user1 + "', '" + user2 + "', '" + text + "'); ");
        System.out.println("Добавлен новый диалог");
        lock.unlock();
        return true;
    }

    public static boolean signUpUser(String nickName, String password, String location) throws SQLException {
        lock.lock();
        resultSet = statement.executeQuery("SELECT * FROM 'users'");
        while (resultSet.next()) {
            String name = resultSet.getString("nickname");
            if (nickName.equals(name)) {
                return false;
            }
        }
        statement.execute("INSERT INTO 'users' ('nickname', 'password', 'location') "
                + "VALUES " + "('" + nickName + "', '" + password + "', '" + location + "'); ");
        System.out.println("Добавлен новый пользователь");
        lock.unlock();
        return true;
    }

    public static boolean logIn(String nickName, String password) throws SQLException {
        lock.lock();
        resultSet = statement.executeQuery("SELECT * FROM 'users'");
        while (resultSet.next()) {
            String name = resultSet.getString("nickname");
            String pass = resultSet.getString("password");
            if (nickName.equals(name)) {
                if (password.equals(pass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void readDatabase() throws SQLException {
        resultSet = statement.executeQuery("SELECT * FROM 'users'");
        System.out.println("USERS");
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("nickname");
            String password = resultSet.getString("password");
            String location = resultSet.getString("location");
            System.out.println("ID = " + id);
            System.out.println("nickname = " + name);
            System.out.println("password = " + password);
            System.out.println("location = " + location);
            System.out.println();
        }
        resultSet = statement.executeQuery("SELECT * FROM 'chat'");
        System.out.println("DIALOGUES");
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("user1");
            String password = resultSet.getString("user2");
            String location = resultSet.getString("string");
            System.out.println("ID = " + id);
            System.out.println("user1 = " + name);
            System.out.println("user2 = " + password);
            System.out.println("dialogue = " + location);
            System.out.println();
        }
        System.out.println("Все выведено");
    }

    public static void closeDatabase() throws SQLException {
        lock.lock();
        dbConnection.close();
        statement.close();
        resultSet.close();
        System.out.println("Соединения закрыты");
        lock.unlock();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DatabaseHandler.getDbConnection();
        DatabaseHandler.createDatabase();
        DatabaseHandler.signUpUser("Test1", "Test1", "Test1");
        DatabaseHandler.signUpUser("Test2", "Test2", "Test2");
        DatabaseHandler.signUpUser("Test3", "Test3", "Test3");
        DatabaseHandler.signUpUser("Test1", "Test1", "Test1");
        DatabaseHandler.signUpDialogue("Test1", "Test2", "aksmdkla");
        DatabaseHandler.readDatabase();
        DatabaseHandler.signUpDialogue("Test2", "Test1", "12312k3");
        DatabaseHandler.readDatabase();
        DatabaseHandler.changeDialogue("Test2", "Test1", "912838190");
        DatabaseHandler.signUpDialogue("Test3", "Test1", "as.d;la.s");
        DatabaseHandler.readDatabase();
        DatabaseHandler.changeDialogue("Test1", "Test3", "TEST");
        DatabaseHandler.readDatabase();
        System.out.println(DatabaseHandler.logIn("Test2", "Test2"));
        DatabaseHandler.closeDatabase();
    }
}
