import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class GetConnection
{
    static{
        try {
            Class.forName("org.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection connection(DBDataSource dbDataSource) {
        Connection c = null;
        try {
            c = DriverManager.getConnection(dbDataSource.getUrl(), dbDataSource.getUserName(), dbDataSource.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public Connection connection(String url,String user,String password) {
        Connection c = null;
        try {
            c = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    public void closeConnection(Connection c) {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}