package com.rpg.lab.achievement;

import com.rpg.lab.player.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "player_achievements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayerAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(nullable = false)
    private LocalDateTime achievedAt;

    public static PlayerAchievement create(
            Player player,
            Achievement achievement
    ) {
        PlayerAchievement playerAchievement = new PlayerAchievement();
        playerAchievement.player = Objects.requireNonNull(player);
        playerAchievement.achievement = Objects.requireNonNull(achievement);
        playerAchievement.achievedAt = LocalDateTime.now();
        return playerAchievement;
    }
}
