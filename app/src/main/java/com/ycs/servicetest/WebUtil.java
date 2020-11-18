package com.ycs.servicetest;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtil {
    public static int index=0;
    public static Boolean isWeb(final String s) {

        Pattern pattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+ ([A-Za-z0-9-~\\/])+$");


        return pattern.matcher(s).matches();

    }
    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.)?)\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }
    public synchronized static void download(final String url, final String path, final String filename, final Context context){
        int downloadId = PRDownloader.download(url,path,filename)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        double d=(new BigDecimal(progress.currentBytes / (double)progress.totalBytes)
                                .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                        Log.e("yyuu",filename+":"+(int)(d*100));
                        MainService.updateProgress(context,(int)(d*100));
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        //Toast.makeText(context, filename+"下载成功", Toast.LENGTH_SHORT).show();
                        Log.e("yyy",filename+"下载成功");
                    }

                    @Override
                    public void onError(Error error) {
                        download(url, path,filename.substring(0,filename.length()-4)+"重试"+".jpg",context);
                        Toast.makeText(context, filename+"重试", Toast.LENGTH_SHORT).show();
                        Log.e("yyy",filename+"重试");
                    }
                });
    }

    public synchronized static void download(final Handler handler, final String url, final String path, final String filename, final Context context){
        int downloadId = PRDownloader.download(url,path,filename)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        double d=(new BigDecimal(progress.currentBytes / (double)progress.totalBytes)
                                .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                        Log.e("yyuu",filename+":"+(int)(d*100));
                        MainService.updateProgress(context,(int)(d*100));
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        //Toast.makeText(context, filename+"下载成功", Toast.LENGTH_SHORT).show();
                        Log.e("yyy",filename+"下载成功");
                        MainService.updateProgress(context,100);
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onError(Error error) {
                        download(url, path,filename.substring(0,filename.length()-4)+"重试"+".jpg",context);
                        Toast.makeText(context, filename+"重试", Toast.LENGTH_SHORT).show();
                        Log.e("yyy",filename+"重试");
                        handler.sendEmptyMessage(1);
                    }
                });
    }

    public static String genearteFileName(){
        Timestamp t=new Timestamp(new Date().getTime());
        String time=t.toString();
        time=time.replace(".","");
        time=time.replace(" ","");
        time=time.replace("-","");
        time=time.replace(":","");
        char [] arr = {'a','b','c','d','e'};
        char rand = arr[index%5];
        index++;
        return time+rand+".jpg";
    }
}
