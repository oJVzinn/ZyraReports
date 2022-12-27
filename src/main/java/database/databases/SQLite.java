package database.databases;

import database.DataBase;
import database.interfaces.DatabaseInterface;

import java.sql.*;

public class SQLite extends DataBase implements DatabaseInterface<SQLite> {
    @Override
    public void setupDataBase() {}

    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {return null;}

    @Override
    public void createTable(String table) {}

    @Override
    public void closeConnection() {}
}
