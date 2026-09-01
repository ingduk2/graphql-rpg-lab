# ⚔️ GraphQL RPG — Netflix DGS 딥다이브 프로젝트

> Spring Boot 4 + Netflix DGS + H2 기반 RPG 게임 서버  
> GraphQL의 핵심 개념(N+1, DataLoader, Subscription)을 직접 구현하며 체득하는 프로젝트

---

## 🗺️ 기술 스택

| 영역        | 기술                      |
|-----------|-------------------------|
| Language  | Java 25                 |
| Framework | Spring Boot 4.0.6       |
| GraphQL   | Netflix DGS 11          |
| ORM       | Spring Data JPA         |
| DB        | H2 (in-memory)          |
| Frontend  | HTML + Fetch API (심플하게) |
| Build     | Gradle                  |

---

## 📚 커리큘럼

### Step 0. 프로젝트 세팅
> "일단 돌아가는 것부터"

- **0-1.** Spring Initializr로 프로젝트 생성 & 의존성 추가
    - Spring Web, Spring Data JPA, H2, Lombok, Netflix DGS
- **0-2.** `application.yml` 설정
    - H2 in-memory DB, JPA ddl-auto, open-in-view false
- **0-3.** DGS 동작 확인
    - `ping` 쿼리 + `PingDataFetcher` 작성
    - `/graphiql` 접속 후 `pong!` 응답 확인

**학습 포인트**
- DGS가 Spring Boot에서 어떻게 동작하는지 큰 그림 이해
- `@DgsComponent`, `@DgsQuery` 애너테이션 첫 인상

---

### Step 1. GraphQL Schema 설계
> "API는 Schema가 전부다"

- **1-1.** 도메인 타입 정의 (`schema.graphqls`)
    - `Player` (id, name, level, hp, maxHp, stats)
    - `Monster` (id, name, hp, attackPower)
    - `Item` (id, name, type, attackBonus, defenseBonus)
    - `Inventory` (playerId, items)
    - `Quest` (id, title, description, status, rewards)
- **1-2.** Query / Mutation / Subscription 틀 설계
- **1-3.** 타입 간 관계 설계 (Player ↔ Inventory ↔ Item)
- **1-4.** GraphiQL에서 Introspection으로 Schema 확인

**학습 포인트**
- GraphQL SDL(Schema Definition Language) 문법
- REST와 다른 Schema-first 설계 철학
- `type`, `input`, `enum`, `interface` 구분

---

### Step 2. 기본 Query 구현
> "조회부터 완벽하게"

- **2-1.** JPA Entity 및 Repository 생성
- **2-2.** `PlayerDataFetcher` 구현
    - `players` — 전체 목록 조회
    - `player(id)` — 단건 조회
- **2-3.** `MonsterDataFetcher` 구현
- **2-4.** 더미 데이터 `data.sql` 작성
- **2-5.** HTML 페이지에서 fetch로 플레이어 목록 표시

**학습 포인트**
- `@DgsQuery`, `@InputArgument` 사용법
- DGS의 Data Fetcher가 REST Controller와 어떻게 다른지
- GraphQL의 필드 선택 — 클라이언트가 원하는 것만 가져오기

---

### Step 3. N+1 문제 재현 & 확인
> "문제를 모르면 해결도 없다"

- **3-1.** `Player`가 `Inventory`와 `Item`을 함께 조회하는 Query 구현
- **3-2.** N+1 쿼리가 발생하는 상황을 의도적으로 만들기
- **3-3.** Hibernate SQL 로그로 쿼리 수 눈으로 확인
- **3-4.** 10개 플레이어 조회 시 → 1 + 10 + 10 = 21개 쿼리 발생 확인

**학습 포인트**
- GraphQL에서 N+1이 왜 REST보다 더 쉽게 터지는지
- Lazy Loading과 Data Fetcher의 관계
- 문제 재현 → 로그 분석 → 개선 사이클

---

### Step 4. DataLoader로 N+1 해결
> "Batching의 마법"

- **4-1.** `ItemDataLoader` 구현 (`MappedBatchLoader`)
- **4-2.** `InventoryDataFetcher`에 DataLoader 적용
- **4-3.** 쿼리 수 비교: 21개 → 3개로 감소 확인
- **4-4.** DataLoader 동작 원리 이해 (같은 요청 내 배치 처리)

