# コンシューマスタブ（契約に基づいたコンシュームを実行するコード）の生成
gradlew generateContractTests

# コンシューマスタブの実行（プロデューサのテスト、別途プロデューサの起動が必要）
gradlew test

# プロデューサスタブ（契約に基づいたプロデューサの応答を返すコード）の生成
gradlew generateClientStubs

# Mavenローカルリポジトリへの成果物インストール
gradlew install

=> cdctest-rest-api-0.1.0-SNAPSHOT.jar 、および、 cdctest-rest-api-0.1.0-SNAPSHOT-stubs.jar がMavenローカルリポジトリにインストールされる

# プロデューサスタブの起動
（初回のみ）

下記のスタブ実行jarをダウンロード、.\lib へ配置する

https://search.maven.org/remotecontent?filepath=org/springframework/cloud/spring-cloud-contract-stub-runner-boot/2.2.5.RELEASE/spring-cloud-contract-stub-runner-boot-2.2.5.RELEASE.jar


java -Dstubrunner.ids="jp.co.ogis_ri.rd.nautible.cdctest:cdctest-rest-api:+:stubs:80" -Dstubrunner.stubsMode="LOCAL" -jar .\lib\spring-cloud-contract-stub-runner-boot-2.2.5.RELEASE.jar


（注）

- gradle・mavenから起動するわけではない。スタンドアロンのSpringBootアプリケーションである

# コンシューマのJUnitテストコードへのプロデューサスタブ組み込み
cdctest-rest-consumer内 jp.co.ogis_ri.rd.nautible.cdctest.rest.consumer.ConsumerApplicationContractTest 参照