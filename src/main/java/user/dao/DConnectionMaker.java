package user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 1-3. [DAO의 확장]
 * UserDao와 DB Connection을 만드는 것이 서로 긴밀하게 연결되지 않도록 분리한다.
 */
public class DConnectionMaker implements ConnectionMaker {

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/tobyspring", "root", "11111111");
    }
}
