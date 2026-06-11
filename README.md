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
- **5-4.** 퀘스트 수락/완료 `acceptQuest`, `completeQuest`
- **5-5.** HTML에서 플레이어 생성 폼 구현

**학습 포인트**
- `@DgsMutation` 사용법
- `input` 타입 설계 — 왜 Query 타입과 분리하는지
- Mutation 에러 핸들링 (`GraphQLException`)

---

### Step 6. 전투 시스템 구현 (Mutation 심화)
> "게임의 핵심"

- **6-1.** 전투 도메인 설계
    - `BattleResult` (damage, remainHp, isCritical, message)
- **6-2.** `attack(playerId, monsterId)` Mutation 구현
    - 데미지 계산 로직 (스탯 + 아이템 보너스 반영)
    - 크리티컬 히트 (15% 확률, 1.5배 데미지)
    - 몬스터 처치 시 경험치/아이템 드롭 처리
- **6-3.** `flee(playerId, monsterId)` Mutation 구현
- **6-4.** HTML 전투 화면 — 공격 버튼 + 결과 표시

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
- **8-4.** Context 활용 — 요청별 플레이어 정보 전달

**학습 포인트**
- REST의 HTTP 상태 코드 vs GraphQL의 에러 처리 방식
- `errors` 배열 구조 이해
- DGS Context 빌더 패턴

---

### Step 9. 성능 최적화 & 마무리
> "더 빠르게, 더 안전하게"

- **9-1.** Query Complexity 분석 — 악의적인 중첩 쿼리 방어
- **9-2.** Persisted Queries 개념 이해
- **9-3.** DataLoader 캐싱 전략 정리
- **9-4.** 전체 아키텍처 회고

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