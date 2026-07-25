# ERD

## Aggregate 구조 (제안)

- **Quest**: `QuestCondition`, `QuestReward`를 소유 (cascade)
- **PlayerQuest**: `PlayerQuestProgress`를 소유 (cascade)
- **Inventory**: `InventoryItem`을 소유 (cascade) — 이미 적용됨

```mermaid
erDiagram
    Player ||--|| Inventory : has
    Inventory ||--o{ InventoryItem : contains
    InventoryItem }o--|| Item : references

    Monster ||--o{ MonsterDrop : has
    MonsterDrop }o--|| Item : drops

    Quest ||--o{ QuestCondition : has
    Quest ||--o{ QuestReward : has
    QuestReward }o--o| Item : "grants (optional)"

    Player ||--o{ PlayerQuest : accepts
    Quest ||--o{ PlayerQuest : "tracked by"
    PlayerQuest ||--o{ PlayerQuestProgress : has
    QuestCondition ||--o{ PlayerQuestProgress : "tracked by"
```