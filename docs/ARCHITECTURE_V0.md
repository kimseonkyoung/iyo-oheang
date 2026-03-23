# OHAENG V0 Architecture

## Request Flow

```text
Kakao Skill
→ POST /skill
→ SkillController
→ SkillFacade
→ DecodeStage → NormalizeStage → ParseStage
→ IdempotencyStage → RateLimitStage → DbGateStage
→ UseCase → DB (FOR UPDATE NOWAIT)
→ SkillResponse (version=2.0, simpleText)
```

> Stage 파라미터(TTL, 제한값 등)는 `docs_private/`로 관리합니다(커밋 금지).

## Pipeline Stage 순서

| 순서 | Stage | 역할 |
|---|---|---|
| 1 | DecodeStage | rawJson → utterance / userId / callbackUrl 추출 |
| 2 | NormalizeStage | 공백 정리, 형태 통일 |
| 3 | ParseStage | utterance → CommandType + args |
| 4 | IdempotencyStage | 중복 요청 차단 (requestId / debounce / alt-key) |
| 5 | RateLimitStage | 유저별 요청 횟수 제한 (슬라이딩 윈도우) |
| 6 | DbGateStage | DB 동시 접근 수 제한 (Semaphore, NOWAIT 패턴) |

> IdempotencyStage가 ParseStage 이후인 이유: CommandType 기반 Debounce를 위해 ParseStage 결과가 필요.

> DbGateStage 허가 반납은 SkillFacade의 `try/finally`에서 보장.

## Layer Responsibilities

| Layer | Responsibility |
|-------|----------------|
| **Controller** | HTTP 입구. 요청 수신 및 Facade 위임 |
| **GlobalExceptionHandler** | `ResourceLockedException` (NOWAIT 실패), `IllegalArgumentException`, 기타 예외를 HTTP 200 + SkillResponse로 변환 |
| **Facade** | 파이프라인 실행, UseCase 라우팅, DbGate 반납 보장 |
| **Pipeline** | Decode → Normalize → Parse → Idempotency → RateLimit → DbGate |
| **UseCase** | 도메인 룰 검증, 트랜잭션, FOR UPDATE NOWAIT 락, DB 반영 |
| **Domain Service** | 순수 계산 (BattleCalculator, EnhanceCalculator) |
| **Response** | SkillResponse DTO (`version=2.0`, `simpleText.payload.text`) |

## Concurrency / Lock 규칙

- 단일 유저 쓰기: `USER FOR UPDATE NOWAIT`
- PvP(2명): `min(userId) → max(userId)` 사전순 락 획득 (데드락 방지)
- NOWAIT 실패 → `ResourceLockedException` → GlobalExceptionHandler → "처리 중" 응답

## V0 Scope

| 기능 | 상태 |
|---|---|
| Pipeline (6 Stage) | 완료 |
| GetMyInfo | 완료 |
| Hunt | 완료 |
| Enhance | 완료 |
| Reroll | 완료 |
| Duel (PvP 5라운드) | 완료 |
| Ranking | 미구현 (준비 중) |
| Raid (PvE 파티) | **V0 제외** — 카카오 스킬 API가 채팅방 식별자를 제공하지 않아 방 단위 파티 구성 불가 |

## Test Strategy

| 대상 | 방식 |
|------|------|
| Controller | MockMvc, 응답 포맷(Contract) 검증 |
| Pipeline Stage | 순수 단위 테스트 (IdempotencyStage / RateLimitStage / DbGateStage) |
| Facade | 파이프라인 연결 테스트 (Mock UseCase) |

## Notes

- Hook 파라미터(키/TTL/제한값 등)는 `docs_private/`로 관리합니다(커밋 금지).
- V1+: IdempotencyStage / RateLimitStage 저장소를 인메모리 → Redis로 교체 예정.
- V1+: 카카오 채팅방 식별자 확보 시 Raid 기능 재추가 가능 (O_RAIDS / O_RAID_MEMBERS 스키마 유지).
