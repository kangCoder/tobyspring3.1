import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import user.dao.UserDao;
import user.domain.User;

/**
 * 2-2-2. [JUnit 테스트]
 * JUnit 이 테스트 클래스를 가져와 테스트를 수행하는 방식
 * 1. @Test, public, void 인 메서드를 모두 찾는다.
 * 2. 오브젝트를 만들고 @Before 가 있으면 실행한다.
 * 3. @Test 가 붙은 메서드를 실행하고 결과 저장하고 @After 실행.
 * 항상 스프링 컨테이너 없이 테스트를 수행하는 방법을 1번으로 고려하자. 그게 제일 빠르다. (필요한 오브젝트 생성, 초기화가 단순하다면 이 방법을 고려)
 *
 */
@SpringBootTest
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoTest {

    @Autowired
    private ApplicationContext context;

    UserDao dao; //픽스처. 테스트를 수행하는데 필요한 정보나 오브젝트.

    @BeforeEach
    public void setUp() {
        dao = new UserDao();
        DataSource dataSource = new SingleConnectionDataSource(
                "jdbc:mysql://localhost:3306/tobyspring", "root", "11111111", true
        );
        dao.setDataSource(dataSource);
        //this.dao = context.getBean("userDao", UserDao.class);
    }

    @Test
    public void addAndGet() throws Exception {
        //given
        User user1 = new User("gymee", "박성철", "spring1");
        User user2 = new User("leegw700", "이길원", "spring2");
        User user3 = new User("bumjin", "박범진", "spring3");

        //when
        dao.add(user1);
        dao.add(user2);
        dao.add(user3);

        User userGet2 = dao.get(user2.getId());

        //then
        assertThat(dao.getCount()).isEqualTo(3);
        assertThat(userGet2.getName()).isEqualTo(user2.getName());
        assertThat(userGet2.getPassword()).isEqualTo(user2.getPassword());
    }

    @Test
    public void getUserFailure() throws Exception {
        //given

        //when
        dao.deleteAll();

        //then
        assertThat(dao.getCount()).isEqualTo(0);
        assertThrows(EmptyResultDataAccessException.class, () -> dao.get("unknown_id"));
    }
}
