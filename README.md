# SE 2분반 15조 Term Project 구동 방법에 관하여

**해당 프로젝트는 SpringBoot를 이용해 만들어졌으며, BackEnd (Spring)와 FrontEnd(Java Swing)가 구분되어 있습니다.**

**프로젝트 실행을 위해 필요한 것**
- **JDK 17** or later
- **Gradle**
- **MySQL server**

**백앤드 실행의 경우**
1. SQL DB를 임의의 이름으로 새로 만들어 주세요.
2. *SW_16\src\main\com.sw.yutnori\resources\application.yml* 에서 *url*, *username*, *password*를 변경해주세요.
3. 터미널에서 프로젝트 루트 디렉토리로 이동 후 명령어 ```./gradlew bootrun``` 을 실행해주세요.

**프론트 실행의 경우**
1. IDE에서 *com.sw.yutnori.ui.swing.SwingGameSetupFrame* 을 실행해주세요.
2. 시각적으로 표현된 파라미터들을 조절하여 입력한 후, 실행 해 성공 메세지가 뜨면 구동에 성공한 것입니다.
