package nus.iss.vttp5_ssf_day16w_practice.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BoardGameRepo {

    @Autowired
    @Qualifier("redis-string")
    RedisTemplate<String, String> redisTemplate;

    // save one BoardGame to Redis
    public void saveBoardGame(String hashKey, String boardGameStringData) { 

        // save new board game in "boardGames" hash
        redisTemplate.opsForHash().put("boardGames", hashKey, boardGameStringData);

    }

    // retrieve all board games stored in Redis 
    public List<Object> getAllBoardGameValues() { 

        return redisTemplate.opsForHash().values("boardGames");

    }

    // retrieve a board game with id 
    public String getBoardGameByGID(String gid) {

        return (String) redisTemplate.opsForHash().get("boardGames", gid);

    }

    // check if key exists within boardGames Redis hash
    public Boolean checkKey(String gid) {

        if (redisTemplate.opsForHash().hasKey("boardGames", gid)) {
            return true;
        } 
        
        else {
            return false;
        }

    }

    
}
