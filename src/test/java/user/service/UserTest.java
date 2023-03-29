package user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import user.domain.Level;
import user.domain.User;

@SpringBootTest
@ContextConfiguration(locations = "/applicationContext.xml")
public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void upgradeLevel() throws Exception {
        //given
        Level[] levels = Level.values();

        //when
        //then
        for (Level level : levels) {
            if(level.nextLevel() == null) {
                continue;
            }

            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test
    public void cannotUpgradeLevel() throws Exception {
        //given
        Level[] levels = Level.values();

        //when
        //then
        for (Level level : levels) {
            if(level.nextLevel() != null) {
                continue;
            }
            assertThrows(IllegalStateException.class, () -> {
                user.setLevel(level);
                user.upgradeLevel();
            });
        }
    }

}
