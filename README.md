Magnolia API Server For Searching Places

## Notice
main branch의 설정환경은 production용 설정이 아닙니다 (실제 서비스용 서버가 아닙니다)

main branch의 설정은 dev branch의 설정과 동일합니다

## API 테스트

- src/test/kotlin/com/cococloudy/magnolia/MagnoliaAPITest.http
- 위의 http request 파일에서 API를 테스트 해 볼 수 있습니다
- Spring Boot 서버를 실행하면 H2 데이터베이스도 함께 실행됩니다 (관련설정 :
  src/main/kotlin/com/cococloudy/magnolia/config/H2ServerConfiguration.kt)
- 예시 더미 데이터는 data/magnolia.mv.db에 함께 저장되어 있습니다

## 사용한 외부라이브러리

- jsonwebtoken : Json Web Token(JWT)를 통해 회원가입/로그인 기능을 구현하였습니다 (https://github.com/jwtk/jjwt)
- okhttp3 : HTTP 요청을 보내고 응답을 받기 위해서 사용하였습니다 (https://github.com/square/okhttp)
- open api3 : API Documentation 자동 생성을 도와주는 라이브러리입니다 (https://github.com/springdoc/springdoc-openapi)
- querydsl: JPA 기능을 확장해 복잡한 쿼리를 보다 잘 처리하기 위해 사용하였습니다 (https://github.com/querydsl/querydsl)

## 개선점

### 로컬 API 호출시 데이터베이스에 결과값 저장 (캐싱)

- 장소 검색 API의 검색결과를 데이터베이스에 저장한다면, 같은 장소 검색 요청이 왔을 때 로컬 API를 호출하지 않고 데이터베이스에 저장된 값을 불러와 더 빠르게 응답할 수 있습니다. (퍼포먼스)
- 또한 카카오 로컬 API, 네이버 로컬 API는 무료로 제공하는 쿼터가 정해져있기 때문에 검색 결과를 저장하여 캐싱하면 효율적으로 쿼터를 사용할 수 있습니다. (쿼터문제)
- 카카오 로컬과 네이버 로컬의 결과가 업데이트 될 수 있기 때문에 캐시된 후 하루 이후의 요청은 API를 새로 호출하여 그 결과를 데이터베이스에 저장한 뒤 값을 반환합니다.
- 이때 로컬 API에서의 결과와 데이터베이스의 결과가 같으면 데이터베이스에 저장된 값들의 생성날짜 (TTL을 위해서)을 업데이트한 뒤에 결과를 보냅니다.
- 만약 결과 값이 다르다면 데이터베이스에 저장된 값들을 삭제하고 새로운 값들을 저장합니다.
- 캐쉬된 결과를 무시하고 새로 결과를 받아올 수 있도록 파라미터를 추가하여 상황에 맞게 사용할 수 있도록 하면 더욱 좋을 것입니다.
- 위의 내용을 장소 검색 API에 반영해놓았습니다.