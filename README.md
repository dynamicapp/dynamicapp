DynamicApp
==========

> DynamicAppはWebテクノロジーを使用してiOSやAndroidなどのネイティブアプリを作成できるフレームワークです。


ディレクトリ構成
-------------------

	  |-lib/ ........... platform code for supported operating systems, core javascripts and www package for test.
	  | |-android/
	  | |-ios/
	  | |-javascript/
	  | |-www/
	  |-license ....... the Apache Software License v2
	  |-version ....... release version in plain text
	  '-readme.md ..... release readme


*iOS版のデモアプリを動かす場合の注意点＊
-------------------
iOS版のデモアプリ等を試される場合には、build配下に作成されるRelease-framework内の”DynamicApp.framework”をプロジェクトに追加してください。

