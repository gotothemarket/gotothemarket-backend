-- PostGIS 확장 활성화
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- 데이터베이스 설정
ALTER DATABASE gotothemarket SET timezone TO 'Asia/Seoul';