**학습 포인트**
- `MappedBatchLoader` vs `BatchLoader` 차이
- DGS에서 DataLoader 등록하는 방법
- DataLoader가 요청 단위(per-request)로 동작하는 이유

---

### Step 5. Mutation 구현
> "쓰기 작업 — GraphQL답게"

- **5-1.** Quest 도메인 및 Query 구현
- **5-2.** 플레이어 생성 `createPlayer(input: CreatePlayerInput!)`
- **5-3.** 아이템 장착/해제 `equipItem`, `unequipItem`
- **5-4.** PlayerQuest 중간 엔티티 설계 (Player ↔ Quest 다대다)
- **5-5.** 퀘스트 수락/완료 `acceptQuest`, `completeQuest`
- **5-6.** HTML에서 플레이어 생성 폼 구현

**학습 포인트**
- `@DgsMutation` 사용법
- `input` 타입 설계 — 왜 Query 타입과 분리하는지
- Mutation 에러 핸들링 (`GraphQLException`)

---

### Step 6. 전투 시스템 구현 (Mutation 심화)
> "게임의 핵심"

- **6-1.** 전투 도메인 설계
  - `BattleResult` (damage, monsterRemainHp, playerRemainHp, isCritical, message, monsterDefeated)
- **6-2.** `attack(playerId, monsterId)` Mutation 구현
  - 플레이어 → 몬스터 데미지 계산 (스탯 + 아이템 보너스 반영)
  - 크리티컬 히트 (15% 확률, 1.5배 데미지)
  - 몬스터 반격 (몬스터 attackPower 기반)
  - 플레이어 HP DB 업데이트
  - 몬스터 처치 시 처리
- **6-3.** `flee(playerId, monsterId)` Mutation 구현
- **6-4.** HTML 전투 화면 — 공격 버튼 + 결과 표시
- **6-5.** 경험치/레벨업 시스템
  - Player exp, expToNextLevel 필드 추가
  - 몬스터 처치 시 exp 지급, 레벨업 로직
- **6-7.** HTML 전투 화면 개선
  - 플레이어/몬스터 스탯 패널 추가 (LV/ATK/DEF/SPD)
  - EXP 바 추가
  - currentMonsterHp 클라이언트 누적 관리
  - 레벨업/경험치 획득 로그 표시
- **6-8.** 몬스터 처치 시 아이템 드롭
  - MonsterDrop 엔티티 설계
  - 드롭 확률 계산 로직 구현
  - BattleResult droppedItem 추가
- **6-9.** 아이템 스탯 보너스 전투 적용
  - 장착 아이템 attackBonus 합산 → 공격력 반영
  - 장착 아이템 defenseBonus 합산 → 방어력 반영
- **6-10.** HTML 전투 화면 최종 업데이트
  - 드롭 아이템 로그 표시
  - 아이템 보너스 스탯 반영 표시

**학습 포인트**
- 복잡한 비즈니스 로직을 Mutation에서 처리하는 패턴
- GraphQL Union 타입으로 성공/실패 분기 처리

---

### Step 7. Subscription으로 실시간 전투
> "GraphQL의 꽃"

- **7-1.** WebSocket 설정 (DGS + Spring WebSocket)
- **7-2.** `battleEvents(battleId: ID!)` Subscription 구현
    - 턴마다 전투 이벤트 실시간 Push
    - 데미지, 상태 변화, 전투 종료 이벤트
- **7-3.** `@DgsSubscription` + `Flux<BattleEvent>` 구현
- **7-4.** HTML 전투 화면에서 실시간 로그 스트리밍 표시

**학습 포인트**
- Subscription의 동작 원리 (Long-lived connection)
- WebSocket vs SSE 선택 기준
- `Flux` / `Publisher` — Reactive Streams 기초
- Query/Mutation/Subscription 세 가지 작동 방식 비교 정리

---

### Step 8. 에러 핸들링 & 인증 기초
> "프로덕션 코드답게"

- **8-1.** Custom Exception 설계 (`PlayerNotFoundException`, `InsufficientHpException`)
- **8-2.** `@DgsExceptionHandler`로 에러 응답 통일
- **8-3.** GraphQL Error Extensions로 에러 코드 추가
- **8-4.** DGS Context 구현
  - DgsContextBuilder 구현
  - 헤더에서 X-Player-Id 읽어서 Context에 담기
  - DataFetcher에서 Context로 playerId 사용
