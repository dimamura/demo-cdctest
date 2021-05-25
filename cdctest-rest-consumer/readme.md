# CDCテストの実行 -パターン1. JUnit組み込みWEBサーバでプロデューサスタブを実行する
gradlew test

# CDCテストの実行 -パターン2. 別個にプロデューサスタブを実行する
## プロデューサースタブの起動
cd ../cdctest-rest-api

（初回のみ）

下記のスタブ実行jarをダウンロード、.\lib へ配置する

https://search.maven.org/remotecontent?filepath=org/springframework/cloud/spring-cloud-contract-stub-runner-boot/2.2.5.RELEASE/spring-cloud-contract-stub-runner-boot-2.2.5.RELEASE.jar

java -Dstubrunner.ids="jp.co.ogis_ri.rd.nautible.cdctest:cdctest-rest-api:+:stubs:80" -Dstubrunner.stubsMode="LOCAL" -jar .\lib\spring-cloud-contract-stub-runner-boot-2.2.5.RELEASE.jar

## コンシューマの実行
java -jar ./build/libs/cdctest-rest-consumer-0.1.0-SNAPSHOT.jar
