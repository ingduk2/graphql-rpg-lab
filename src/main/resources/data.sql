INSERT INTO players (name, level, hp, max_hp, attack, defense, speed, exp)
VALUES
    ('전사 김철수', 1, 100, 100, 10, 5, 5, 0),
    ('마법사 이영희', 1, 80, 80, 15, 3, 7, 0),
    ('궁수 박민준', 1, 90, 90, 12, 4, 9, 0);

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
    ('민첩의 반지', 'ACCESSORY', 2, 2);

INSERT INTO inventories (player_id)
VALUES (1), (2), (3);

INSERT INTO quests (title, description)
VALUES
    ('슬라임 사냥', '슬라임 5마리를 처치하라'),
    ('고블린 토벌', '고블린 3마리를 처치하라'),
    ('던전 탐험', '던전 깊은 곳을 탐험하라');

INSERT INTO monster_drops (monster_id, item_id, drop_rate)
VALUES
    (1, 1, 0.5),
    (2, 3, 0.4),
    (3, 4, 0.3),
    (3, 5, 0.2);