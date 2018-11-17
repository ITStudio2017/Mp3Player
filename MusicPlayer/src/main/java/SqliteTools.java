import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteTools {
    static Connection conn = null;
    static Statement stmt = null;
    static String url = "jdbc:sqlite:"+ System.getProperty("user.dir") + "/SQLite3/MyMusic.db";
    public static void executor(String sql) {
        try{
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("database connected error");
        }
        try{
            stmt.executeUpdate(sql);
            stmt.close();
            conn.commit();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("sql statement error");
        }
    }
    public static void connect() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
