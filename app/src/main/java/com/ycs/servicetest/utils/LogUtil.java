package com.ycs.servicetest.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LogUtil {
    public static void writeLog() {
        new Thread(() -> {
            boolean captureLogThreadOpen = true;
            FileOutputStream fos;
            int logCount = 0;
            String[] running = new String[]{"logcat", "-s", "adb logcat *: F"};
            /*核心代码*/
            while (captureLogThreadOpen) {
                try {
                    /*命令的准备*/
                    ArrayList<String> getLog = new ArrayList<>();
                    getLog.add("logcat");
//                         getLog.add("-d");
//                         getLog.add("-v");
//                         getLog.add("time");
//                      getLog.add("*:D");

                    ArrayList<String> clearLog = new ArrayList<>();
                    clearLog.add("logcat");
                    clearLog.add("-c");

                    Process process = Runtime.getRuntime().exec(getLog.toArray(new String[0]));//抓取当前的缓存日志


                    BufferedReader buffRead = new BufferedReader(new InputStreamReader(process.getInputStream()));//获取输入流
                    Runtime.getRuntime().exec(clearLog.toArray(new String[0]));//清除是为了下次抓取不会从头抓取
                    String str;
                    File logFile = new File(Environment.getExternalStorageDirectory() + "/.savedPic/log.txt");//打开文件
                    fos = new FileOutputStream(logFile, true);//true表示在写的时候在文件末尾追加
                    String newline = System.getProperty("line.separator");//换行的字符串
                    //Date date = new Date(System.currentTimeMillis());
                    //String time = format.format(date);

                    //Log.i(TAG, "thread");
                    while ((str = buffRead.readLine()) != null) {//循环读取每一行
                        if (!(str.contains("yyy"))) {
                            continue;
                        }
                        //Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
                        //Log.i(TAG, str);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-", Locale.CHINA);
                        Date date = new Date(System.currentTimeMillis());
                        String time = format.format(date);
                        fos.write((time + str).getBytes());//加上年
                        fos.write(newline != null ? newline.getBytes() : new byte[0]);//换行
                        logCount++;
                        if (logCount > 100) {//大于10000行就退出
                            captureLogThreadOpen = false;
                            fos.close();
                            break;
                        }
                    }
                    fos.close();
                    Runtime.getRuntime().exec(clearLog.toArray(new String[0]));
                } catch (Exception ignored) {

                }

            }
        }).start();

    }


}
