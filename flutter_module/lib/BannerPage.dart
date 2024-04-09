import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/material.dart';
import 'package:flutter_interactional_widget/flutter_interactional_widget.dart';

class BannerPage extends StatefulWidget {
  const BannerPage({super.key});

  @override
  _BannerPageState createState() => _BannerPageState();
}

class _BannerPageState extends State<BannerPage> with TickerProviderStateMixin {
  double height = 200;
  bool isPlay = true;
  late Animation<double> animation;
  late AnimationController controller;

  late String src;
  final audioPlayer = AudioPlayer();
  //late AudioCache audioCache;

  @override
  initState() {
    super.initState();
    src = "play";
    //audioCache = AudioCache(fixedPlayer: audioPlayer);
    controller = AnimationController(
        duration: const Duration(milliseconds: 4000), vsync: this);
    animation = Tween(begin: 0.0, end: 1.0).animate(controller);
    controller.repeat();
    // audioPlayer.setReleaseMode(ReleaseMode.release);
    // audioPlayer.play(AssetSource('happy.mp3'));
    // WidgetsBinding.instance.addPostFrameCallback((timeStamp) async{
    //   // await audioPlayer.setSource(AssetSource('happy.mp3'));
    //   // await audioPlayer.resume();
    //   await
    // });
    audioPlayer.play(AssetSource('assets/happy.mp3'));
  }

  @override
  void dispose() {
   // audioCache.clearAll();
    audioPlayer.dispose();
    controller.dispose();
    super.dispose();
  }

  void play(){

    audioPlayer.resume();
    //audioCache.loop('happy.mp3');
  }

  void pause() {
    audioPlayer.pause();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async {
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
                      if (isPlay) {
                        controller.repeat();
                        play();
                        setState(() {
                          isPlay = false;
                          src = "play";
                        });
                      } else {
                        controller.stop();
                        controller.reset();
                        pause();
                        setState(() {
                          isPlay = true;
                          src = "pause";
                        });
                      }
                    },
                    child: RotationTransition(
                      turns: animation,
                      child: Image.asset(
                        "assets/$src.png",
                        width: 60,
                        height: 60,
                      ),
                    )))
          ],
        ),
      ),
    );
  }

  Widget banner() {
    return InteractionalWidget(
      width: MediaQuery.of(context).size.width,
      //屏幕长度
      maxAngleX: 40,
      maxAngleY: 60,
      height: MediaQuery.of(context).size.height,
      middleScale: 1,
      foregroundScale: 1.1,
      backgroundScale: 1.2,
      backgroundWidget: backgroundWidget(),
      foregroundWidget: foregroundWidget(),
      middleWidget: middleWidget(),
    );
  }

  Widget backgroundWidget() {
    return Container(
      child: getImage('background.png'),
    );
  }

  Widget foregroundWidget() {
    return Container(
      child: getImage('foreground.png'),
    );
  }

  Widget middleWidget() {
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
