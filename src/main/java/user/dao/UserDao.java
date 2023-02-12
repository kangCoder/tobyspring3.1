package user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import user.domain.User;

/**
 * 1-1-2. JDBC API의 기본적인 사용 방법을 따라 만든 클래스
 * 1-2. [관심사의 분리] 중복코드를 추출하고 추상 클래스로 만들어 기능을 확장한다. -> 서브클래스에서 추상메서드 구현하게 만들기 -> 템플릿 메서드 패턴
 * 1-3. [DAO의 확장] UserDao는 그저 데이터를 어떻게 쓸지만 신경쓰면 되지, 어떤 DB와 연결할 것인지는 신경쓸 필요가 없다. -> 관계설정 책임의 분리!
 * 1-7. [의존관계 주입] UserDao는 ConnectionMaker에 의존하고 있다. -> 커넥션을 구현한 구현클래스의 존재를 모르고 있다.
 * 1-8-3. [DataSource 인터페이스] DB 커넥션을 연결해주고 이것저것 처리해주는 DataSource 인터페이스가 이미 존재한다. 이것을 이용해보자.
 */
public class UserDao {

    //수정자 메서드 DI 방식의 전형적인 형태 (@Setter 를 쓰면 편함.)
    private DataSource dataSource;
    private ConnectionMaker connectionMaker;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
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
     * add, get version1: DAO 에서 DB와의 연결과 쿼리문, 업데이트까지 모두 담당한다. add, get version2: DB와의 연결을 가져오는 구문을 추상 메서드로 추출한다. add, get
     * version3: DB Connection을 제공하는 클래스를 구체적으로 알 필요없게 한다. UserDao는 그저 가져와서 쓰기만 한다.
     */
    public void add(User user) throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tobyspring", "root", "11111111");
//        Connection c = connectionMaker.makeConnection(); //어떤 커넥션 메이커인지 알 필요가 없다.
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?, ?, ?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/tobyspring", "root", "11111111");
//        Connection c = connectionMaker.makeConnection();
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select * from users where id=?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }

    /**
     * 1-2. 중복 코드를 메서드로 추출한다.
     */
    //public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
}
