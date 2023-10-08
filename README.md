# 스펙
- java(17)
- Spring boot(3.1.4)

# port
- 8080(web)

# 기능
- 계정 로그인,가입
  - 일반(Jwt 토큰기반)
  - Oauth2 google(Jwt 토큰기반)

# dockerize
```
gradlew build --exclude-task test
gradlew jibDockerBuild
```

# runtime
```.docker/env.example 파일에 담긴 환경변수를 실행할때 추가해준다.(vscode 사용시 기본으로 셋팅되어있다.)```
<img width="631" alt="image" src="https://github.com/ehaakdl/gora-backend/assets/6407466/7c2860cb-e365-499d-b78d-d4043d91df73">
