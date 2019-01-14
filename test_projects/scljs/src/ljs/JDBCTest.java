import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCTest {
    public Connection getConnection() throws ClassNotFoundException,
            SQLException {
        // 加载MySQL的JDBC的驱动
        Class.forName("com.mysql.jdbc.Driver");

        String url = "jdbc:mysql://127.0.0.1:3307/test";
        String username = "zj";
        String password = "abc123456";

        // 创建与MySQL数据库的连接类的实例
        Connection conn = DriverManager.getConnection(url, username, password);
        System.out.println("Database connection established");
        return conn;
    }

    public static void main(String[] args) {
        JDBCTest t = new JDBCTest();
        try {
            Connection c = t.getConnection();
            if (c==null){
                System.out.println("失败");
            }else {
                System.out.println("成功");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
