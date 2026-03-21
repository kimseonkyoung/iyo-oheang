# OHAENG V0 Architecture

## Request Flow

```text
Kakao Skill
→ POST /skill
→ Controller
→ Facade
→ Decode → Normalize → Parse
→ [Hook: IdempotencyGuard] → [Hook: RateLimit] → [Hook: DbGate]
→ UseCase → DB
→ SkillResponse
(V1+) After Commit → [Hook: Async Post-Commit]
```

> Note: Hook은 **위치만 정의**하며, 키/TTL/제한값 등 파라미터는 공개 문서에 작성하지 않습니다.

## Pipeline Stage 순서

| 순서 | Stage | 역할 |
|---|---|---|
| 1 | DecodeStage | rawJson → utterance / userId / callbackUrl 추출 |
| 2 | NormalizeStage | 공백 정리, 형태 통일 |
| 3 | ParseStage | utterance → CommandType + args |
| 4 | IdempotencyGuard | 중복 요청 차단 (requestId / alt-key / debounce) |
| 5 | RateLimit | 유저별 요청 횟수 제한 (슬라이딩 윈도우) |
| 6 | DbGate | DB 동시 접근 수 제한 (Semaphore) |

> IdempotencyGuard가 Parse 이후인 이유: CommandType 기반 Debounce를 위해 ParseStage 결과가 필요.

## Layer Responsibilities

| Layer | Responsibility |
|-------|----------------|
| **Controller** | HTTP 입구. 요청 수신 및 Facade 위임 |
| **Facade** | 파이프라인 오케스트레이션, UseCase 라우팅, 응답 조립 |
| **Pipeline** | Decode → Normalize → Parse → Hook 체인 |
| **UseCase** | 도메인 룰 검증, 트랜잭션, 락 관리 및 DB 반영 |
| **Domain Service** | 순수 계산 (BattleCalculator, EnhanceCalculator) |
| **Response** | SkillResponse DTO (`version=2.0`, `simpleText.payload.text`) |

## V0 Scope

- Pipeline: 전체 Stage 구현 완료
- UseCase: GetMyInfo / Hunt / Enhance / Reroll / Duel 구현 완료
- Hook: IdempotencyGuard / RateLimit / DbGate V0 인메모리 구현 완료

## Test Strategy

| 대상 | 방식 |
|------|------|
| Controller | MockMvc, 응답 포맷(Contract) 검증 |
| Pipeline Stage | 순수 단위 테스트 (엣지 케이스 포함) |
| Facade | 파이프라인 연결 테스트 (Mock UseCase) |

## Notes

- 상세 설계/정책/패턴은 GitHub Wiki에 기록합니다.
- Hook 파라미터(키/TTL/제한값 등)는 `docs_private/`로 관리합니다(커밋 금지).
- V1+: Hook 저장소를 인메모리 → Redis로 교체 예정.
