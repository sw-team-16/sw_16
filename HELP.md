# 프로젝트 설정 및 실행 방법

## 백엔드 실행
### 1. Clone
```bash
git clone https://github.com/sw-team-16/sw_16 # 또는 본인이 fork한 repository 주소
cd sw_16
```

### 2. Build
```bash
# Windows
./gradlew.bat build -x test # 현재 테스트에 에러가 나옴

# Linux/Mac
./gradlew build -x test
```

### 3. Deploy

```bash
./gradlew bootRun
```


## Database가 필요한 경우

### 1. MySQL 설치 

```bash
# MacOS
brew install mysql
brew services start mysql
mysql_secure_installation

# Linux Ubuntu
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo mysql_secure_installation
```

Windows:
1. [MySQL 공식 다운로드 페이지](https://dev.mysql.com/downloads/installer/)에서 MySQL Installer 다운로드
2. 설치 프로그램 실행 후, MySQL Server 선택하여 설치 진행
3. 설치 중 root 비밀번호 설정 (e.g.: root)


### 2. DB 생성

```sql
-- MySQL 접속 (Windows: cmd, Mac/Linux: Terminal)
mysql -u root -p

-- 여기서부터는 명령어 끝에 `;` 무조건 붙여줘야 됨.
-- 데이터베이스 생성 (중요! -> 이거 해야 DB 없어서 생기는 오류 막을 수 잇음)
CREATE DATABASE yutnori;

-- 사용자 생성 및 권한 부여 (e.g: mycin / 700325)
CREATE USER mycin@localhost IDENTIFIED BY 700325;
GRANT ALL PRIVILEGES ON yutnori.* TO mycin@localhost;
FLUSH PRIVILEGES;
```
