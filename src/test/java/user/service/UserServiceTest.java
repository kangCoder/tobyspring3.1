package user.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import user.dao.UserDao;
import user.domain.Level;
import user.domain.User;
import user.service.UserService;

@SpringBootTest
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;

    private List<User> users;

    @BeforeEach
    public void setUp() {
        users = Arrays.asList(new User("bumjin", "박범진", "p1", Level.BASIC, 49, 0),
                new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, 29),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, 30),
                new User("green", "오민규", "p5", Level.GOLD, 100, 100));
    }

    @Test
    public void upgradeLevels() throws Exception {
        //given
        userDao.deleteAll();
        for(User user : users) {
            userDao.add(user);
        }

        //when
        userService.upgradeLevels();

        //then
        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    @Test
    public void add() throws Exception {
        //given
        userDao.deleteAll();

        User userWithLevel = users.get(4); //GOLD

        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        //when
        userService.add(userWithLevel);
        userService.add(userWithoutLevel); //null은 BASIC이 될 것이다라고 예측

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        //then
        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);

    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
    }
}
