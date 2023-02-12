package user.dao;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.Getter;

/**
 * DAO가 DB를 얼마나 많이 연결해서 사용하는지 파악하기 위한 클래스.
 * 모든 DAO에서 makeConnection을 호출할 떄마다 카운터를 증가시키는 방법은 엄청난 노가다이다.
 * DB의 연결횟수를 세는 것은 DAO의 관심사항이 아니다 -> 책임 분리!
 */

/**
 * 인터페이스를 구현은 했지만, 직접 DB 커넥션을 만들지는 않는다.
 * 대신 DAO가 커넥션을 가져올 때마다 카운트를 증가시킨다.
 *
 */
@Getter
public class CountingConnectionMaker implements ConnectionMaker{
    private int count = 0;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;

    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.count++;
        return realConnectionMaker.makeConnection();
    }
}
