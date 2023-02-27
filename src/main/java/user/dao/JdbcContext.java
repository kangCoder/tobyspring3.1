package user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.Setter;

/*
전략 컨텍스트를 모든 DAO에서 사용할 수 있게 클래스로 따로 분리
 */
@Setter
public class JdbcContext {

    private DataSource dataSource;

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = this.dataSource.getConnection();
            ps = stmt.makePreparedStatement(c);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public void executeSql(final String query) throws SQLException {
        workWithStatementStrategy(
                c -> c.prepareStatement(query)
        );
    }

}
