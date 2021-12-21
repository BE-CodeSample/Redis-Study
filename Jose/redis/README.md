# 레디스

## 블랙리스트

- 로그인을 하면 `YOURSSU:사용자이메일` 과 같이 키가 저장됩니다. 값으로는 `refreshToken`이 들어갑니다.
- `블랙리스트`는 `BLACKLIST:jwt토큰` 과 같이 키가 저장됩니다. 값으로는 해당 토큰이 어떤 이유로 `블랙리스트`에 들어갔는지(`reissue`, `logout`,`withdraw`)가 들어갑니다.

1. 회원가입

- `email` , `username` , `password` , `role` 를 body에 줍니다.
- `valid` 검사를 하고 이상이 없으면 회원가입이 완료됩니다.
- 응답으로 `email` , `username` , `role` 을 받습니다.

2. 로그인

- `email` , `password` 를 body에 줍니다.
- db에서 회원정보가 있다면 `refreshToken` 은 레디스에 저장합니다.
- 응답으로 `email` , `username` , `role` , `accessToken` , `refreshToken` 을 받습니다.

3. 토큰 재발급

- `refreshToken`을 `header`에 `Authentication` 필드의 값으로 줍니다.
- 레디스에서 해당 `refreshToken`이 있는지 확인하고 있다면 해당 `refreshToken`은 `블랙리스트`로 등록됩니다.
- 레디스에 새로 발급받은 `refreshToken`을 등록합니다.
- 응답으로 `email` , `accessToken` , `refreshToken` 받습니다.

4. 로그아웃

- `accessToken`을 `header`에 `Authentication` 필드의 값으로 줍니다.
- `accessToken`을 뜯어서 내부에 `email`값을 가져옵니다.
- 해당 `email`로 등록된 `refreshToken`이 있는지 확인합니다.
- 있다면 `accessToken`과 `refreshToken`을 `블랙리스트`로 등록합니다.
- 없다면 `accessToken` 을 `블랙리스트`로 등록합니다.

5. 회원탈퇴

- 로그아웃과 같습니다.
- 회원탈퇴한 토큰을 지우는 방법도 있지만, 해당 토큰으로 `토큰 재발급` , `로그아웃` , `회원탈퇴` api 를 지속적으로 호출한다면 db에 해당 유저가 있는지를 계속 검색해야하기 때문에 `블랙리스트`에 넣었습니다.
