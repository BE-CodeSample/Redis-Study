#### 실전 예제

#### 간단한 jwt구조에서 로그인, 로그아웃 단계에 Redis를 적용해보자

1. Redis를 활용하여 login단계에서 refreshToken을 레디스에 저장
- login을 통해 accessToken 발급
- 동시에 refreshToken은 레디스에 저장