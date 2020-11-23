package com.ycs.servicetest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.downloader.Constants;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Patterns.DOMAIN_NAME;
import static android.util.Patterns.GOOD_IRI_CHAR;

public class WebUtil {
    public static int index=0;
    public static int downloadId;
    public static Boolean isWeb(final String s) {

        Pattern pattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+ ([A-Za-z0-9-~\\/])+$");


        return pattern.matcher(s).matches();

    }
    public static boolean isHttpUrl(String urls) {
        boolean isurl = false;
     //   String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式
        String regex="((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
        + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
        + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
        + "(?:" + DOMAIN_NAME + ")"
        + "(?:\\:\\d{1,5})?)" // plus option port number
        + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params
        + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
        + "(?:\\b|$)";
        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());
        isurl = mat.matches();//判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public synchronized static void download(final String url, final String path, final String filename, final Context context){
        int downloadId = PRDownloader.download(url,path,filename)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        Toast.makeText(context, filename+"开始下载", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, filename+"下载成功", Toast.LENGTH_SHORT).show();

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
        downloadId = PRDownloader.download(url,path,filename)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        Toast.makeText(context, filename+"开始下载", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, filename+"下载成功", Toast.LENGTH_SHORT).show();
                        Log.e("yyy",filename+"下载成功");
                        Uri uri = null;
                        File f=new File(new File(path),filename);
                        if(Build.VERSION.SDK_INT>= 24){
                            uri = FileProvider.getUriForFile(context, "com.ycs.codecreate.provider", f);
                        }else{
                            uri=Uri.fromFile(f);
                        }
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri));
                        MainService.update(filename+"下载完成");
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(context, "下载失败！", Toast.LENGTH_SHORT).show();
                        MainService.update(filename+"下载失败，请重试");
//                        download(handler,url, path,filename.substring(0,filename.length()-4)+"重试"+".mp4",context);
//                        Toast.makeText(context, filename+"重试", Toast.LENGTH_SHORT).show();
//                        Log.e("yyy",filename+"重试");

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
        return time+rand+".mp4";
    }
    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap;
    }
}
