## 개요
간단한 commerce project

Use : Spring Boot, Jpa, MySql, Redis, Docker, AWS

Goal : 판매자와 구매자 사이를 중계해 주는 commerce server 구축

# 회원
### 공통
- [ㅇ] 이메일을 통해서 인증코드를 통한 회원 가입

### 고객
- [ㅇ] 회원 가입
- [ㅇ] 인증(이메일)
- [ㅇ] 로그인 토큰 발행
- [ㅇ] 로그인 토큰을 통한 제어 확인 (JWT, FIlter 를 간단히 이용)
- [ㅇ] 예치금 관리

### 판매자
- [ㅇ] 회원 가입

## 주문 서버

### 판매자
- [ㅇ] 상품 등록, 수정
- [ㅇ] 상품 삭제

### 구매자
- [ㅇ] 장바구니를 통한 Redis 연동