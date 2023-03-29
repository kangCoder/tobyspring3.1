package user.dao;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;

/**
 * 2-2-2. [JUnit 테스트] JUnit 이 테스트 클래스를 가져와 테스트를 수행하는 방식 1. @Test, public, void 인 메서드를 모두 찾는다. 2. 오브젝트를 만들고 @Before 가 있으면 실행한다. 3.
 *
 * @Test 가 붙은 메서드를 실행하고 결과 저장하고 @After 실행. 항상 스프링 컨테이너 없이 테스트를 수행하는 방법을 1번으로 고려하자. 그게 제일 빠르다. (필요한 오브젝트 생성, 초기화가 단순하다면 이 방법을 고려)
 */
@SpringBootTest
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserDaoTest {

    @Autowired
    private UserDao dao; //픽스처. 테스트를 수행하는데 필요한 정보나 오브젝트.

    User user1, user2, user3;

    @BeforeEach
    public void setUp() {
        this.user1 = new User("gyumee", "박성철", "spring1", Level.BASIC, 1, 0);
        this.user2 = new User("leegw700", "이길원", "spring2", Level.SILVER, 55, 10);
        this.user3 = new User("bumgin", "박범진", "spring3", Level.GOLD, 100, 40);
    }

    @Test
    public void addAndGet() throws Exception {
        //given
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);
        dao.add(user3);

        //when
        User userGet1 = dao.get(user1.getId());
        User userGet2 = dao.get(user2.getId());
        User userGet3 = dao.get(user3.getId());

        //then
        checkSameUser(userGet1, user1);
        checkSameUser(userGet2, user2);
        checkSameUser(userGet3, user3);
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

    @Test
    public void getAll() throws Exception {
        //given
        dao.deleteAll();

        dao.add(user1);
        List<User> users1 = dao.getAll();
        for (User user : users1) {
            System.out.println("user1.getId=" + user.getId());
        }
        assertThat(users1.size()).isEqualTo(1);
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        for (User user : users2) {
            System.out.println("user2.getId=" + user.getId());
        }
        assertThat(users2.size()).isEqualTo(2);
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        for (User user : users3) {
            System.out.println("user3.getId=" + user.getId());
        }
        assertThat(users3.size()).isEqualTo(3);
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));
        checkSameUser(user3, users3.get(0));
    }

    @Test
    public void duplicateKey() throws DataAccessException {
        dao.deleteAll();

        assertThrows(DataAccessException.class, () -> {
            dao.add(user1);
            dao.add(user1);
        });
    }

    @Test
    public void update() throws Exception {
        //given
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);

        //when
        user1.setName("오민규");
        user1.setPassword("spring6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        //then
        User user1Update = dao.get(user1.getId());
        checkSameUser(user1, user1Update);
        User user2Update = dao.get(user2.getId());
        checkSameUser(user2, user2Update);
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
        assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
    }
}
