INSERT INTO monsters (name, hp, max_hp, attack_power, exp_reward)
VALUES
    ('슬라임', 30, 30, 5, 20),
    ('고블린', 50, 50, 10, 40),
    ('오크', 100, 100, 20, 80);

INSERT INTO items (name, type, attack_bonus, defense_bonus)
VALUES
    ('낡은 검', 'WEAPON', 5, 0),
    ('가죽 갑옷', 'ARMOR', 0, 5),
    ('마법 지팡이', 'WEAPON', 10, 0),
    ('철 방패', 'ARMOR', 0, 8),
    ('민첩의 반지', 'ACCESSORY', 2, 2),
    ('슬라임 결정', 'ACCESSORY', 3, 0),   -- 슬라임 사냥 퀘스트 보상 (id: 6)
    ('고블린 귀', 'ACCESSORY', 0, 3),     -- 고블린 토벌 퀘스트 보상 (id: 7)
    ('던전 열쇠', 'ACCESSORY', 5, 5);     -- 던전 탐험 퀘스트 보상 (id: 8)

INSERT INTO quests (title, description)
VALUES
    ('슬라임 사냥', '슬라임 5마리를 처치하라'),
    ('고블린 토벌', '고블린 3마리를 처치하라'),
    ('레벨업 달인', '레벨을 5번 올려라'),
    ('진정한 영웅', '몬스터 3마리 처치 + 레벨 2번 올리기');

INSERT INTO quest_conditions (quest_id, type, target_count)
VALUES
    (1, 'KILL_MONSTER', 5),
    (2, 'KILL_MONSTER', 3),
    (3, 'LEVEL_UP', 5),
    (4, 'KILL_MONSTER', 3),
    (4, 'LEVEL_UP', 2);

INSERT INTO monster_drops (monster_id, item_id, drop_rate)
VALUES
    (1, 1, 0.5),
    (2, 3, 0.4),
    (3, 4, 0.3),
    (3, 5, 0.2);

INSERT INTO quest_rewards (quest_id, reward_exp, item_id)
VALUES
    (1, 50, 6),
    (2, 80, 7),
    (3, 150, 8),
    (4, 200, null);