- **8-5.** 플레이어 생성 화면 (index.html 개선)
  - 최초 접속 시 플레이어 생성
  - playerId 로컬스토리지 저장
  - 이후 요청마다 X-Player-Id 헤더 자동 추가
  - data.sql 플레이어 더미 데이터 제거

**학습 포인트**
- REST의 HTTP 상태 코드 vs GraphQL의 에러 처리 방식
- `errors` 배열 구조 이해
- DGS Context 빌더 패턴

---

### Step 9. 퀘스트 시스템 고도화
- **9-1.** 퀘스트 조건 도메인 설계
  - QuestCondition (type, targetCount)
  - QuestType enum (KILL_MONSTER, REACH_LEVEL, BATTLE_COUNT)
- **9-2.** 퀘스트 진행도 추적
  - PlayerQuestProgress (currentCount)
  - 몬스터 처치/레벨업 시 진행도 업데이트
- **9-3.** 퀘스트 자동 완료
  - 조건 달성 시 자동으로 COMPLETED
  - BattleService에서 퀘스트 진행도 체크
- **9-4.** 퀘스트 보상
  - 완료 시 exp, 아이템 보상
- **9-5.** HTML 퀘스트 화면
  - 퀘스트 목록 및 진행도 표시
  - 퀘스트 수락/완료 UI

---

### Step 10. 테스트 커버리지 완성 & 도메인 리팩토링
> "더 견고하게, 더 안전하게"

**도메인 리팩토링**
- `Quest`, `PlayerQuest`를 Aggregate Root로 리팩토링
  - `QuestCondition`/`QuestReward`, `PlayerQuestProgress`를 각각 캡슐화 (package-private 생성자)
  - 생성 시점에 최소 1개 조건을 필수로 받도록 강제 (불변식 컴파일 타임 보장)
- `ItemDropProcessor`의 `ThreadLocalRandom` 직접 호출을 `RandomProvider` 인터페이스로 추상화
  → 테스트에서 결정론적 값 주입 가능

**테스트 커버리지**
- 도메인 단위 테스트: `Player`, `Battle`, `Inventory`
- 서비스 통합 테스트: `QuestService`, `InventoryService`, `BattleService`(최소)
- GraphQL 리졸버 통합 테스트: `PlayerDataFetcher`, `MonsterDataFetcher`, `QuestDataFetcher`, `InventoryDataFetcher`, `BattleDataFetcher`
- `InventoryDataLoader` 배치 로딩 테스트 (`TestTransaction`으로 별도 스레드 커밋 이슈 해결)
- `@IntegrationTest` 메타 어노테이션 + 공용 `@TestConfiguration`으로 컨텍스트 캐싱 최적화 (4개 → 1개)

**문서**
- `docs/erd.md` — Aggregate 구조를 포함한 ERD (Mermaid)

---

### Step 11. 퀘스트 체인
> "이야기가 있는 성장"

- **11-1.** `Quest`에 선행 퀘스트(`prerequisiteQuestId` 또는 `Quest` 자기참조) 필드 추가
  - Aggregate Root 설계 원칙 유지 — 필요하다면 생성 시점에 선택적으로 받도록
- **11-2.** `acceptQuest` 시점에 선행 퀘스트 완료 여부 검증
  - 선행 퀘스트가 없거나 이미 `COMPLETED` 상태여야 수락 가능
  - 조건 미충족 시 명확한 예외(예: `PrerequisiteQuestNotCompletedException`) 발생
- **11-3.** 스키마에 체인 정보 노출 여부 결정
  - `Quest` 타입에 `prerequisiteQuest: Quest` 필드를 추가할지, 아니면 서버에서만 검증하고 클라이언트엔 안 보여줄지
- **11-4.** 테스트 보강
  - `QuestServiceTest`에 선행 퀘스트 미완료 시 수락 실패 케이스 추가
  - `QuestDataFetcherTest`에 GraphQL 레벨 에러 응답 케이스 추가
- **11-5.** HTML 퀘스트 화면에 잠긴 퀘스트(미충족) 표시

**학습 포인트**
- 자기참조 연관관계(`Quest → Quest`) 설계 시 순환 방지 고려
- 도메인 규칙(선행 조건)을 어느 계층(엔티티 vs 서비스)에서 검증할지 판단

---

### Step 12. 장비 슬롯 시스템
> "가지고 있는 것과 쓰고 있는 것은 다르다"

