# CDCテストの実行
## プロデューサーの起動
java -jar ./build/lib/cdctest-rest-producer-0.1.0-SNAPSHOT.jar

## コンシューマスタブ（JUnit）の実行
cd ../cdctest-rest-api

gradlew test
