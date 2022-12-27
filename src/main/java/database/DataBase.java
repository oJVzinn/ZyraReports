package database;

import com.zyramc.ojvzinn.reports.Main;
import database.databases.MySQL;
import database.databases.SQLite;
import database.interfaces.DatabaseInterface;

public abstract class DataBase {

    private static DatabaseInterface<? extends DataBase> databse;

    public static void setupDataBases(DataTypes dataTypes, Main main) {
        if (dataTypes.equals(DataTypes.MYSQL)) {
            MySQL mySQL = new MySQL(main);
            mySQL.setupDataBase();
            mySQL.createDefaultTables();
            databse = mySQL;
        } else {
            SQLite sqLite = new SQLite();
            sqLite.setupDataBase();
            databse = sqLite;
        }

        main.getLogger().info("Você escolheu a database do tipo: " + dataTypes.getName());
    }

    @SuppressWarnings("unchecked")
    public static <T extends DataBase> T getDatabase(Class<T> databaseClass) {
        return databse != null && databaseClass.isAssignableFrom(databse.getClass()) ? (T) databse : null;
    }
}
