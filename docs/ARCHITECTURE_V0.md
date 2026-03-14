# OHAENG V0 Architecture

## Request Flow (with Hook Points)
```text
Kakao Skill
→ POST /skill
→ Controller
→ Facade
→ Decode → Normalize → Parse
→ [Hook: IdempotencyGuard] → [Hook: RateLimit]
→ (V1) UseCase → (optional) DB
→ SkillResponse
(V1+) After Commit → [Hook: Async Post-Commit]
```

> Note: Hook은 **위치만 정의**하며, 키/TTL/제한값 등 파라미터는 공개 문서에 작성하지 않습니다.

## Layer Responsibilities

| Layer | Responsibility |
|-------|----------------|
| **Controller** | HTTP 입구. 요청 수신(컨텐츠 타입) 및 Facade 위임 |
| **Facade** | Decode/Normalize/Parse 파이프라인 오케스트레이션, 결과 조립 |
| **Pipeline** | Decode(필드 추출) → Normalize(형태 통일) → Parse(Command 생성 + 문법 검증) |
| **UseCase (V1+)** | 도메인 룰 검증, 트랜잭션, 락 관리 및 DB 반영 |
| **Response** | SkillResponse DTO (`version=2.0`, `simpleText.payload.text`) |

## V0 Scope
- Pipeline: Decode/Normalize/Parse 구현
- UseCase: 고정 응답 반환 (비즈니스 로직 미구현)
- Hook: 위치만 정의, 구현은 V1+

## Test Strategy

| 대상 | 방식 |
|------|------|
| Controller | MockMvc, 응답 포맷(Contract) 검증 |
| Pipeline | 순수 단위 테스트(문법/엣지 케이스) |
| Facade | 파이프라인 연결 테스트(스텁/BDDMockito), 흐름 검증 |

## Notes
- 상세 설계/정책/패턴은 GitHub Wiki에 기록합니다.
- Hook 파라미터(키/TTL/제한값 등)는 `docs_private/`로 관리합니다(커밋 금지).
