#### 실전 예제

#### 간단한 jwt구조에서 로그인, 로그아웃 단계에 Redis를 적용해보자
- redis는 RedisTemplate을 활용할 것
- redis는 도커 컨테이너로 실행했다 / 도커는 알아서..

1. Redis를 활용하여 login단계에서 refreshToken을 레디스에 저장
- login을 통해 accessToken 발급
- 동시에 refreshToken은 레디스에 저장
- refreshToken을 통한 로그인은 /reissue로 진행
- reissue는 
  >Token이 유효한지 검증합니다.
  >1. Access Token에서 Authentication 객체를 가지고 와서 저장된 name(email)을 가지고 옵니다.
  >2. email을 가지고 Redis에 저장된 Refresh Token을 가지고 와서 입력받은 Refresh Token 값과 비교합니다.
  >3. Authentication 객체를 가지고 새로운 토큰을 생성합니다.
  >4. Redis에 새로 생성된 Refresh Token을 저장합니다.
  >5. 클라이언트에게 새로 발급된 토큰 정보를 내려줍니다.




- 레디스에 refreshToken 저장 후 토큰 재발급까지 완료
- todo : 로그아웃 블랙리스트



참고 : https://wildeveloperetrain.tistory.com/59