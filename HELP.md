## 프로젝트 설정 및 실행 방법

### 1. Clone
```bash
git clone [repository-url]
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

