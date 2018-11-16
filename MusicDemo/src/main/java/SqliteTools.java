import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteTools {


    public static void connect() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection conn = null;
        String url = "jdbc:sqlite:"+ System.getProperty("user.dir") + "/SQLite3/MyMusic.db";
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        connect();
    }
}
