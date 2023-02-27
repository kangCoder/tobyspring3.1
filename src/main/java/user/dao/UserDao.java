package user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import user.domain.User;

/*
 1-1-2. JDBC API의 기본적인 사용 방법을 따라 만든 클래스
 1-2. [관심사의 분리] 중복코드를 추출하고 추상 클래스로 만들어 기능을 확장한다. -> 서브클래스에서 추상메서드 구현하게 만들기 -> 템플릿 메서드 패턴
 1-3. [DAO의 확장] UserDao는 그저 데이터를 어떻게 쓸지만 신경쓰면 되지, 어떤 DB와 연결할 것인지는 신경쓸 필요가 없다. -> 관계설정 책임의 분리!
 1-7. [의존관계 주입] UserDao는 ConnectionMaker에 의존하고 있다. -> 커넥션을 구현한 구현클래스의 존재를 모르고 있다.
 1-8-3. [DataSource 인터페이스] DB 커넥션을 연결해주고 이것저것 처리해주는 DataSource 인터페이스가 이미 존재한다. 이것을 이용해보자.
 2-3-2. [테스트 결과의 일관성] 매 번 UserDaoTest 를 실행하기 위해서는 테이블 데이터를 삭제해줘야 하는 번거로움이 있다. 이것을 deleteAll() 로 해결해보자.
 */
/*
 전략 패턴의 구조로 보면 UserDao 의 메서드가 클라이언트, 내부 클래스가 전략, jdbcContextWithStatementStrategy()가 컨텍스트.
 컨텍스트는 다른 DAO에서도 사용할 수 있으므로 분리시키는 것이 좋다.
 */
public class UserDao {

    //수정자 메서드 DI 방식의 전형적인 형태 (@Setter 를 쓰면 편함.)
    private DataSource dataSource;
    private ConnectionMaker connectionMaker;
    private JdbcContext jdbcContext; //분리된 JdbcContext를 DI받아 사용.
    private JdbcTemplate jdbcTemplate;

    //수정자 메서드 DI인데, JdbcContext에 대한 생성, DI도 동시에 수행.
    public void setDataSource(DataSource dataSource) {
        this.jdbcContext = new JdbcContext();
        this.jdbcContext.setDataSource(dataSource); //의존 오브젝트 주입
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //생성자 메서드 DI 방식.
    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker; //어떤 커넥션을 맺을 지는 UserDao의 책임이 아니다. 그저 주는 대로 쓰면 된다.
    }

    //의존관계 검색: 의존관계를 주입받는 것이 아닌 스스로 IoC컨테이너에게 요청한다.
    //의존관계 검색의 경우 UserDao가 굳이 빈 오브젝트일 필요가 없다.
    public UserDao() {
        DaoFactory daoFactory = new DaoFactory(); //컨테이너에게 의존관계를 요청.
        this.connectionMaker = daoFactory.connectionMaker();
    }

    /**
     * add, get version1: DAO 에서 DB와의 연결과 쿼리문, 업데이트까지 모두 담당한다. add, get
     * version2: DB와의 연결을 가져오는 구문을 추상 메서드로 추출한다. add, get
     * version3: DB Connection을 제공하는 클래스를 구체적으로 알 필요없게 한다. UserDao는 그저 가져와서 쓰기만 한다.
     * version4: 전략 패턴을 적용. 로컬 클래스 사용.
     * version5: 템플릿/콜백 패턴을 적용. 콜백을 따로 분리.
     * version6: JdbcTemplate 사용.
     */
    public void add(final User user)  {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id=?",
                new Object[] {id},
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
        );
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        });
    }

    public void deleteAll() {
        this.jdbcTemplate.update(con -> con.prepareStatement("delete from users"));
    }

    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
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

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    //protected abstract PreparedStatement makeStatement(Connection c) throws SQLException;


}
