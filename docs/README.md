# OHAENG(五行戰記) - Zero Point 문서

> 원칙: **Repo docs는 1장 요약만**, 상세 설계/정책은 **GitHub Wiki**로 관리합니다.  
> `docs/`에는 최종적으로 **README.md + ARCHITECTURE_V0.md만** 커밋합니다.

## 📁 Repo 문서 (`docs/`)
| 문서 | 설명 |
|------|------|
| [ARCHITECTURE_V0.md](./ARCHITECTURE_V0.md) | V0 아키텍처 1장 요약 (Hook 위치만 포함) |

## 📚 Wiki 문서
상세 설계/정책은 **레포의 Wiki 탭**에서 관리합니다. *(페이지명은 아래 표와 동일하게 생성/유지)*

| Wiki 페이지 | 설명 |
|-------------|------|
| Home | 문서 개요 및 목차 |
| Parser Pipeline | Decode/Normalize/Parse 파이프라인 + Hook 삽입 위치(개념) |
| Domain Rules | 오행/강화/리롤/DOWN 등 도메인 규칙(공개용 개념판) |
| Locking Strategy | NOWAIT, 데드락 방지, 락 순서(개념) |
| Idempotency | 멱등성 가드 훅 개념 (V1+) |
| Raid Spec | 레이드 정책(단톡방 1개 운영) 개념 |
| Logging | 로깅 원칙(요약/마스킹/민감정보 금지) 개념 |

## ⚠️ 문서 정책
- **중복 금지**: Repo 문서는 요약만, 상세는 Wiki로.
- **비공개 분리**: 수치/키/토큰/URL/**상세 로그 필드** 등 민감 내용은 `docs_private/`로 관리 *(커밋 금지)*.
- **외부 문서**: 원문 복붙 금지. 요약 + 링크만.