- **12-1.** 도메인 모델 설계
  - `InventoryItem`에 `equipped: boolean`(또는 `EquipmentSlot` enum: WEAPON/ARMOR/ACCESSORY) 필드 추가
  - `Inventory`에 슬롯별 장착 로직 캡슐화 — 같은 슬롯에 이미 장착된 아이템이 있으면 자동 교체
  - `getAttackBonus()`/`getDefenseBonus()`를 "장착된 아이템만" 집계하도록 변경
  - `InventoryTest`에 장착/해제/교체/보너스 집계 케이스 보강

- **12-2.** 서비스 레벨 반영
  - `InventoryService.equipItem`을 "인벤토리에 추가"가 아니라 "보유 중인 아이템을 장착 상태로 전환"으로 의미 변경
  - 보유하지 않은 아이템 장착 시도 시 예외 처리
  - 같은 슬롯에 이미 장착된 아이템이 있으면 자동 교체되는지 검증
  - `InventoryServiceTest` 케이스 갱신 (기존 "보유=효과" 가정 테스트들 새 로직에 맞게 수정)

- **12-3.** 전투 반영 확인
  - `Battle.attack()`이 장착된 아이템 보너스만 반영하는지 확인 (`Inventory.getAttackBonus/getDefenseBonus`를 그대로 쓰므로 자연히 반영되는지 검증)
  - `BattleTest`의 `AttackWithItems` 케이스가 "장착 상태"를 전제로 하도록 갱신

- **12-4.** GraphQL 스키마/응답 반영
  - `Item` 또는 `InventoryItem` 타입에 `equipped: Boolean!` 필드 노출
  - `InventoryDataFetcherTest`에 장착 상태가 응답에 반영되는지 케이스 추가

- **12-5.** HTML 인벤토리 화면 개선
  - 보유 아이템 목록에서 장착 중인 아이템 시각적으로 구분 (예: 테두리 강조, "EQUIPPED" 배지)
  - 장착/해제 버튼 추가

**학습 포인트**
- "상태(장착 여부)"를 엔티티 필드로 모델링할지, 별도 값 객체(슬롯)로 분리할지 트레이드오프
- 기존 API(`equipItem`/`unequipItem`)의 의미를 재정의할 때 하위 호환성 고려
- 기존 테스트가 새 도메인 규칙과 충돌할 때(회귀) 어떻게 접근하는지

---

### Step 13. 몬스터 스케일링
> "레벨이 오를수록 세상도 강해진다"

- **13-1.** 스케일링 로직 설계
  - MonsterScaler 유틸리티(정적 메서드) 추가 — 플레이어 레벨 기반으로 hp/attackPower를 배율 계산
  - 몬스터 마스터 데이터(DB)는 "기본 스탯"으로 그대로 유지, 조회/전투 시점에만 동적 계산
  - MonsterScaler 단위 테스트 작성

- **13-2.** Battle 반영
  - Battle 생성 시점에 playerLevel 기반으로 스케일링된 몬스터 스탯을 사용하도록 변경
  - BattleTest에 스케일링 반영 케이스 추가

- **13-3.** GraphQL/서비스 반영
  - BattleService.attack()에서 스케일링 적용 확인
  - 필요 시 monster 단건 조회 응답에도 "현재 플레이어 기준 스탯" 노출 여부 결정

- **13-4.** HTML 화면 반영
  - 몬스터 패널에 스케일링된 스탯이 정확히 표시되는지 확인

**학습 포인트**
- 마스터 데이터와 "계산된 값"을 분리해서 설계하는 방식
- 동적 계산 로직을 어느 계층(도메인 vs 서비스)에 둘지 판단

---

### Step 14. 리더보드
> "누가 제일 강한가"

- **14-1.** 레벨 기준 랭킹 조회
  - PlayerRepository에 정렬 쿼리 추가 (레벨 내림차순, 동률이면 exp 내림차순)
  - LeaderboardEntry 타입 설계 (rank, playerId, playerName, level, exp)
  - PlayerService(또는 LeaderboardService)에 leaderboard(limit) 메서드 추가
  - 단위/서비스 테스트 작성

- **14-2.** GraphQL 스키마/리졸버 반영
  - schema.graphqls에 leaderboard(limit: Int): [LeaderboardEntry!]! 추가
  - PlayerDataFetcher(또는 신규 LeaderboardDataFetcher)에 리졸버 구현
  - DataFetcherTest 작성

- **14-3.** HTML 리더보드 화면
  - 순위/이름/레벨/exp 목록 표시
  - 내 순위 하이라이트(선택)

