import 'dart:developer';
import 'dart:io';
import 'dart:math';

import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_interactional_widget/flutter_interactional_widget.dart';


class BannerPage extends StatefulWidget {
  @override
  _BannerPageState createState() => _BannerPageState();

}

class _BannerPageState extends State<BannerPage> with TickerProviderStateMixin{
  double height=200;
  bool isPlay=true;
  Animation<double> animation;
  AnimationController controller;

  String src;
  AudioPlayer audioPlayer=new AudioPlayer() ;
  AudioCache player;

  initState() {
    super.initState();
    player =new AudioCache(fixedPlayer: audioPlayer);
    src="play";
    controller = AnimationController(duration: const Duration(milliseconds: 4000), vsync: this);
    animation = Tween(begin: 0.0, end: 1.0).animate(controller);
    controller.repeat();
    play();
  }
  @override
  void dispose() {
    player.clearAll();
    audioPlayer.stop();
    audioPlayer.dispose();
    controller.dispose();
    super.dispose();
    print("dispose");
  }


  void play(){
    player.loop('happy.mp3');
  }
  void pause(){
    audioPlayer.pause();
    //audioPlayer.pause();
  }
  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async{
        // player.clearAll();
        // audioPlayer.stop();
        // audioPlayer.dispose();
        // controller.dispose();
        // print("dispose");
        dispose();
        return true;
      },
      child: Scaffold(
        body: Stack(
          children: [
            banner(),
            Positioned(
                top: 40,
                left: 20,
                child: GestureDetector(
                  onTap: () {
                    if(isPlay){
                      controller.repeat();
                      play();
                      setState(() {
                        isPlay=false;
                        src="play";
                      });
                    }else{
                      controller.stop();
                      controller.reset();
                      pause();
                      setState(() {
                        isPlay=true;
                        src="pause";

                      });
                    }
                   // Fluttertoast.showToast(msg:"111");
                  },
                  child: RotationTransition(
                      turns: animation,
                      child: Image.asset(
                        "assets/$src.png",
                        width:  60,
                        height: 60,),
                ))
            )
          ],
        ),
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