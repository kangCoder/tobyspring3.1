package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatement 를 만드는 전략 인터페이스
 * 람다 표현식 -> 하나의 메서드만을 가지고 있는 인터페이스
 */
public interface StatementStrategy {

    PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
