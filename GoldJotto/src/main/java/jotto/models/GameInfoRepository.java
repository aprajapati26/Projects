package jotto.models;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameInfoRepository extends MongoRepository<GameInfo, String>{
    public List<GameInfo> findByuserName(String userName);
}

