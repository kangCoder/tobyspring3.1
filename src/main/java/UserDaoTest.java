import java.sql.SQLException;
import org.springframework.context.support.GenericXmlApplicationContext;
import user.dao.CountingConnectionMaker;
import user.dao.DaoFactory;
import user.dao.UserDao;
import user.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserDaoTest {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        UserDao dao = new DUserDao();

//        ConnectionMaker connectionMaker = new DConnectionMaker(); //어떤 커넥션을 맺을 지를 main에게 맡긴다.
//        UserDao dao = new UserDao(connectionMaker);
        //사실 Main도 ConnectionMaker의 구현클래스를 결정하는 일을 맡으면 안된다.
        //Main은 그저 UserDao의 테스트 용도지 다른 용도로 쓰여서는 안된다.

        //Main이 ConnectionMaker의 구현클래스를 결정하는 일의 책임 분리.
//        UserDao dao = new DaoFactory().userDao();

        //스프링의 빈 팩토리(애플리케이션 컨텍스트)를 적용한 경우.
//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
//        UserDao dao = context.getBean("userDao", UserDao.class); //메서드의 이름이 빈의 이름이다.

        //스프링의 설정 정보(xml 파일)을 사용한 경우.
        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        UserDao dao = context.getBean("userDao", UserDao.class);

        System.out.println("======DAO 사용코드 시작=======");
        User user = new User();
        user.setId("bitgustn7");
        user.setName("Hyunsu");
        user.setPassword("kang");

        dao.add(user); //커넥션 한 번

        System.out.println(user.getId() + " 등록 성공.");

        User user2 = dao.get(user.getId()); //커넥션 두 번
        System.out.println("등록: " + user.getId() + ", 조회: " + user2.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
        System.out.println("======DAO 사용코드 끝=======");

        //Counting
        //CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
        //System.out.println("Connection count: " + ccm.getCount()); //예상: 2번

    }

}
