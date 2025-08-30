# NHKニュースウィジェット

Android用のNHKニュースを表示するダークテーマ対応ウィジェットです。

## 特徴

- **ダークテーマ対応**: 現代的なダークUIデザイン
- **NHKニュースRSS**: NHK NEWS WEBの最新ニュースを取得
- **リアルタイム更新**: 30分ごとの自動更新
- **クリック対応**: ニュースをタップしてブラウザで詳細を表示
- **レスポンシブデザイン**: サイズ変更可能なウィジェット

## 技術仕様

- **最小SDK**: Android 7.0 (API 24)
- **対象SDK**: Android 14 (API 34)
- **言語**: Kotlin
- **アーキテクチャ**: MVVM パターン
- **ライブラリ**:
  - OkHttp3 (HTTP通信)
  - Jsoup (RSS解析)
  - Coroutines (非同期処理)
  - Material Design Components

## セットアップ

1. プロジェクトをクローン
2. Android Studio で開く
3. ビルドして実行

```bash
./gradlew assembleDebug
```

## ウィジェットの追加方法

1. アプリをインストール
2. ホーム画面を長押し
3. ウィジェット → NHKニュースウィジェットを選択
4. 配置して完了

## 更新履歴

### v1.0
- NHKニュースRSS対応
- ダークテーマUI
- 自動更新機能
- GitHub Actions CI/CD

## ライセンス

MIT License

## 注意事項

このアプリはNHKの公開RSSフィードを使用しています。
商用利用の際はNHKの利用規約をご確認ください。