- **14-4.** (고도화) 처치 수 기준 랭킹 추가
  - Player에 killCount 필드 추가
  - BattleVictoryProcessor에서 몬스터 처치 시 killCount 증가
  - leaderboard에 정렬 기준(SortBy: LEVEL/KILL_COUNT) 파라미터 추가
  - 관련 테스트 보강, HTML에 정렬 기준 토글 추가

**학습 포인트**
- 정렬/페이징 쿼리 설계 (Pageable, Sort)
- 랭킹(rank) 계산을 서버에서 할지 클라이언트에서 할지 트레이드오프
- 기존 통계 없이 새 집계 필드(killCount)를 도입할 때 마이그레이션/초기값 고려

---

### Step 15. 골드/판매 시스템
> "쓸모없는 검도 돈이 된다"

- **15-1.** 도메인 모델 설계
  - Player에 gold 필드 추가 (기본값 0)
  - gainGold(int amount) 메서드 추가
  - Item(또는 별도 정책 클래스)에 판매가 계산 로직 설계
    (attackBonus/defenseBonus 기반? 고정가? 결정 필요)
  - PlayerTest에 관련 케이스 추가

- **15-2.** 서비스 레벨 반영
  - InventoryService.sellItem(playerId, itemId) 추가
    - 보유 여부 확인 → 인벤토리에서 제거 → 판매가만큼 골드 지급
  - 장착 중인 아이템도 판매 가능한지, 판매 전 자동 해제할지 정책 결정
  - InventoryServiceTest에 케이스 추가

- **15-3.** GraphQL 스키마/응답 반영
  - schema.graphqls에 sellItem(itemId: ID!): SellItemResult! 뮤테이션 추가
    (또는 Inventory/Player를 그대로 반환할지 결정)
  - Player 타입에 gold: Int! 필드 노출
  - InventoryDataFetcherTest/PlayerDataFetcherTest에 케이스 추가

- **15-4.** HTML 반영
  - 플레이어 패널에 GOLD 표시
  - 인벤토리 아이템에 SELL 버튼 추가
  - 판매 로그 표시

**학습 포인트**
- 판매가 정책(고정가 vs 스탯 기반)을 어디에 둘지 — Item 엔티티 vs 별도 PricingPolicy
- 장착 중인 아이템 판매 시 사이드이펙트(자동 해제) 처리 방식

---

### Step 16. 성능 최적화 & 마무리 (예정)
> "더 빠르게, 더 안전하게"

- **16-1.** Query Complexity 분석 — 악의적인 중첩 쿼리 방어
- **16-2.** Persisted Queries 개념 이해
- **16-3.** DataLoader 캐싱 전략 정리
- **16-4.** 전체 아키텍처 회고

**학습 포인트**
- GraphQL의 보안 고려사항 (Depth Limit, Complexity Limit)
- 프로덕션에서 GraphQL 사용 시 주의점

---

## 📁 프로젝트 구조 (목표)

```
graphql-rpg/
├── src/main/
│   ├── java/com/rpg/lab/
│   │   ├── player/
│   │   │   ├── PlayerEntity.java
│   │   │   ├── PlayerRepository.java
│   │   │   └── PlayerDataFetcher.java
│   │   ├── monster/
│   │   ├── battle/
│   │   │   ├── BattleService.java
│   │   │   ├── BattleDataFetcher.java
│   │   │   └── BattleSubscription.java
│   │   ├── item/
│   │   │   ├── ItemDataLoader.java       ← N+1 해결
│   │   │   └── ItemDataFetcher.java
│   │   └── quest/
│   └── resources/
│       ├── schema/
│       │   └── schema.graphqls           ← GraphQL Schema
│       ├── application.yml
│       └── data.sql
└── frontend/
    ├── index.html                         ← 플레이어 목록
    └── battle.html                        ← 전투 화면
```

---

## 🎯 최종 목표

이 프로젝트를 끝내고 나면:

- GraphQL Schema를 처음부터 설계할 수 있다
- N+1 문제를 **눈으로 확인**하고 DataLoader로 해결할 수 있다
- Subscription으로 실시간 데이터 Push를 구현할 수 있다
- DGS 프레임워크의 주요 애너테이션을 자유롭게 쓸 수 있다
- "GraphQL을 왜 쓰는가"에 대한 자기 언어로 된 답을 갖게 된다

---