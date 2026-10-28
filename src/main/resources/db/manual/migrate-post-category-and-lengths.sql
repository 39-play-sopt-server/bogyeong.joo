-- 기존 숫자 카테고리를 사용하던 MySQL DB에만 서버를 중지한 상태에서 한 번 실행합니다.
-- 신규 DB에는 필요하지 않으며, application.yml이 자동 실행하는 스크립트가 아닙니다.
-- 실행 전 백업하고 아래 조회 결과가 모두 0인지 확인합니다. 데이터는 자동 삭제/절삭하지 않습니다.
SELECT COUNT(*) AS invalid_posts FROM post
WHERE category IS NULL OR CAST(category AS CHAR) NOT IN ('0', '1', '2')
   OR title IS NULL OR content IS NULL
   OR CHAR_LENGTH(title) > 100 OR CHAR_LENGTH(content) > 10000;
SELECT COUNT(*) AS invalid_members FROM member WHERE name IS NULL OR CHAR_LENGTH(name) > 50;

-- 기존 Hibernate가 생성한 숫자 범위 CHECK를 제거한 뒤 문자열로 변환합니다.
-- 환경마다 제약 이름이 다르면 SHOW CREATE TABLE post로 확인해 이름을 맞춥니다.
ALTER TABLE post DROP CHECK post_chk_1;
ALTER TABLE post MODIFY category VARCHAR(32) NOT NULL;
UPDATE post SET category = CASE category
    WHEN '0' THEN 'DAILY' WHEN '1' THEN 'QUESTION' WHEN '2' THEN 'INFORMATION'
    ELSE category END;
ALTER TABLE post ADD CONSTRAINT chk_post_category
    CHECK (category IN ('DAILY', 'QUESTION', 'INFORMATION'));
ALTER TABLE post MODIFY title VARCHAR(100) NOT NULL, MODIFY content VARCHAR(10000) NOT NULL;
ALTER TABLE member MODIFY name VARCHAR(50) NOT NULL;
