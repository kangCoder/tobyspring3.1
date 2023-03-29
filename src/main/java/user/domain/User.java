package user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String name;
    private String password;

    private Level level;
    private int login; //로그인 횟수
    private int recommend; //추천수

    public void upgradeLevel() {
        Level nextLevel = this.level.nextLevel();
        if(nextLevel == null) {
            throw new IllegalStateException(this.level + "은 업그레이드가 불가합니다.");
        } else {
            this.level = nextLevel;
        }
    }
}
