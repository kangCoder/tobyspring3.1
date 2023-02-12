package user.dao;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * 1-4. [제어의 역전] 팩토리: 객체의 생성 방법을 결정하고 만들어진 오브젝트를 돌려준다. 오브젝트를 생성하는 쪽과 사용하는 쪽의 역할과 책임을 분리하기 위함.
 * IoC: ConnectionMaker 구현클래스를 결정하고 오브젝트를 만드는 제어권을 UserDao -> DaoFactory로 넘겼다.
 * 그렇게 되면서 UserDao 또한 DaoFactory에 의해 수동적으로 만들어지게됨.
 * 즉 필요한 구현체를 생성하는 책임(제어)을 DaoFactory 에게 떠넘겼다. (제어의 역전)
 */

/**
 * 1-5. [스프링의 IoC]
 * 스프링의 강력한 기능 중 하나는 빈 팩토리(애플리케이션 컨텍스트).
 * 스프링 빈: 스프링 컨테이너가 IoC를 부여하는 오브젝트. 즉 객체의 생성, 소멸 등 객체 관리를 스프링 컨테이너가 대신 해주는 오브젝트.
 */

/**
 * 1-8-3. [DataSource 인터페이스 사용]
 * DataSource 구현체를 구현해보자.
 */
@Configuration //스프링이 빈 팩토리를 위한 오브젝트 설정을 담다하는 클래스라고 인식
public class DaoFactory {

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        //프로퍼티 값의 주입
        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost:3306/tobyspring");
        dataSource.setUsername("root");
        dataSource.setPassword("11111111");

        return dataSource;
    }

    @Bean //오브젝트 생성을 담당하는 IoC 용 메서드라는 표시
    public UserDao userDao() {
        //수정자 메서드 DI 방식을 통한 빈 생성.
        UserDao userDao = new UserDao();
        //userDao.setConnectionMaker(connectionMaker());
        userDao.setDataSource(dataSource());
        return userDao;
    }

//    public MessageDao messageDao() {
//        return new MessageDao(connectionMaker());
//    }

    //여러 개의 DAO를 생성하는 경우가 생길 수 있음 -> ConnectionMaker 구현클래스의 오브젝트 생성 분리
    //DAO는 여전히 connectionMaker()를 통해서 DI를 받는다.
    @Bean
    public ConnectionMaker connectionMaker() {
        //return new DConnectionMaker();
        return new CountingConnectionMaker(realConnectionMaker());
    }

    //connectionMaker()가 realConnectionMaker()에서 실제 커넥션을 생성하는 것을 DI받게 한다.
    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new DConnectionMaker();
    }


}
