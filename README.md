Magnolia API Server For Searching Places

## 사용한 외부라이브러리

- jsonwebtoken : Json Web Token(JWT)를 통해 회원가입/로그인 기능을 구현하였습니다 (https://github.com/jwtk/jjwt)
- okhttp3 : HTTP 요청을 보내고 응답을 받기 위해서 사용하였습니다 (https://github.com/square/okhttp)
- open api3 : API Documentation 자동 생성을 도와주는 라이브러리입니다 (https://github.com/springdoc/springdoc-openapi)
- querydsl: JPA 기능을 확장해 복잡한 쿼리를 보다 잘 처리하기 위해 사용하였습니다 (https://github.com/querydsl/querydsl)

## 개선점

장소 검색 API의 검색결과를 캐싱시켜 데이터베이스에 저장하여 같은 검색에 대해서 처리하면 더 빠르게 처리할 수 있다. 또한 카카오 로컬 API, 네이버 로컬 API의 무료로 제공하는 쿼터가 정해져있기 때문에 검색
결과를 저장하여 캐싱하면 더 효율적으로 쿼터를 사용할 수 있다.

카카오 로컬과 네이버 로컬의 결과가 업데이트 될 수 있기 때문에 캐시된 후 하루 이후의 요청은 캐시된 결과를 사용하지 않고 API를 새로 호출하여 그 결과를 데이터베이스에 저장한 뒤 값을 반환한다.
캐쉬된 결과를 무시하고 새로 결과를 받아올 수 있도록 파라미터를 추가하여 사용성을 고려하여 사용할 수 있도록 하면 더욱 좋을 것이다.
