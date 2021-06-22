# demo-cdctestについて
- Spring Cloud Contractを用いたCDCテストのトレーニングアプリケーションです
- オブジェクトの広場連載記事 [マイクロサービスアーキテクチャに効く!テスト技法](https://www.ogis-ri.co.jp/otc/hiroba/technical/microservices-test/) の[第1回～第3回 サービス間インターフェースのテスト技法 CDCテスト](https://www.ogis-ri.co.jp/otc/hiroba/technical/microservices-test/part1.html) に対応しています。記事本文と合わせてご利用ください 

# 基本形：REST APIのCDCテスト （HTTPのケース）
- HTTPで連携するプロデューサ/コンシューマアプリケーションのCDCテストを実施します
- 以下プロジェクトを使用します。ビルド方法やContractからのスタブ生成・実行方法は各プロジェクトのreadme.mdを参照してください
    - cdctest-rest-api
    - cdctest-rest-producer
    - cdctest-rest-consumer
- オブジェクトの広場連載記事の[第2回](https://www.ogis-ri.co.jp/otc/hiroba/technical/microservices-test/part2.html)で実装内容を解説しています

# 応用形：非同期メッセージングのCDCテスト （AWS SQSのケース）
- AWS SQSで連携するプロデューサ/コンシューマアプリケーションのCDCテストを実施します
- 以下プロジェクトを使用します。ビルド方法やContractからのスタブ生成・実行方法は各プロジェクトのreadme.mdを参照してください
    - cdctest-awssqs-queue
    - cdctest-awssqs-producer
    - cdctest-awssqs-consumer
- オブジェクトの広場連載記事の[第3回](https://www.ogis-ri.co.jp/otc/hiroba/technical/microservices-test/part3.html)で実装内容を解説しています