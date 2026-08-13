INSERT INTO monsters (name, hp, max_hp, attack_power, exp_reward)
VALUES
    ('슬라임', 30, 30, 5, 20),
    ('고블린', 50, 50, 10, 40),
    ('오크', 100, 100, 20, 80),
    ('고블린 두목', 150, 150, 25, 120);  -- id: 4, 신규 보스 몬스터

INSERT INTO items (name, type, attack_bonus, defense_bonus)
VALUES
    ('낡은 검', 'WEAPON', 5, 0),
    ('가죽 갑옷', 'ARMOR', 0, 5),
    ('마법 지팡이', 'WEAPON', 10, 0),
    ('철 방패', 'ARMOR', 0, 8),
    ('민첩의 반지', 'ACCESSORY', 2, 2),
    ('슬라임 결정', 'ACCESSORY', 3, 0),   -- 슬라임 사냥 퀘스트 보상 (id: 6)
    ('고블린 귀', 'ACCESSORY', 0, 3),     -- 고블린 토벌 퀘스트 보상 (id: 7)
    ('던전 열쇠', 'ACCESSORY', 5, 5),     -- 던전 탐험 퀘스트 보상 (id: 8)
    ('두목의 도끼', 'WEAPON', 15, 0);    -- 고블린 두목 토벌 보상 (id: 9)

INSERT INTO quests (title, description, prerequisite_quest_id)
VALUES
    ('슬라임 사냥', '슬라임 5마리를 처치하라', null),
    ('고블린 토벌', '고블린 3마리를 처치하라', null),
    ('레벨업 달인', '레벨을 5번 올려라', null),
    ('진정한 영웅', '몬스터 3마리 처치 + 레벨 2번 올리기', null),
    ('고블린 두목 토벌', '고블린 두목을 처치하라', 2);  -- id: 5, 고블린 토벌(id:2) 완료해야 열림

INSERT INTO quest_conditions (quest_id, type, target_count, target_monster_id)
VALUES
    (1, 'KILL_MONSTER', 5, 1),   -- 슬라임(id:1) 5마리
    (2, 'KILL_MONSTER', 3, 2),   -- 고블린(id:2) 3마리
    (3, 'LEVEL_UP', 5, null),    -- 레벨업 5번
    (4, 'KILL_MONSTER', 3, null), -- 진정한 영웅 - 모든 몬스터 3마리
    (4, 'LEVEL_UP', 2, null),    -- 진정한 영웅 - 레벨업 2번
    (5, 'KILL_MONSTER', 1, 4);   -- 고블린 두목(id:4) 1마리

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
    (4, 200, null),
    (5, 300, 9);  -- 고블린 두목 토벌 보상