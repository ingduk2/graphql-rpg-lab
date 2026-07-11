package com.rpg.lab.quest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestRewardRepository extends JpaRepository<QuestReward, Long> {

    @Query("""
            SELECT r FROM QuestReward r
            LEFT JOIN FETCH r.item
            WHERE r.quest.id = :questId
            """)
    List<QuestReward> findAllWithItemByQuestId(@Param("questId") Long questId);
}
