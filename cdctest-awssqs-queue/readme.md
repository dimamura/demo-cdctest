# プロバイダスタブ（契約に基づいたQueueへのメッセージ送信を実行するコード）の生成
gradlew generateContractTests

# プロバイダスタブの実行（プロバイダスタブが実行され、本プロジェクトに含まれるコンシューマモックがQueueへメッセージを受信する）
gradlew test

# コンシューマスタブ（契約に基づいたプロバイダのメッセージを送信するコード）の生成
gradlew generateClientStubs

# Mavenローカルリポジトリへの成果物インストール
gradlew install

=> cdctest-awssqs-queue-0.1.0-SNAPSHOT.jar 、および、 cdctest-awssqs-queue-0.1.0-SNAPSHOT-stubs.jar がMavenローカルリポジトリにインストールされる
