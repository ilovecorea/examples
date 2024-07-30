-- 'pets' 테이블 생성
CREATE TABLE pets
(
    id   SERIAL PRIMARY KEY,    -- 각 애완동물의 고유 식별자, 자동 증가
    name VARCHAR(255) NOT NULL, -- 애완동물의 이름, 필수 필드
    tag  VARCHAR(255)           -- 애완동물의 종류나 태그, 선택적 필드
);

-- 'errors' 테이블 생성
CREATE TABLE errors
(
    code    INT  NOT NULL, -- 오류 코드, 필수 필드
    message TEXT NOT NULL  -- 오류 메시지, 필수 필드
);