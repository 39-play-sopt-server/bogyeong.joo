# 세미나 실습 구조에 맞춘 변경

## 1. 메뉴와 기능별 메서드

실습의 메뉴 1~6, `while`, `switch` 구조를 유지했습니다. Main의 `run()`에서 메뉴를 반복하고 `createPost()`, `readPosts()`, `readPost()`, `updatePost()`, `deletePost()`를 호출합니다.

실습에서는 Controller가 메뉴를 진행했지만, 심화 과제에서는 Main과 View가 클라이언트 역할을 합니다. 따라서 메뉴 진행은 Main으로 옮기고, 서버 역할의 Controller는 요청을 받아 공통 응답을 반환하도록 유지했습니다. `main()`은 객체 연결과 시작을 담당합니다.

View의 메서드 이름도 실습에 맞춰 `printMenu()`, `readCommand()`, `readTitle()`, `readContent()`, `readPostNumber()`, `printPost()`, `printMessage()`로 정리했습니다. 게시글 번호는 목록의 순번이 아니라 고유 ID입니다. `printPost()`는 심화 과제의 공통 응답을 받습니다.

## 2. 입력과 출력 분리

```text
Main → PostView → PostInput   (Scanner 읽기, 숫자·카테고리 해석)
               → PostOutput  (안내, 메뉴, 게시글, 결과 출력)
```

PostInput에는 콘솔 출력이 없고, PostOutput에는 Scanner가 없습니다. PostView는 입력 안내 후 입력을 요청하고, 숫자 형식이나 카테고리 선택이 잘못되면 메시지를 표시하고 재입력을 받습니다. 이 연결 역할을 남겨 Main이 입력·출력 구현을 직접 관리하지 않도록 했습니다.

제목과 본문이 비어 있는지 판단하는 업무 규칙은 계속 Post가 담당합니다.

## 4. Post가 자신의 상태 변경

수정 시 새로운 Post를 반환하던 방식을 기존 객체의 `update()`가 상태를 바꾸는 방식으로 변경했습니다. 제목, 본문, 카테고리를 모두 검증한 뒤 필드를 변경하므로, 잘못된 수정이 일부 필드만 바꾸지 않습니다.

ID, 작성자, 작성 시각은 `final`로 유지합니다. 제목, 본문, 카테고리, 수정 시각만 수정할 수 있습니다. Service는 조회한 Post의 `update()`를 호출하고 Repository에 저장합니다.

응답 DTO 추가는 이번 변경 범위에서 제외했습니다. 따라서 응답은 계속 Post 객체를 참조하며, 같은 JVM의 호출자가 그 객체의 `update()`를 직접 호출하면 저장된 상태에도 영향을 줄 수 있습니다. 현재 Main과 View는 조회한 Post를 출력하는 데만 사용하고, 수정 요청은 Controller로 전달합니다.

## 실행과 검증

Java 21 환경에서 실행합니다.

```powershell
.\gradlew.bat run --console=plain
.\gradlew.bat test --console=plain '-Dorg.gradle.jvmargs=-Dfile.encoding=MS949'
```

테스트 명령의 인코딩 옵션은 현재 Windows 환경에서 한글 프로젝트 경로를 테스트 JVM에 전달하기 위한 설정입니다. Java 소스는 UTF-8로 컴파일합니다.

테스트에서는 수정 시 같은 객체가 유지되는지, ID·작성자·작성 시각이 보존되는지, 검증 실패 시 모든 수정 가능 필드와 수정 시각이 유지되는지 확인합니다.
