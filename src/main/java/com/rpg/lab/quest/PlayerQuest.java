package com.rpg.lab.quest;

import com.rpg.lab.player.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "player_quests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerQuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestStatus status;

    @OneToMany(mappedBy = "playerQuest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerQuestProgress> progressList = new ArrayList<>();

    public static PlayerQuest start(Player player, Quest quest) {
        PlayerQuest playerQuest = new PlayerQuest();
        playerQuest.player = Objects.requireNonNull(player);
        playerQuest.quest = Objects.requireNonNull(quest);
        playerQuest.status = QuestStatus.IN_PROGRESS;
        quest.getConditions().forEach(condition ->
                playerQuest.progressList.add(PlayerQuestProgress.create(playerQuest, condition)));
        return playerQuest;
    }

    public void complete() {
        this.status = QuestStatus.COMPLETED;
    }
}
