package com.rpg.lab.quest;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestRewardRepository extends Repository<QuestReward, Long> {

    @Query("""
            SELECT r FROM QuestReward r
            LEFT JOIN FETCH r.item
            WHERE r.quest.id = :questId
            """)
    List<QuestReward> findAllWithItemByQuestId(@Param("questId") Long questId);

    QuestReward save(QuestReward questReward);
}
