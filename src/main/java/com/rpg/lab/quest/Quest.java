package com.rpg.lab.quest;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "quests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    public static Quest create(String title, String description) {
        Quest quest = new Quest();
        quest.title = Objects.requireNonNull(title);
        quest.description = description;
        return quest;
    }
}
