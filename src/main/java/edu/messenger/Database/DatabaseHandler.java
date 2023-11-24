package edu.messenger.Database;

import java.sql.*;

public class DatabaseHandler extends Configs {
    private static Connection dbConnection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:sqlite:TEST.s3db";
        Class.forName("org.sqlite.JDBC");
        dbConnection = DriverManager.getConnection(connectionString);
        statement = dbConnection.createStatement();
        System.out.println("База подключена");
    }

    public static void createDatabase() throws SQLException {
        statement = dbConnection.createStatement();
        statement.execute("DROP TABLE if exists 'users'");
        statement.execute("CREATE TABLE if not exists 'users' "
                + "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'nickname' text, 'password' text, 'location', text);");
        System.out.println("База данных создана или уже существует");
    }

    public static void signUpUser(String nickName, String password, String location) throws SQLException, ClassNotFoundException {
        statement.execute("INSERT INTO 'users' ('nickname', 'password', 'location') "
                + "VALUES " + "('" + nickName + "', '" + password + "', '" + location + "'); ");
        System.out.println("Таблица заполнена");
        System.out.println("Добавлен новый пользователь");
        readDatabase();
    }

    public static void readDatabase() throws ClassNotFoundException, SQLException {
        resultSet = statement.executeQuery("SELECT * FROM users");

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

        System.out.println("Таблица выведена");
    }

    public static void closeDatabase() throws SQLException {
        dbConnection.close();
        statement.close();
        resultSet.close();
        System.out.println("Соединения закрыты");
    }

}
