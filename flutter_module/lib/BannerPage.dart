import 'package:flutter/material.dart';
import 'package:flutter_interactional_widget/flutter_interactional_widget.dart';

class BannerPage extends StatefulWidget {
  @override
  _BannerPageState createState() => _BannerPageState();
}

class _BannerPageState extends State<BannerPage> {
  double height=200;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          banner()
        ],
      ),
    );
  }
  Widget banner(){
    return InteractionalWidget(
      width: MediaQuery.of(context).size.width,//屏幕长度
      maxAngleX: 40,
      maxAngleY: 60,
      height:MediaQuery.of(context).size.height,
      middleScale: 1,
      foregroundScale: 1.1,
      backgroundScale: 1.2,
      backgroundWidget: backgroundWiget(),
      foregroundWidget: foregroundWiget(),
      middleWidget: middleWiget(),
    );
  }

  Widget backgroundWiget() {
    return Container(
       child: getImage('background.png'),
    );
  }
  Widget foregroundWiget() {
    return Container(
       child: getImage('foreground.png'),

    );
  }
  Widget middleWiget() {
    return Container(
      child: getImage('middle.png'),
    );
  }
  Image getImage(String s) {
    return Image.asset(
      "assets/$s",
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
      fit: BoxFit.fill,
      scale: 3.0,
    );
  }

}