---
title: pg00105
...

  ------------------------------------------------------------------------
  ![](bgimg/bg00001.jpg)
  DynamicApp JavaScript API Document
  Ver.1.2
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.  
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00002.jpg)
  改編履歴
  Version 内容 ページ 更新日付
  1.0 新規作成 全て 2012/07/31
  　 新規APIを追加
  1.1
  　・AddressBook
  　・Bluetooth
  　・Bluetooth4LE
  　・Peripheral
  　・Felica
  P.59〜P.86 2012/10/08
  新規APIを追加
  　・AppVersion P.87 2012/10/15
  新規APIを一覧に追加 P.3 2012/10/08
  1.1.1
  新規APIを一覧に追加 P.4 2012/10/15
  新規APIを追加
  1.2
  　・Contacts
  　・PDFViewer
  　・Ad
  P.89〜P.104
  2013/02/15
  2013/02/15
  2013/02/15
  新規APIを一覧に追加 P.4
  Camera APIに動画撮影機能を追加 P.34, P.36
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   2
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00003.jpg)
  目次
  DynamicApp JavaScript API 一覧 4
  Encryption 7
  File 10
  FileReader 17
  FileWriter 19
  Cache 22
  Notification 　　　　　　 31
  Camera 34
  Sound 37
  Movie 41
  QRReader 48
  LoadingScreen 50
  Database 　　　　　　 53
  AddressBook 60
  Bluetooth 62
  Bluetooth4LE 66
  Peripheral 74
  Felica 81
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   3
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00004.jpg)
  AppVersion 88
  Contact 89
  PDFViewer 102
  Ad 104
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   4
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00005.jpg)
  DynamicApp JavaScript API 一覧
  No. オブジェクト 概要
  1. Encryption 文字列の暗号化/復号化機能を提供します。
  2. File ファイル操作（コピー、移動、削除）の機能を提供します
  3. FileReader ファイル読み込み機能を提供します
  4. FileWriter ファイル書き込み機能を提供します
  5. Cache アプリケーションデータやリソースのキャッシュ機能を提供します
  6. Notification デバイスのローカル通知機能を提供します
  7. Camera デバイスのカメラ機能を提供します
  8. Sound 音声再生機能を提供します
  9. Movie 動画再生機能を提供します
  10. QRReader QRリーダー機能を提供します
  11. LoadingScreen Loading screen画面を表示します
  12. Database データベース機能を提供します
  13. AddressBook 電話帳UIを表示します
  14. Bluetooth Bluetoothによる通信をサポートします
  15. Bluetooth4LE Bluetooth4LE対応デバイスとの通信をサポートします
  （※iOSのみ、ただしiPhone以前のiPhone、iPad2以前のiPadではご利用いただけません）

  16. Peripheral Bluetooth4LEデバイスを表します
  （※iOSのみ、ただしiPhone以前のiPhone、iPad2以前のiPadではご利用いただけません）

  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   5
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00006.jpg)
  No. オブジェクト 概要
  17. Felica Felicaカードへのリード/ライト機能を提供します
  （※Androidのみ）
  18. AppVersion アプリバージョンを表示します
  19. Contacts デバイス内の電話帳データ操作をサポートします
  20. PDFViewer PDFビューワ機能を提供します
  （※iOSのみ）
  21. Ad 広告機能を提供します
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   6
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00007.jpg)
  Encryption
  メソッド:
  　・encrypt
  　・decrypt 
  encrypt
  文字列を暗号化します
  Encryption.encrypt(string, successCallback, errorCallback);
  string
  暗号化対象の文字列
  successCallback
  暗号化が正常終了した場合に呼び出されます
  function(encryptedString) {
  // Do something with the encryptedString.
  }
  パラメータ
  　・encryptedString : 暗号化された文字列
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   7
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00008.jpg)
  errorCallback
  暗号化に失敗した際に呼び出されれます
  function(message) {
  // Show a helpful message.
  }
  パラメータ
  　・message : ネイティブ側からのエラーメッセージ
  decrypt
  文字列の復号化を行います。
  Encryption.decrypt(encryptedString, successCallback, errorCallback);
  encryptedString
  暗号化されている文字列
  successCallback
  復号化が正常終了した場合に呼び出されます
  function(decryptedString) {
  // Do something with the decryptedString.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   8
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00009.jpg)
  パラメータ
  　・decryptedString : 復号化された文字列
  errorCallback
  復号化に失敗した場合に呼び出されます
  function(message) {
  // Show a helpful message.
  }
  パラメータ
  　・message : ネイティブ側からのエラーメッセージ
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   9
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00010.jpg)
  File
  プロパティ:
  　・name : ファイル名
  　・fullpath : フルパス
  　・type : ファイルタイプ
  　・size : ファイルサイズ
  メソッド:
  　・コンストラクタ
  　・isFile
  　・isDirectory
  　・copy
  　・move
  　・remove
   
  コンストラクタ
  var file = new File(parentPath, filename, successCallback,
  errrorCallback);
  parentPath
  このファイルが存在する親のパス
  filename
  ファイル名
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   10
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00011.jpg)
  successCallback
  ファイルオブジェクトが正常に作成された場合に呼び出されます
  function(metadata) {
  // Do something with the metadata.
  }
  パラメータ
  　・metadata : ファイルメタデータ
  errorCallback
  ファイルオブジェクト作成に失敗した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode : エラーコード
  　　エラーコードの詳細については、P.16 エラーコードを参照のこと
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   11
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00012.jpg)
  isFile
  このファイルオブジェクトがファイルかどうかを返します
  file.isFile();
  isDirectory
  このファイルオブジェクトがディレクトリかどうかを返します
  file.isDirectory();
  copy
  ファイルのコピーを行います
  file.copy(targetDirectory, newName, successCallback, errorCallback);
  targetDirectory
  コピー先ディレクトリ
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   12
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00013.jpg)
  successCallback
  ファイルコピーが正常終了した場合に呼び出されます
  function() {
  // Do something
  }
  errorCallback
  ファイルコピーに失敗した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode : エラーコード
  　　エラーコードの詳細については、P.16 エラーコードを参照のこと
  move
  ファイルの移動を行います
  file.move(targetDirectory, newName, successCallback, errorCallback);
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   13
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00014.jpg)
  targetDirectory
  移動先ディレクトリ
  newName
  新しいファイル名
  successCallback
  ファイル移動が正常終了した場合に呼び出されます
  function() {
  // Do something
  }
  errorCallback
  ファイル移動に失敗した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
   ・errorCode : エラーコード
    エラーコードの詳細については、P.16 エラーコードを参照のこと
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   14
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00015.jpg)
  remove
  ファイルを削除します
  file.remove(successCallback, errorCallback);
  successCallback
  ファイル削除が正常終了した場合に呼び出されます
  function() {
  // Do something
  }
  errorCallback
  ファイル削除に失敗した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode : エラーコード
  　　エラーコードの詳細については、P.16 エラーコードを参照のこと
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   15
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00016.jpg)
  エラーコード
  エラーコード一覧
  　File.ERROR\_NOT\_FOUND = 1;
  　File.ERROR\_SECURITY = 2;
  　File.ERROR\_ABORT = 3;
  　File.ERROR\_NOT\_READABLE = 4;
  　File.ERROR\_ENCODING = 5;
  　File.ERROR\_NO\_MODIFICATION\_ALLOWED = 6;
  　File.ERROR\_INVALID\_STATE = 7;
  　File.ERROR\_SYNTAX = 8;
  　File.ERROR\_INVALID\_MODIFICATION = 9;
  　File.ERROR\_QUOTA\_EXCEEDED = 10;
  　File.ERROR\_TYPE\_MISMATCH = 11;
  　File.ERROR\_PATH\_EXISTS = 12;
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   16
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00017.jpg)
  FileReader
  メソッド:
  　・コンストラクタ
  　・read
  コンストラクタ
  var reader = new FileReader(file);
  file
  読み込み対象のファイルオブジェクト
  read
  読み込みを行います
  reader.read(encoding, successCallback, errorCallback);
  encoding
  ファイルで使われているエンコーディング
  successCallback
  読み込みが正常終了した場合に呼び出されます
  function(readData) {
  // Do something with the data.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   17
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00018.jpg)
  パラメータ
  　・readData: ファイルから読み込んだ内容
  errorCallback
  読み込み時にエラーが発生した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode : エラーコード
  　　エラーコードの詳細については、P.16 エラーコードを参照のこと
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   18
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00019.jpg)
  FileWriter
  プロパティ:
  　・position : ファイルポインタの現在位置
  メソッド:
  　・コンストラクタ
  　・write
  　・seek
  コンストラクタ
  var writer = new FileWriter(file);
  file
  書き込み対象のファイルオブジェクト
  write
  書き込みを行います
  writer.write(writeData, successCallback, errorCallback);
  writeData
  書き込みデータ
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   19
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00020.jpg)
  successCallback
  書き込みが正常終了した場合に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  読み込み時にエラーが発生した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode : エラーコード
  　　エラーコードの詳細については、P.16 エラーコードを参照のこと
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   20
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00021.jpg)
  seek
  ファイルポインタを指定された位置に移動します。（バイト単位）
  writer.seek(pos);
  pos
  ファイルポインタを移動させる位置
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   21
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00022.jpg)
  Cache
  メソッド:
  　・addData
  　・getList
  　・removeData
  　・resetDataCache
  　・addResource
  　・updateResource
  　・getResourceList
  　・removeResource
  　・resetResourceCache
  addData
  データキャッシュにデータを追加します
  Cache.addData(data, expiredDate, successCallback, errorCallback);
  data
  追加するデータ
  expiredDate
  有効期限（yyyy-MM-dd HH:mm:ss形式）
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   22
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00023.jpg)
  successCallback
  データのキャッシュ保存が正常終了した場合に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  データ保存失敗時に呼び出されます
  function() {
  // Do something in case of error.
  }
  getList
  キャッシュに保存されているデータリストを返します
  Cache.getList();
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   23
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00024.jpg)
  removeData
  キャッシュからデータを削除します
  Cache.removeData(id, successCallback, errorCallback);
  id
  データID
  successCallback
  データ削除が正常終了した場合に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  データ削除失敗時に呼び出されます
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   24
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00025.jpg)
  resetDataCache
  データキャッシュをクリアします
  Cache.resetDataCache(successCallback, errorCallback);
  successCallback
  クリアが正常終了した場合に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  クリアに失敗した際に呼び出されます
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   25
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00026.jpg)
  addResource
  リソース（URL）データをキャッシュに追加します。
  追加されたデータURLからリソースのダウンロードを行います。
  Cache.addResource(remoteContentsPath, expiredDate, successCallback,
  errorCallback);
  remoteContentsPath
  リモートコンテンツパス
  expiredDate
  有効期限（yyyy-MM-dd HH:mm:ss形式）
  successCallback
  データ追加が正常終了した場合に呼び出されます
  function(result) {
  // Do something with result.
  }
  パラメータ
  　・result : 結果情報
  　　 result.id : ダウンロードしたデータID
  　　　result.expireDate : データの有効期限
  　　　result.fullpath : ダウンロードデータの保存先
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   26
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00027.jpg)
  errorCallback
  データ追加失敗時に呼び出されます
  function() {
  // Do something in case of error.
  }
  updateResource
  リソースデータを更新します（ダウンロードも含む）
  Cache.updateResource(id, nextexpiredDate, successCallback,
  errorCallback);
  id
  データID
  nextexpiredDate
  次の有効期限（yyyy-MM-dd HH:mm:ss形式）
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   27
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00028.jpg)
  successCallback
  更新が正常終了した場合に呼び出されます
  function(result) {
  // Do something.
  }
  パラメータ
  　・result : 結果情報
  　　result.id : ダウンロードしたデータID
  　　result.expireDate : データの有効期限
  　　result.fullpath : ダウンロードデータの保存先
  　
  errorCallback
  更新失敗時に呼び出されます
  function() {
  // Do something in case of error.
  }
  getResourceList
  データリストを取得します
  Cache.getResourceList();
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   28
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00029.jpg)
  removeResource
  キャッシュからリソースデータを削除します
  Cache.removeResource(id, successCallback, errorCallback);
  id
  データID
  successCallback
  データ削除が正常終了した場合に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  データ削除時にエラーが発生した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   29
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00030.jpg)
  resetResourceCache
  リソースキャッシュをクリアします
  Cache.resetResourceCache(successCallback, errorCallback);
  successCallback
  キャッシュクリアが正常終了した場合に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  キャッシュクリア時にエラーが発生した場合に呼び出されます（主にファイル削除失敗）

  function(errorCode) {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   30
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00031.jpg)
  Notification
  メソッド:
  　・notify
  　・cancelNotification
  notify
  指定された時間に、指定されたメッセージを通知します（ローカル通知）
  Notification.notify( date, message, [options] );
  date
  通知を行う日付（yyyy-MM-dd HH:mm:ss形式）
  message
  通知メッセージ
  options
  通知関係のオプションパラメータ（iOSのみ有効）
  { badge : 1,
  hasAction : true,
  action : “ 起動 ” };
  options
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   31
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00032.jpg)
  オプション
  　 ・budge: アプリケーションアイコンに表示するバッジ数
  　・hasAction: アクションボタンを表示するかどうか
  　・action: アクションボタンのタイトル
  cancelNotification
  通知をキャンセルします
  Notification.cancelNotification(date, successCallback, errorCallback);
  date
  キャンセルする通知日付
  successCallback
  キャンセルが正常に行われた場合に呼び出されます
  function() {
  // Do something.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   32
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00033.jpg)
  errorCallback
  キャンセルに失敗した場合に呼び出されます
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   33
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00034.jpg)
  Camera
  メソッド:
  　・getPicture
  　・recordVideo
  getPicture
  カメラ、もしくはライブラリから画像を取得します。
  画像のBase64エンコード文字列か画像ファイルのURIを返します
  Camera.getPicture( successCallback, errorCallback, [ options ] );
  successCallback
  画像の取得が正常に行われた場合に呼び出されます
  function(imageData) {
  // Do something with the image.
  }
  パラメータ
  　・imageData:
  画像データ（Base64エンコード文字列）もしくは、画像ファイルのURI
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   34
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00035.jpg)
  errorCallback
  画像取得に失敗した場合に呼び出されます
  function(message) {
  // Show a helpful message.
  }
  パラメータ
  　・message: ネイティブ側からのエラーメッセージ
  options
  カスタマイズオプション
  { quality : 75,
  destinationType : Camera.DestinationType.DATA\_URL,
  sourceType : Camera.PictureSourceType.CAMERA,
  encodingType: Camera.EncodingType.JPEG,
  targetWidth: 100,
  targetHeight: 100 };
  オプション
  　 ・quality: 画質( 0\~100)
  　・destinationType: 戻り値のフォーマット
  　・sourceType: 画像ソース
  　・encodingType: 画像のエンコードタイプ
  　・targetWidth: 取得画像の幅
  　・targetHeight: 取得画像の高さ
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   35
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00036.jpg)
  recordVideo
  動画の撮影を行います。
  Camera.recordVideo(successCallback, errorCallback);
  successCallback
  動画撮影が正常に行われた場合に呼び出されます
  function(videoData) {
  // Do something with the videoData.
  }
  パラメータ
  　・videoData: 撮影した動画ファイルへのURI
  errorCallback
  動画撮影に失敗した場合に呼び出されます
  function(message) {
  // Show a helpful message.
  }
  パラメータ
  　・message: ネイティブ側からのエラーメッセージ
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   36
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00037.jpg)
  Sound
  メソッド:
  　・コンストラクタ
  　・play
  　・pause
  　・stop
  　・release
  　・getCurrentPosition
  　・setCurrentPosition
  　・getDuration
  コンストラクタ
  var sound = new Sound(src, [successCallback], [errorCallback] );
  src
  サウンドコンテンツへのURI
  successCallback
  再生、一時停止、停止など全てのアクションが正常に終了した場合に呼び出されます

  function() {
  // Do something
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   37
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00038.jpg)
  errorCallback
  再生、一時停止、停止など全てのアクションに於いてエラーが発生した場合に呼び出されます

  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode: エラーコード
  　　　SOUND\_ERR\_ABORTED = 1
  　　　SOUND\_ERR\_NETWORK = 2
  　　　SOUND\_ERR\_DECODE = 3
  　　　 SOUND\_ERR\_NONE\_SUPPORTED = 4
  play
  サウンドを再生します
  sound.play([numberOfLoops:] );
  numberOfLoops
  ループ回数
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   38
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00039.jpg)
  pause
  サウンドの再生を一時停止します
  sound.pause( );
  stop
  サウンドの再生を停止します
  sound.stop( );
  release
  このオブジェクトで使用していたサウンドリソースを解放します
  sound.release();
  getCurrentPosition
  現在の再生位置を取得します
  sound.getCurrentPosition();
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   39
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00040.jpg)
  setCurrentPosition
  現在の再生位置（秒）を設定します
  sound.setCurrentPosition(pos, [func]);
  pos
  再生位置（秒）
  func
  再生位置変更後に行うファンクション
  getDuration
  再生時間を取得します
  sound.getDuration();
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   40
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00041.jpg)
  Movie
  メソッド:
  　・コンストラクタ
  　・play
  　・pause
  　・stop
  　・release
  　・getDuration
  　・getCurrentPosition
  　・setCurrentPosition
  　・getThumbnail
  コンストラクタ
  var movie = new Movie(src, [successCallback], [errorCallback],
  [options]);
  src
  動画コンテンツへのURI
  successCallback
  再生、一時停止、停止など全てのアクションが正常に終了した場合に呼び出されます

  function() {
  // Do something
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   41
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00042.jpg)
  errorCallback
  再生、一時停止、停止など全てのアクションに於いてエラーが発生した場合に呼び出されます

  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode：エラーコード
  　　　MOVIE\_ERR\_ABORTED = 1
  　　　MOVIE\_ERR\_NETWORK = 2
  　　　MOVIE\_ERR\_DECODE = 3
  　　　MOVIE\_ERR\_NONE\_SUPPORTED = 4
  options
  ムービープレーヤーオプション
  { frame : {posX : 0, posY : 0, width : 100, height : 100},
  scalingMode : Movie.SCALING\_ASPECT\_FIT,
  controlStyle : Movie.CONTROL\_NONE };
  オプション
  　・frame: ムービープレーヤー表示位置、サイズ
  　・scalingMode: スケーリングモード
  　・controlStyle: コントロールスタイル
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   42
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00043.jpg)
  play
  ムービーの再生開始を行います
  movie.play( [options]);
  options
  ムービープレーヤーオプション
  { frame : {posX : 0, posY : 0, width : 100, height : 100},
  scalingMode : Movie.SCALING\_ASPECT\_FIT,
  controlStyle : Movie.CONTROL\_NONE };
  オプション
  　 ・frame: ムービープレーヤー表示位置、サイズ
  　・scalingMode: スケーリングモード
  　・controlStyle: コントロールスタイル
  pause
  ムービーの再生を一時停止します
  movie.pause( );
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   43
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00044.jpg)
  stop
  ムービーの再生を停止します
  movie.stop( );
  release
  このオブジェクトで使用していたムービーリソースを解放します
  movie.release();
  getDuration
  ムービーの再生時間を取得します
  movie.getDuration();
  getCurrentPosition
  現在の再生位置（秒）を取得します
  movie.getCurrentPosition();
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   44
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00045.jpg)
  setCurrentPosition
  現在の再生位置（秒）を設定します
  movie.setCurrentPosition(pos, [func]);
  pos
  再生位置（秒）
  func
  再生位置変更後に行うファンクション
  getThumbnail
  ムービーのサムネイルを取得します
  movie.getThumbnail(successCallback, errorCallback, [options]);
  successCallback
  サムネイルの取得が正常に行われた場合に呼び出されます
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   45
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00046.jpg)
  function(data) {
  // Do something
  }
  パラメータ
  　・data: Base64 エンコーディングされたイメージデータ
  errorCallback
  サムネイル取得時にエラーが発生した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode: エラーコード
  　　 MOVIE\_ERR\_NONE\_SUPPORTED = 4
  options
  サムネイル画像取得に関するオプションを設定します
  { quality : 75
  width : 100,
  height : 100,
  offset : 0 };
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   46
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00047.jpg)
  オプション
  　・quality: サムネイル画像の画質（0\~100）
  　・width: サムネイル画像の幅
  　・height: サムネイル画像の高さ
  　・offset:
  ムービー開始位置からのオフセット（どの位置のサムネイルを取得するか）※iOSのみ

  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   47
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00048.jpg)
  QRReader
  メソッド:
  ・read 
  read
  QRコードを読み込みます
  QRReader.read(successCallback , errorCallback, [options]);
  successCallback
  QRコード読み込みが正常終了した際に呼び出されます
  function(dataString) {
  // Do something with the string.
  }
  パラメータ
  　・dataString: QRコードデータ
  errorCallback
  QRコード読み込み時に失敗した際に呼び出されます
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   48
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00049.jpg)
  options
  QRコード読み込み時のオプションを設定します
  {source: QRReader.SOURCE\_CAMERA};
  オプション
  　・source: どこからQRコードを読み込むか
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   49
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00050.jpg)
  LoadingScreen
  メソッド:
  　・startLoad
  　・stopLoad
  startLoad
  ローディングビューの表示を開始します
  LoadingScreen.startLoad( successCallback, errorCallback, [ options ] );
  successCallback
  ローディングスクリーン表示が正常に開始された場合に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  ローディングスクリーン表示時にエラーが発生した場合に呼び出されます
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   50
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00051.jpg)
  options
  表示オプションを設定します
  {label: "Loading...",
  style:LoadingScreen.STYLEWHITELARGE,
  isBlackBackground:true};
  オプション
  　・label: ローディングスクリーンに表示する文字列
  　・style: ローディングスクリーンのスタイル
  　・isBlackBackgroud: バックグラウンドが黒かどうか
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   51
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00052.jpg)
  stopLoad
  ローディングスクリーンの表示を停止します
  LoadingScreen.stoptLoad(successCallback, errorCallback);
  successCallback
  ローディングスクリーン停止が正常に完了した際に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  ローディングスクリーン停止時にエラーが発生した場合に呼び出されます
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   52
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00053.jpg)
  Database
  メソッド:
  　・コンストラクタ
  　・executeSQL
  　・beginTrans
  　・commit
  　・rollback
  コンストラクタ
  var db = new Database(dbName, successCallback, errorCallback);
  dbName
  データベース名
  successCallback
  データベースオブジェクトが正常に作成された場合に呼び出されます
  function() {
  // Do something.
  }
  errorCallback
  データベースオブジェクト作成時にエラーが発生した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   53
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00054.jpg)
  パラメータ
  　・errorCode: エラーコード
  　　エラーコードの詳細については、P.59 エラーコードを参照のこと
  executeSQL
  SQLクエリを実行します
  db.executeSQL(query, args, successCallback, errorCallback);
  query
  クエリ文字列
  args
  クエリがSELECT文かどうかを指定する
  ※次期バージョン以降、プリペアードステートメントに対応する予定
  successCallback
  クエリ実行が正常終了した場合に呼び出されます
  ※SELECT文の場合、該当データがJSONオブジェクトで返されます
  function(result) {
  // Do something.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   54
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00055.jpg)
  errorCallback
  クエリー実行時に失敗した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode: エラーコード
  　　エラーコードの詳細については、P.59 エラーコードを参照のこと
  beginTrans
  トランザクションを開始します
  db.beginTrans(successCallback, errorCallback);
  successCallback
  トランザクション開始が正常終了した場合に呼び出されます
  function() {
  // Do something.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   55
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00056.jpg)
  errorCallback
  トランザクション開始に失敗した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode: エラーコード
  　　エラーコードの詳細については、P.59 エラーコードを参照のこと
  commitTrans
  トランザクションをコミットします
  db.commitTrans(successCallback, errorCallback);
  successCallback
  コミットに成功した場合に呼び出されます
  function() {
  // Do something.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   56
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00057.jpg)
  errorCallback
  コミットに失敗した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode: エラーコード
  　　エラーコードの詳細については、P.59 エラーコードを参照のこと
  rollback
  トランザクションをロールバックします
  db.rollback(successCallback, errorCallback);
  successCallback
  ロールバックに成功した場合に呼び出されます
  function() {
  // Do something.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   57
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00058.jpg)
  errorCallback
  ロールバックに失敗した場合に呼び出されます
  function(errorCode) {
  // Do something in case of error.
  }
  パラメータ
  　・errorCode: エラーコード
  　　エラーコードの詳細については、P.59 エラーコードを参照のこと
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   58
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00059.jpg)
  エラーコード
  エラーコード一覧
  Database.SQLITE\_ERROR = 1; // SQL error or missing database
  Database.SQLITE\_INTERNAL = 2; // Internal logic error in SQLite
  Database.SQLITE\_PERM = 3; // Access permission denied
  Database.SQLITE\_ABORT = 4; // Callback routine requested an abort
  Database.SQLITE\_BUSY = 5; // The database file is locked
  Database.SQLITE\_LOCKED = 6; // A table in the database is locked
  Database.SQLITE\_NOMEM = 7; // A malloc() failed
  Database.SQLITE\_READONLY = 8; // Attempt to write a readonly database
  Database.SQLITE\_INTERRUPT = 9; // Operation terminated by
  sqlite3\_interrupt()
  Database.SQLITE\_IOERR = 10; // Some kind of disk I/O error occurred
  Database.SQLITE\_CORRUPT = 11; // The database disk image is malformed
  Database.SQLITE\_NOTFOUND = 12; // Unknown opcode in
  sqlite3\_file\_control()
  Database.SQLITE\_FULL = 13; // Insertion failed because database is full
  Database.SQLITE\_CANTOPEN = 14; // Unable to open the database file
  Database.SQLITE\_PROTOCOL = 15; // Database lock protocol error
  Database.SQLITE\_EMPTY = 16; // Database is empty
  Database.SQLITE\_SCHEMA = 17; // The database schema changed
  Database.SQLITE\_TOOBIG = 18; // String or BLOB exceeds size limit
  Database.SQLITE\_CONSTRAINT = 19; // Abort due to constraint violation
  Database.SQLITE\_MISMATCH = 20; // Data type mismatch
  Database.SQLITE\_MISUSE = 21; // Library used incorrectly
  Database.SQLITE\_NOLFS = 22; // Uses OS features not supported on host
  Database.SQLITE\_AUTH = 23; // Authorization denied
  Database.SQLITE\_FORMAT = 24; // Auxiliary database format error
  Database.SQLITE\_RANGE = 25; // 2nd parameter to sqlite3\_bind out of
  range
  Database.SQLITE\_NOTADB = 26; // File opened that is not a database file
  Database.SQLITE\_ROW = 100; // sqlite3\_step() has another row ready
  Database.SQLITE\_DONE = 101; // sqlite3\_step() has finished executing
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   59
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00060.jpg)
  AddressBook
  メソッド:
  　・show
  　・showForSelection
  　
  show
  電話帳UIを表示します。
  AddressBook.show(successCallback, errorCallback);
  successCallback
  電話帳UIが正常に表示された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  電話帳UI表示時にエラーが発生した場合に呼び出されます。
  function(message) {
  // Do something in case of error.
  }
  パラメータ
  　・message: ネイティブ側からのエラーメッセージ
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   60
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00061.jpg)
  showForSelection
  連絡先情報取得UIを表示します。
  AddressBook.showForSelection(successCallback, errorCallback);
  successCallback
  連絡先が正常に取得された場合に呼び出されます。
  function(data) {
  // Do something.
  }
  パラメータ
  　・data: 選択したアドレス情報
  連絡先取得時にエラーが発生した場合に呼び出されます。
  function(message) {
  // Do something in case of error.
  }
  パラメータ
  　・message: ネイティブ側からのエラーメッセージ
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   61
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00062.jpg)
  Bluetooth
  プロパティ：
  　・peerList
  　・onRecvCallback
  メソッド：
  　・discover
  　・connect
  　・disconnect
  　・send
  discover
  デバイスの検索を開始します。
  　
  Bluetooth.discover();
  connect
  デバイスに接続します。
  Bluetooth.connect(peerData, successCallback, errorCallback);
  peerData
  デバイス情報
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   62
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00063.jpg)
  successCallback
  接続が正常に取得された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  接続に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  disconnect
  デバイスの接続を解除します。
  Bluetooth.disconnect(peerData, successCallback, errorCallback);
  peerData
  デバイス情報
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   63
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00064.jpg)
  successCallback
  接続解除が正常に取得された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  接続解除に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  send
  デバイスへデータ送信を行います。
  Bluetooth.send(peerData, sendData, successCallback, errorCallback);
  peerData
  デバイス情報
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   64
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00065.jpg)
  sendData
  送信データ（String）
  successCallback
  正常にデータ送信された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  データ送信に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   65
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00066.jpg)
  Bluetooth4LE
  ※ご注意※
  Androidでは現在ご利用頂けません。
  iPhone4以前のiPhone、iPad2以前のiPadではご利用頂けません。
  定数：
  　Services
  　 ・Bluetooth4LE.SERVICE\_ALERT\_NOTIFICATION = '1811'
  　 ・Bluetooth4LE.SERVICE\_BATTERY\_SERVICE = '180F'
  　・Bluetooth4LE.SERVICE\_BLOOD\_PRESSURE = '1810'
  　・Bluetooth4LE.SERVICE\_CURRENT\_TIME = '1805'
  　・Bluetooth4LE.SERVICE\_CYCLINGSPEED\_AND\_CADENCE = '1816'
  　・Bluetooth4LE.SERVICE\_DEVICE\_INFORMATION = '180A'
  　・Bluetooth4LE.SERVICE\_GENERIC\_ACCESS = '1800'
  　・Bluetooth4LE.SERVICE\_GENERIC\_ATTRIBUTE = '1801'
  　・Bluetooth4LE.SERVICE\_GLUCOSE = '1808'
  　・Bluetooth4LE.SERVICE\_HEALTH\_THERMOMETER = '1809'
  　・Bluetooth4LE.SERVICE\_HEART\_RATE = '180D'
  　・Bluetooth4LE.SERVICE\_HUMAN\_INTERFACE\_DEVICE = '1812'
  　・Bluetooth4LE.SERVICE\_IMMEDIATE\_ALERT = '1802'
  　・Bluetooth4LE.SERVICE\_HUMAN\_LINK\_LOSS = '1803'
  　・Bluetooth4LE.SERVICE\_NEXT\_DST\_CHANGE = '1807'
  　・Bluetooth4LE.SERVICE\_PHONE\_ALERT\_STATUS = '180E'
  　・Bluetooth4LE.SERVICE\_REFERENCE\_TIME\_UPDATE = '1806'
  　・Bluetooth4LE.SERVICE\_SCAN\_PARAMETERS = '1813'
  　・Bluetooth4LE.SERVICE\_TX\_POWER = '1804'
  　Characteristics
  　・Bluetooth4LE.CHARACTERISTICS\_ALERT\_CATEGORY\_ID = '2A43'
  　・Bluetooth4LE.CHARACTERISTICS\_ALERT\_CATEGORY\_ID\_BIT\_MASK =
  '2A42'
  　・Bluetooth4LE.CHARACTERISTICS\_ALERT\_LEVEL = '2A06'
  　・Bluetooth4LE.CHARACTERISTICS\_ALERT\_NOTIFICATION\_CONTROL\_POINT =
  '2A 44'
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   66
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00067.jpg)
  　 ・Bluetooth4LE.CHARACTERISTICS\_ALERT\_STATUS = '2A3F'
  　・Bluetooth4LE.CHARACTERISTICS\_APPEARANCE = '2A01'
  ・Bluetooth4LE.CHARACTERISTICS\_BATTERY\_LEVEL = '2A19'
  ・Bluetooth4LE.CHARACTERISTICS\_BLOOD\_PRESSURE\_FEATURE = '2A49'　・
  ・Bluetooth4LE.CHARACTERISTICS\_BLOOD\_PRESSURE\_MEASUREMENT = '2A35'
  ・Bluetooth4LE.CHARACTERISTICS\_BODY\_SENSOR\_LOCATION = '2A38'
  ・Bluetooth4LE.CHARACTERISTICS\_BOOT\_KEYBOARD\_INPUT\_REPORT = '2A22'
  ・Bluetooth4LE.CHARACTERISTICS\_BOOT\_KEYBOARD\_OUTPUT\_REPORT = '2A32'
  ・Bluetooth4LE.CHARACTERISTICS\_BOOT\_MOUSE\_INPUT\_REPORT = '2A33'
  ・Bluetooth4LE.CHARACTERISTICS\_CURRENT\_TIME = '2A2B'
  ・Bluetooth4LE.CHARACTERISTICS\_DATE\_TIME = '2A08'
  ・Bluetooth4LE.CHARACTERISTICS\_DAY\_DATE\_TIME = '2A0A'
  ・Bluetooth4LE.CHARACTERISTICS\_DAY\_OF\_WEEK = '2A09'
  　・Bluetooth4LE.CHARACTERISTICS\_DEVICE\_NAME = '2A00'
  　・Bluetooth4LE.CHARACTERISTICS\_DST\_OFFSET = '2A0D'
  　・Bluetooth4LE.CHARACTERISTICS\_EXACT\_TIME\_256 = '2A0C'
  　・Bluetooth4LE.CHARACTERISTICS\_FIRMWARE\_REVISION\_STRING = '2A26'
  　・Bluetooth4LE.CHARACTERISTICS\_GLUCOSE\_FEATURE = '2A51'
  　・Bluetooth4LE.CHARACTERISTICS\_GLUCOSE\_MEASUREMENT = '2A18'
  　・Bluetooth4LE.CHARACTERISTICS\_GLUCOSE\_MEASUREMENT\_CONTEXT = '2A18'
  　・Bluetooth4LE.CHARACTERISTICS\_HARDWARE\_REVISION\_STRING = '2A27'
  　・Bluetooth4LE.CHARACTERISTICS\_HEART\_RATE\_CONTROL\_POINT = '2A39'
  　・Bluetooth4LE.CHARACTERISTICS\_HEART\_RATE\_MEASUREMENT = '2A37'
  　・Bluetooth4LE.CHARACTERISTICS\_HID\_CONTROL\_POINT = '2A4C'
  　・Bluetooth4LE.CHARACTERISTICS\_HID\_INFORMATION = '2A4A'
  　・Bluetooth4LE.CHARACTERISTICS\_IEEE\_11073-20601\_REGULATORY\_CERTIFICA
  TION\_DATA\_LIST
  = '2A2A'
  　・Bluetooth4LE.CHARACTERISTICS\_INTERMEDIATE\_BLOOD\_PRESSURE = '2A36'
  　・Bluetooth4LE.CHARACTERISTICS\_INTERMEDIATE\_TEMPERATURE = '2A1E'
  　・Bluetooth4LE.CHARACTERISTICS\_LOCAL\_TIME\_INFORMATION = '2A0F'
  　・Bluetooth4LE.CHARACTERISTICS\_MANUFACTURER\_NAME\_STRING = '2A29'
  　・Bluetooth4LE.CHARACTERISTICS\_MEASUREMENT\_INTERVAL = '2A21'
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   67
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00068.jpg)
  　 ・Bluetooth4LE.CHARACTERISTICS\_MODEL\_NUMBER\_STRING = '2A24'
  ・Bluetooth4LE.CHARACTERISTICS\_NEW\_ALERT = '2A46'
  ・Bluetooth4LE.CHARACTERISTICS\_PERIPHERAL\_PREFERRED\_CONNECTION\_PARAME
  TERS
  = '2A04'
  ・Bluetooth4LE.CHARACTERISTICS\_PERIPHERAL\_PRIVACY\_FLAG = '2A02'
  ・Bluetooth4LE.CHARACTERISTICS\_PNP\_ID = '2A50'
  ・Bluetooth4LE.CHARACTERISTICS\_PROTOCOL\_MODE = '2A4E'
  ・Bluetooth4LE.CHARACTERISTICS\_RECONNECTION\_ADDRESS = '2A03'
  ・Bluetooth4LE.CHARACTERISTICS\_RECORD\_ACCESS\_CONTROL\_POINT = '2A52'
  ・Bluetooth4LE.CHARACTERISTICS\_REFERENCE\_TIME\_INFORMATION = '2A14'
  ・Bluetooth4LE.CHARACTERISTICS\_REPORT = '2A4D'
  ・Bluetooth4LE.CHARACTERISTICS\_REPORT\_MAP = '2A4B'
  ・Bluetooth4LE.CHARACTERISTICS\_RINGER\_CONTROL\_POINT = '2A40'
  ・Bluetooth4LE.CHARACTERISTICS\_RINGER\_SETTING = '2A41'
  ・Bluetooth4LE.CHARACTERISTICS\_SCAN\_INTERVAL\_WINDOW = '2A4F'
  ・Bluetooth4LE.CHARACTERISTICS\_SCAN\_REFRESH = '2A31'
  ・Bluetooth4LE.CHARACTERISTICS\_SERIAL\_NUMBER\_STRING = '2A25'
  ・Bluetooth4LE.CHARACTERISTICS\_SERVICE\_CHANGED = '2A05'
  ・Bluetooth4LE.CHARACTERISTICS\_SOFTWARE\_REVISION\_STRING = '2A28'
  ・Bluetooth4LE.CHARACTERISTICS\_SUPPORTED\_NEW\_ALERT\_CATEGORY = '2A47'
  ・Bluetooth4LE.CHARACTERISTICS\_SUPPORTED\_UNREAD\_ALERT\_CATEGORY =
  '2A48'
  ・Bluetooth4LE.CHARACTERISTICS\_SYSTEM\_ID = '2A23'
  ・Bluetooth4LE.CHARACTERISTICS\_TEMPERATURE\_MEASUREMENT = '2A1C'
  ・Bluetooth4LE.CHARACTERISTICS\_TEMPERATURE\_TYPE = '2A1D'
  ・Bluetooth4LE.CHARACTERISTICS\_TIME\_ACCURACY = '2A12'
  ・Bluetooth4LE.CHARACTERISTICS\_TIME\_SOURCE = '2A13'
  ・Bluetooth4LE.CHARACTERISTICS\_TIME\_UPDATE\_CONTROL\_POINT = '2A16'
  ・Bluetooth4LE.CHARACTERISTICS\_TIME\_UPDATE\_STATE = '2A17'
  ・Bluetooth4LE.CHARACTERISTICS\_TIME\_WITH\_DST = '2A11'
  ・Bluetooth4LE.CHARACTERISTICS\_TIME\_ZONE = '2A0E'
  ・Bluetooth4LE.CHARACTERISTICS\_TX\_POWER\_LEVEL = '2A07'
  ・Bluetooth4LE.CHARACTERISTICS\_UNREAD\_ALERT\_STATUS = '2A45'
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   68
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00069.jpg)
  　Descriptors
  ・Bluetooth4LE.DESCRIPTORS\_CHARACTERISTIC\_AGGREGATE\_FORMAT = '2905'
  ・Bluetooth4LE.DESCRIPTORS\_CHARACTERISTIC\_EXTENDED\_PROPERTIES =
  '2900'
  ・Bluetooth4LE.DESCRIPTORS\_CHARACTERISTIC\_PRESENTATION\_FORMAT =
  '2904'
  ・Bluetooth4LE.DESCRIPTORS\_CHARACTERISTIC\_USER\_DESCRIPTION = '2901'
  ・Bluetooth4LE.DESCRIPTORS\_CLIENT\_CHARACTERISTIC\_CONFIGURATION =
  '2902'
  ・Bluetooth4LE.DESCRIPTORS\_EXTERNAL\_REPORT\_REFERENCE = '2907'
  ・Bluetooth4LE.DESCRIPTORS\_REPORT\_REFERENCE = '2908'
  ・Bluetooth4LE.DESCRIPTORS\_SERVER\_CHARACTERISTIC\_CONFIGURATION =
  '2903'
  ・Bluetooth4LE.DESCRIPTORS\_VALID\_RANGE = '2906'
  　ValueType
  ・Bluetooth4LE.VALUE\_TYPE\_UUID = 0
  ・Bluetooth4LE.VALUE\_TYPE\_BOOLEAN = 1
  ・Bluetooth4LE.VALUE\_TYPE\_2BIT = 2
  ・Bluetooth4LE.VALUE\_TYPE\_NIBBLE = 3
  ・Bluetooth4LE.VALUE\_TYPE\_8BIT = 4
  ・Bluetooth4LE.VALUE\_TYPE\_16BIT = 5
  ・Bluetooth4LE.VALUE\_TYPE\_24BIT = 6
  ・Bluetooth4LE.VALUE\_TYPE\_32BIT = 7
  ・Bluetooth4LE.VALUE\_TYPE\_UINT8 = 8
  ・Bluetooth4LE.VALUE\_TYPE\_UINT12 = 9
  ・Bluetooth4LE.VALUE\_TYPE\_UINT16 = 10
  ・Bluetooth4LE.VALUE\_TYPE\_UINT24 = 11
  ・Bluetooth4LE.VALUE\_TYPE\_UINT32 = 12
  ・Bluetooth4LE.VALUE\_TYPE\_UINT40 = 13
  ・Bluetooth4LE.VALUE\_TYPE\_UINT48 = 14
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   69
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00070.jpg)
  ・Bluetooth4LE.VALUE\_TYPE\_UINT64 = 15
  ・Bluetooth4LE.VALUE\_TYPE\_UINT128 = 16
  ・Bluetooth4LE.VALUE\_TYPE\_SINT8 = 17
  ・Bluetooth4LE.VALUE\_TYPE\_SINT12 = 18
  ・Bluetooth4LE.VALUE\_TYPE\_SINT16 = 19
  ・Bluetooth4LE.VALUE\_TYPE\_SINT24 = 20
  ・Bluetooth4LE.VALUE\_TYPE\_SINT32 = 21
  ・Bluetooth4LE.VALUE\_TYPE\_SINT48 = 22
  ・Bluetooth4LE.VALUE\_TYPE\_SINT64 = 23
  ・Bluetooth4LE.VALUE\_TYPE\_SINT128 = 24
  ・Bluetooth4LE.VALUE\_TYPE\_FLOAT32 = 25
  ・Bluetooth4LE.VALUE\_TYPE\_FLOAT64 = 26
  ・Bluetooth4LE.VALUE\_TYPE\_SFLOAT = 27
  ・Bluetooth4LE.VALUE\_TYPE\_FLOAT = 28
  ・Bluetooth4LE.VALUE\_TYPE\_DUNIT16 = 29
  ・Bluetooth4LE.VALUE\_TYPE\_UTF8S = 30
  ・Bluetooth4LE.VALUE\_TYPE\_UTF16S = 31
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   70
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00071.jpg)
  プロパティ：
  　・peripheralList
  メソッド：
  　・scanDevices
  　・connect
  　・disconnect
  scanDevices
  デバイスのスキャンを開始します。
  Bluetooth4LE.scanDevices(services);
  　 services
  検索対象サービス(Array)
  connect
  デバイスへ接続します。
  Bluetooth4LE.connect(peripheral, successCallback, errorCallback);
  peripheral
  デバイス情報
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   71
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00072.jpg)
  successCallback
  接続が正常に取得された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  接続に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  disconnect
  デバイスとの接続を解除します。
  Bluetooth4LE.disconnect(peripheral, successCallback, errorCallback);
  peripheral
  デバイス情報
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   72
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00073.jpg)
  successCallback
  接続解除が正常に取得された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  接続解除に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   73
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00074.jpg)
  Peripheral
  ※ご注意※
  Androidでは現在ご利用頂けません。
  iPhone4以前のiPhone、iPad2以前のiPadではご利用頂けません。
  プロパティ：
  　・deviceName : デバイス名
  　・id : デバイスID（iOS:UUID/Android:MAC Address）
  メソッド：
  　・writeValueForCharacteristic
  　・readValueForCharacteristic
  　・writeValueForDescriptor
  　・readValueForDescriptor
  writeValueForCharacteristic
  キャラクタリスティクに値を書き込みます。
  peripheral.writeValueForCharacteristic(value, valueType, characteristic,
  »   writeType, successCallback, errorCallback);
  　 value
  書き込む値
  valueType
  書き込む値のタイプ
  service
  対象のサービス
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   74
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00075.jpg)
  characteristic
  対象のキャラクタリスティック
  writeType
  書き込みタイプ
  　- WriteWithResponse : 0
  　- WriteWithoutResponse : 1
  successCallback
  書き込みが正常終了した場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  書き込みに失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   75
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00076.jpg)
  readValueForCharacteristic
  キャラクタリスティクから値を読み込みます。
  peripheral.readValueForCharacteristic(service, characteristic,
  valueType, successCallback, errorCallback);
  service
  対象のサービス
  characteristic
  対象のキャラクタリスティック
  valueType
  読み込む値のタイプ
  successCallback
  読み込みが正常に取得された場合に呼び出されます。
  function(value) {
  // Do something.
  }
  パラメータ
  　・value: 読み込んだ値
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   76
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00077.jpg)
  errorCallback
  読み込みに失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  writeValueForDescriptor
  ディスクリプタに値を書き込みます。
  peripheral.writeValueForDescriptor(value, valueType, service,
  characteristic, descriptor,
  »   successCallback, errorCallback);
  value
  書き込む値
  valueType
  書き込む値のタイプ
  service
  対象のサービス
  characteristic
  対象のキャラクタリスティック
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   77
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00078.jpg)
  descriptor
  対象のディスクリプタ
  successCallback
  書き込みが正常に取得された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  書き込みに失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   78
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00079.jpg)
  readValueForDescriptor
  ディスクリプタから値を読み込みます。
  peripheral.readValueForDescriptor(service, characteristic, descriptor,
  valueType,
  »   successCallback, errorCallback);
  service
  対象のサービス
  characteristic
  対象のキャラクタリスティック
  descriptor
  対象のディスクリプタ
  successCallback
  読み込みが正常終了した場合に呼び出されます。
  function(value) {
  // Do something with the value.
  }
  パラメータ
  　・value: 読み込んだ値
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   79
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00080.jpg)
  errorCallback
  読み込みに失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   80
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00081.jpg)
  Felica
  ※ご注意※
  iOSではご利用頂けません。
  またAndroidでご使用の際はNFC機能を搭載した端末が必要です。
  メソッド：
  　・getIDm
  　・getPMm
  　・requestSystemCode
  　・requestServiceCode
  　・requestResponse
  　・readWithoutEncryption
  　・writeWithoutEncryption
  getIDm
  IDmを取得します。
  Felica.getIDm(successCallback, errorCallback);
  　
  successCallback
  IDmが正常に取得された場合に呼び出されます。
  function(IDm) {
  // Do something with the IDm.
  }
  パラメータ
  　・IDm: 取得したIDm(String)
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   81
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00082.jpg)
  errorCallback
  取得に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  getPMm
  PMmを取得します。
  Felica.getPMm(successCallback, errorCallback);
  successCallback
  PMmが正常に取得された場合に呼び出されます。
  function(PMm) {
  // Do something with the PMm.
  }
  パラメータ
  　・PMm: 取得したPMm(String)
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   82
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00083.jpg)
  errorCallback
  取得に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  requestSystemCode
  システムコード取得要求を送信します。
  Felica.getPMm(successCallback, errorCallback);
  successCallback
  システムコードが正常に取得された場合に呼び出されます。
  function(systemcodes) {
  // Do something with the system codes.
  }
  パラメータ
  　・systemcodes: 取得したシステムコード(Array)
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   83
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00084.jpg)
  errorCallback
  取得に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  requestResponse
  応答要求を行います
  Felica.requestResponse(successCallback, errorCallback);
  successCallback
  応答要求が正常に行われた場合に呼び出されます。
  function(mode) {
  // Do something with the mode.
  }
  パラメータ
  　・mode: システムの最新状態
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   84
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00085.jpg)
  errorCallback
  取得に失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  readWithoutEncryption
  認証を必要としないサービスからブロックデータを読み出します。
  Felica.readWithoutEncryption(serviceCode, addr, successCallback,
  errorCallback);
  serviceCode
  サービスコード
  addr
  読み込みを開始するブロックデータアドレス
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   85
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00086.jpg)
  successCallback
  読み込みが正常に行われた場合に呼び出されます。
  function(blockdata) {
  // Do something with the block data.
  }
  パラメータ
  　・blockdata: 読み込まれたブロックデータ
  errorCallback
  読み込みに失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  writeWithoutEncryption
  認証を必要としないサービスへブロックデータを書き込みます。
  Felica.writeWithEncryption(serviceCode, addr, buff, successCallback,
  errorCallback);
  serviceCode
  サービスコード
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   86
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00087.jpg)
  serviceCode
  サービスコード
  addr
  書き込みを開始するブロックデータアドレス
  buff
  書き込みデータ
  successCallback
  書き込みが正常に行われた場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  書き込みに失敗した発生した場合に呼び出されます。
  function() {
  // Do something in case of error.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   87
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00088.jpg)
  AppVersion
  メソッド：
  　・get
  get
  アプリバージョンを取得します。
  　
  AppVersion.get(callback);
  callback
  アプリバージョンが取得されます。
  function(version) {
  // Do something with App Version.
  }
  パラメータ
  　・version: アプリバージョン(String)
  　　　　　　iOSの場合：info.plistに設定したbundle version
  　　　　　　Androidの場合：AndroidManifest.xmlに設定したversionName
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   88
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00089.jpg)
  Contacts
  メソッド：
  　・create
  　・search
  create
  新しいコンタクトオブジェクトを作成します。
  var contact = Contacts.create();
  　
  search
  デバイス内の電話帳データベースから検索を行います
  Contacts.search(conditions, orderby, successCallback, errorCallback);
  conditions
  検索条件
  orderby
  取得順
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   89
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00090.jpg)
  successCallback
  連絡先情報が正常に取得された場合に呼び出されます。
  function(contacts) {
  // Do something.
  }
  errorCallback
  連絡先情報取得に失敗した発生した場合に呼び出されます。
  function(message) {
  // Show a helpful message.
  }
  パラメータ
  　・message: ネイティブ側からのエラーメッセージ
  サンプル
  var conditions = new Array();
  conditions.push(“displayName = 'Test'”);
  conditions.push(“addresses != ''”);
  Contacts.search(conditions, Contacts.SEARCH\_ORDER\_ASC,
  successCallback,
  errorCallback) ‏
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   90
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00091.jpg)
  Contact
  プロパティ：
  　・id: ID.
  　・displayName: 表示名
  　・name: 名前
  　・nickname: ニックネーム
  　・phoneNumbers: 電話番号（Array）
  　・emails: メールアドレス（Array）
  　・addresses: 住所（Array）
  　・ims: IMアドレス（Array）
  　・organizations: 組織（Array）
  　・birthday: 誕生日
  　・note: 備考
  　・photos: 連絡先写真（Array）
  　・categories: カテゴリ（Array）
  　・urls: ホームページ（Array）
  メソッド:
  　・remove 
  　・save
  remove
  連絡先情報を削除します。
  Contact.remove(successCallback, errorCallback);
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   91
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00092.jpg)
  successCallback
  連絡先情報が正常に削除された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  連絡先情報の削除に失敗した場合に呼び出されます。
  function(message) {
  // Show a helpful message.
  }
  パラメータ
  　・message: ネイティブ側からのエラーメッセージ
  save
  連絡先情報を保存します。
  Contact.save(successCallback, errorCallback);
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   92
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00093.jpg)
  successCallback
  連絡先情報保存が正常に削除された場合に呼び出されます。
  function() {
  // Do something.
  }
  errorCallback
  連絡先情報の保存に失敗した場合に呼び出されます。
  function(message) {
  // Show a helpful message.
  }
  パラメータ
  　・message: ネイティブ側からのエラーメッセージ
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   93
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00094.jpg)
  ContactPhoneNumber
  プロパティ：
  　・type: 電話番号タイプ
  　・number: 電話番号
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   94
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00095.jpg)
  ContactAddress
  プロパティ：
  　・type: アドレスタイプ
  　・formatted:表示用にフォーマットされたフルアドレス
  　・streetAddress:ストリートアドレス
  　・locality:市、もしくは町名
  　・region:地方名
  　・postalCode:郵便番号
  　・country:国
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   95
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00096.jpg)
  ContactEmailAddress
  プロパティ：
  　・type: メールアドレスタイプ
  　・address:メールアドレス
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   96
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00097.jpg)
  ContactIMAddress
  プロパティ：
  　・type: アドレスタイプ
  　・address:IMアドレス
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   97
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00098.jpg)
  ContactPhoto
  プロパティ：
  　・uri:フォト画像へのURI　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   98
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00099.jpg)
  ContactCategory
  プロパティ：
  　・category:カテゴリ名　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   99
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00100.jpg)
  ContactURL
  プロパティ：
  　・type:URLタイプ
  　・url:URL
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   100
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00101.jpg)
  ContactOrganization
  プロパティ：
  　・type:組織タイプ
  　・name:組織名
  　・department:部門
  　・title:タイトル
  　
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   101
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00102.jpg)
  PDFViewer
  メソッド：
  　・showWithFileObject
  　・showWithFilePath
  　・showWithURL
  　
  showWithFileObject
  ファイルオブジェクトを使用してPDFを表示します
  PDFViewer.showWithFileObject(file);
  file
  PDFファイルへのファイルオブジェクト
  showWithFilePath
  ファイルパスで指定されたPDFを表示します
  PDFViewer.showWithFilePath(filePath);
  filePath
  PDFファイルへのファイルパス
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   102
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00103.jpg)
  showWithURL
  URLで指定されたPDFを表示します
  PDFViewer.showWithURL(url);
  url
  PDFファイルへのURL
  ※PDFを表示する前に、該当ファイルをダウンロードします。
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   103
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00104.jpg)
  Ad
  メソッド：
  　・create
  　・show
  　・hide
  　
  create
  広告表示用ビューを作成し、広告をロードします。
  Ad.create(position, successCallback, errorCallback, [options]);
  position
  広告表示用ビューを表示する位置を指定します。指定値は以下の通り
  　AD.POSITION\_TOP : 0
  　AD.POSITION\_BOTTOM : 1
  successCallback
  広告表示用ビューが作成され、広告が正常に読み込まれた場合に呼ばれます
  function() {
  // Do something.
  }
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   104
  ------------------------------------------------------------------------

  ------------------------------------------------------------------------
  ![](bgimg/bg00105.jpg)
  errorCallback
  広告表示用ビューの作成に失敗した、もしくは広告の読み込みに失敗した場合に呼ばれます

  function() {
  // Do something in case of error.
  }
  options
  オプションを設定します
  {
  id : a03ce87ff88282d3a7799fe89de30b // 例 ) パブリッシャー id :
  a14e62fe30c05d7
  };
  オプション
  　・id:
  Admob用のパブリッシャーIDを指定します。指定する文字列はEncryptorオブジェクトを使用して暗号化された文字列を指定して

  ください。（※Androidのみ）
  Copyright(C)  ZYYX  Inc.  All  rights  reserved.   105
  ------------------------------------------------------------------------
