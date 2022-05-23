import 'package:flutter/material.dart';

import 'BannerPage.dart';

void main() {
  runApp(MyApp());

}

class MyApp extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async{
        Navigator.pop(context,true);
        return true;
      },
      child: MaterialApp(
          title: 'Happy BirthDay！',
          theme: ThemeData(
            primarySwatch: Colors.blue,
          ),
          home: BannerPage()
      ),
    );
  }


}

