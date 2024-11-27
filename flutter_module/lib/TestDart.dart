/*
*
 * Created on 2024/04/18.
 * @author carsonyang
*/

import 'dart:ui';

import 'package:flutter/services.dart';

class TestDart {
  //变量
  var i = 1;
  var s = "String";

  var d = 33.4;

  //object数组
  var array = ['11', 2, 3];

  //可空变量
  String? ss;

  late bool? b = true;

  //延迟初始化变量 lateinit
  late String lateString;

  //by lazy
  late String lazyString = "";

  //只能设置一次 val
  final finalString = "";

  //const 编译时常量
  static const constString = "";
  var bb = const [];

  //一些表达式

  //相当于apply?.{}
  var paint = Paint()
    ?..color = const Color(0x00000444)
    ..strokeCap = StrokeCap.round
    ..strokeWidth = 5.0;

  //==============================//

  //构造函数
  TestDart(this.i, this.ss);

  //次构造函数
  TestDart.dd(int ii) : this(ii, null);

  //==============================//
  //异步函数
  Future<int> testAsyncAndAwait() async {
    await Future.delayed(const Duration(seconds: 1));
    var result = await (2 + 23435 + 42334);
    const bar = 00;
    return result;
  }

/*===========================*/
  //函数
  int helloChild() {
    for (final ii in array) {
      s += ii.toString();
    }

    bool isVisible = b ?? false;

    var visiblity = isVisible ? "show" : "hide";

    //一些奇奇怪怪的语法
    var list = ["11", "22", "22", "33"];

    for (String l in list) {}

    //map代表对list每一个参数进行修改

    //foreach获取每个参数

    list.map((item) {
      return item.toUpperCase();
    }).forEach((item) {
      print('$item: ${item.length}');
    });
    switch (visiblity) {
      case "":
        return 0;
      case "1":
        return 1;
    }
    var ww = switch (visiblity) {
      "1" => "1", "2" => "2", _ => 21
    };

    return 2;
  }

  helloMethod({required int? i, bool? j, String s = ""}) async {
    helloMethod(i: 9);
    helloMethod(i: 1);
    testAsyncAndAwait().then((result) {
      //result是上一个运行的结果
    }).then((_) =>
    {
      //根据上一个完成才进行下一个
    });

    //等所有的任务完成才那个
    var s = await Future.wait([
      testAsyncAndAwait(),
      testAsyncAndAwait(),
      testAsyncAndAwait(),
    ]);
    //还有可以获取到三个任务分别得到的结果
  }

  helloMethod2(int? i, bool? j, String s, [double d = 0]) {
    //在非future函数里调future函数
    testAsyncAndAwait();
    //
    testAsyncAndAwait().then((value) => null);

    return helloMethod2(i = 1, j = false, "3");
  }
}

//单继承
class TestDartFather extends TestDart with OtherClass implements TestInterface {
  TestDartFather(super.i, super.ss);

  void hello3() {
    helloChild();
    helloMixin();
  }

  @override
  void helloInterface() {}
}

//Mixin组合
mixin OtherClass {
  int other = 1;

  void helloMixin() {}
}

//任意一个类都可以当interface
class TestInterface {
  void helloInterface() {
    print("object");
  }
}

class Main {
  var tr = TestDart(1, null);

  //次构造函数初始化
  var tt = TestDart.dd(2);
  static const platform = MethodChannel('唯一的通道名字');

  void ttt() {
    tr.helloChild();
    tt.helloChild();
  }

  Future<void> getBatteryLevel() async {
    String battery;
    try {
      final result = await platform.invokeMethod<int>('getBattery');
    } on PlatformException {}
  }
}
