package com.rpg.lab.testsupport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueryCounter {

    private final EntityManager em;
    private final EntityManagerFactory emf;

    /**
     * 1차 캐시를 비운 뒤 action 실행 중 나간 JDBC statement 수를 센다.
     */
    public long count(Runnable action) {
        em.flush();
        em.clear();

        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        action.run();
        return stats.getPrepareStatementCount();
    }
}
