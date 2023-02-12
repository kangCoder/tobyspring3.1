//package user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 1-2. [관심사의 분리] 서브클래스와 추상메서드의 구현으로 DB Connection의 확장.
 */
//public class DUserDao extends UserDao{
//
//    @Override
//    public Connection getConnection() throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        return DriverManager.getConnection("jdbc:mysql://localhost:3306/tobyspring", "root", "11111111");
//    }
//}
