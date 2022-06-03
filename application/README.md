# Text2Draw
캡스톤 디자인 팀 You &amp; AI app UI 구현

***

## 개발 환경
Client : Android Studio, Java

DB : Firebase

***

## 참고 문헌
* [Android Developers][android_link]

[android_link]: https://developer.android.com/docs/

* [Firebase Guide][firebase_link]

[firebase_link]: https://firebase.google.com/docs/android/setup?authuser=0

***

## 환경 구축
### Firebase 프로젝트 생성 및 연동
Android Studio -> Tools -> Firebase -> Authentication -> Authenticate using Google Sign-In 클릭   
<img src="https://user-images.githubusercontent.com/91214201/158526267-abee978a-508d-44ea-8156-1afb70a3f81a.png"></img>

Connect to Firebase -> 기존 프로젝트에 연결하거나 새 프로젝트를 만들어 Firebase 연동(google-services.json 자동 추가)   
<img src="https://user-images.githubusercontent.com/91214201/158527097-dfd0d612-95cc-4355-abb5-571ab24f8688.png"></img>

Add the Firebase Authentication SDK to your app -> build.gradle에 자동으로 필요한 의존성 추가
<img src="https://user-images.githubusercontent.com/91214201/158527326-5e30d4ff-f498-40c2-89ab-a0ff935cc75d.png"></img>   

SHA-1 : Gradle 탭에서 signingReport 실행
<img src="https://user-images.githubusercontent.com/91214201/158529970-a30e88d3-e66b-42cb-b500-f156ac827923.png"></img>

Firebase Console -> 프로젝트 설정 -> SHA-1 인증서 등록

### Firebase Authentication
원하는 제공업체 추가
<img src="https://user-images.githubusercontent.com/90811540/158543347-953558df-214b-4d1f-8f4c-1cdd575b155f.png"></img>
### Firebase Realtime Database
Realtime Database 항목에서 Database 생성 -> 규칙 수정
<img src="https://user-images.githubusercontent.com/91214201/158533093-86c51023-aece-43be-acfc-465303ebb609.png"></img>

Android Studio -> Tools -> Firebase -> Realtime Database -> get started with Realtime Database 클릭   
Add the Realtime Database SDK to your app -> build.gradle에 자동으로 필요한 의존성 추가   
<img src="https://user-images.githubusercontent.com/90811540/158551078-f0599b46-c020-461e-a6bc-fb79059062df.png"></img>
