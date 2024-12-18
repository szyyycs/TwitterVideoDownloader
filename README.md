# Android推特视频下载器  Android TwitterVideoDownloader

## 基本功能 Basic Function

- 解析推文链接，生成下载链接，自动下载推文包含的视频
- Parse tweet links, generate download links, and automatically download videos contained in tweets
  links
- 安卓9.0下支持自动获取剪切板内容，自动进行解析后台下载。安卓10及以上版本需要复制链接至悬浮窗/通知栏中解析下载
- Android 9.0 supports automatic acquisition of clipboard content and automatic analysis and
  background download. Android 10 and above need to copy the link to the floating
  window/notification bar for analysis and download
- 支持存储视频与链接推文文案（默认路径：根目录/.savedPic，支持修改下载目录）
- Support storage of videos (default path: root directory/.savedPic, supports modification of
  download directory)
- 支持列表流\瀑布流\tiktok流查看下载的视频
- Support multiple ways to view downloaded videos
- 生日彩蛋（flutter实现，支持自由设置生效时间）
- Birthday Surprise Dialog (implemented by flutter, supports free setting of effective time)

> 提示：目前通过Twitter官方获取视频地址的API已失效，现在本项目获取视频地址的方式是仿请求使用某下载网站的接口。目前（2024/04/16）该下载方式有效。

## 构建提示 Build Tips

- 项目集成了flutter模块，没有flutter环境可以把flutter_module的引用注释掉，不影响主功能使用
- 想要使用flutter模块，需要到flutter_module模块里执行 > flutter build apk 指令构建flutter产物再编译主项目
- 只是单纯想体验功能可以在项目apk目录里直接用安装包安装。有帮助就给个star吧！

- The project integrates the flutter module. If there is no flutter environment, you can comment out
  the reference to flutter_module without affecting the use of the downloaded and viewing downloaded
  video.
- If you want to use the flutter module, you need to execute the > flutter build apk command in the
  flutter_module module to build the flutter product and then compile the main project.
- If you just want to experience the function, you can install it directly in the project apk
  directory using the installation package. Give a star if it helps!

## 技术栈关键词

- GSYVideoPlayer
- Bugly/应用升级
- Kotlin
- Flutter
- MVVM
- Tiktok模式
- MMKV
- Glide
- Bmob
- 推特SDK
- 裸眼3D悬浮
- 瀑布流
- 自定义通知栏进度条
- 悬浮窗
- Jetpack Compose
- 指纹识别
- ...

> 提示：个人学习项目，想到啥写啥，想到啥加啥，有需要的功能欢迎提issue！


#twitterdownload #twittervideodownload #X #XvideoDownload #twitter
