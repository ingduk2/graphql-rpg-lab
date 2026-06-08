INSERT INTO players (name, level, hp, max_hp, attack, defence, speed)
VALUES
    ('전사 김철수', 1, 100, 100, 10, 5, 5),
    ('마법사 이영희', 1, 80, 80, 15, 3, 7),
    ('궁수 박민준', 1, 90, 90, 12, 4, 9);

INSERT INTO monsters (name, hp, max_hp, attack_power)
VALUES
    ('슬라임', 30, 30, 5),
    ('고블린', 50, 50, 10),
    ('오크', 100, 100, 20);

INSERT INTO items (name, type, attack_bonus, defense_bonus)
VALUES
    ('낡은 검', 'WEAPON', 5, 0),
    ('가죽 갑옷', 'ARMOR', 0, 5),
    ('마법 지팡이', 'WEAPON', 10, 0),
    ('철 방패', 'ARMOR', 0, 8),
    ('민첩의 반지', 'ACCESSORY', 2, 2);

INSERT INTO inventories (player_id)
VALUES (1), (2), (3);

INSERT INTO inventory_items (inventory_id, item_id)
VALUES
    (1, 1), (1, 2),
    (2, 3),
    (3, 4), (3, 5);