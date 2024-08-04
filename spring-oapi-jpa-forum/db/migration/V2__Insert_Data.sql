-- Insert sample data into users table
INSERT INTO users (username, email, created_by, modified_by)
VALUES
    ('홍길동', 'hong.gildong@example.com', 1, 1),
    ('김철수', 'kim.cheolsu@example.com', 1, 1),
    ('이영희', 'lee.younghee@example.com', 1, 1),
    ('박영수', 'park.youngsu@example.com', 1, 1);

-- Insert sample data into boards table
INSERT INTO boards (name, description, created_by, modified_by)
VALUES
    ('자유 게시판', '자유롭게 이야기하는 공간입니다.', 1, 1),
    ('공지사항', '공식 공지 및 업데이트를 확인하세요.', 2, 2),
    ('기술 토론', '최신 기술 동향에 대해 토론합니다.', 3, 3);

-- Insert sample data into posts table
INSERT INTO posts (board_id, user_id, title, content, created_by, modified_by)
VALUES
    (1, 1, '환영합니다!', '안녕하세요, 새로운 포럼에 오신 것을 환영합니다!', 1, 1),
    (2, 2, '포럼 규칙', '포럼 규칙을 읽고 준수해 주세요.', 2, 2),
    (3, 3, '최신 기술 동향', '최신 기술 동향에 대해 이야기해 봅시다.', 3, 3),
    (1, 4, '자기소개', '안녕하세요, 저는 새로 가입한 박영수입니다. 반갑습니다!', 4, 4);

-- Insert sample data into comments table
INSERT INTO comments (post_id, user_id, content, created_by, modified_by)
VALUES
    (1, 2, '환영 인사 감사해요!', 2, 2),
    (1, 3, '저도 반갑습니다!', 3, 3),
    (2, 1, '규칙을 준수하겠습니다.', 1, 1),
    (3, 4, '인공지능이 미래라고 생각합니다.', 4, 4),
    (4, 1, '저도 반갑습니다, 박영수님!', 1, 1);