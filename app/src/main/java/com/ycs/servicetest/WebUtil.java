package com.ycs.servicetest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Environment;
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
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;

import static android.util.Patterns.DOMAIN_NAME;
import static android.util.Patterns.GOOD_IRI_CHAR;
import static com.ycs.servicetest.MainActivity.TAG;

public class WebUtil {
    public static int index=0;
    public static int downloadId;
    public static final int STOP_SERVICE=1;
    public static boolean isAnalyse=false;
    public static ArrayList<String> downLoadList=new ArrayList<>();
    public static Boolean isDownloading=false;
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
//    public synchronized static void download(final String url, final String path, final String filename, final Context context){
//        int downloadId = PRDownloader.download(url,path,filename)
//                .build()
//                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
//                    @Override
//                    public void onStartOrResume() {
//                        Toast.makeText(context, filename+"开始下载", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setOnPauseListener(new OnPauseListener() {
//                    @Override
//                    public void onPause() {
//
//                    }
//                })
//
//                .setOnProgressListener(new OnProgressListener() {
//                    @Override
//                    public void onProgress(Progress progress) {
//                        double d=(new BigDecimal(progress.currentBytes / (double)progress.totalBytes)
//                                .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
//                        Log.e("yyuu",filename+":"+(int)(d*100));
//                        MainService.updateProgress(context,(int)(d*100));
//                    }
//                })
//                .start(new OnDownloadListener() {
//                    @Override
//                    public void onDownloadComplete() {
//                        Toast.makeText(context, filename+"下载成功", Toast.LENGTH_SHORT).show();
//
//                        Log.e("yyy",filename+"下载成功");
//                    }
//
//                    @Override
//                    public void onError(Error error) {
//                        download(url, path,filename.substring(0,filename.length()-4)+"重试"+".jpg",context);
//                        Toast.makeText(context, filename+"重试", Toast.LENGTH_SHORT).show();
//                        Log.e("yyy",filename+"重试");
//                    }
//                });
//    }
    public static void init(Context context){
        TwitterConfig config = new TwitterConfig.Builder(context)
                .twitterAuthConfig(new TwitterAuthConfig(Constant.TWITTER_KEY, Constant.TWITTER_SECRET))
                .build();
        Twitter.initialize(config);
    }
    public static void predownload(String url,Context context, Handler handler){
        Long id = getTweetId(url);
        //final String fname = String.valueOf(id);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> tweetCall = statusesService.show(id, null, null, null);
        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                WebUtil.isAnalyse=false;
                if (result.data.extendedEntities == null && result.data.entities.media == null) {
                    MainService.updateNotification(context,"链接中未找到文件，下载失败");
                    Toast.makeText(context, "链接未找到文件", Toast.LENGTH_SHORT).show();
                } else if (result.data.extendedEntities != null) {
                    Log.e(TAG, result.data.extendedEntities.media.get(0).type);

                    String text=result.data.text;
                    if(text.contains("http")){
                        text=text.substring(0,text.indexOf("http"));
                    }
                    if (!(result.data.extendedEntities.media.get(0).type).equals("video") &&
                            !(result.data.extendedEntities.media.get(0).type).equals("animated_gif")) {
                        MainService.updateNotification(context,"链接中未找到视频，下载失败");
                        Toast.makeText(context, "链接未找到视频", Toast.LENGTH_SHORT).show();
                    } else {
                        //String filename = fname;
                        String url;
//                        if ((result.data.extendedEntities.media.get(0).type).equals("video") ||
//                                (result.data.extendedEntities.media.get(0).type).equals("animated_gif")) {
//                            filename = filename + ".mp4";
//                        }
                        int i = 0;
                        url = result.data.extendedEntities.media.get(0).videoInfo.variants.get(i).url;
                        Log.e(TAG,"收到链接啦"+url);
                        while (!url.contains(".mp4")) {
                            try {
                                if (result.data.extendedEntities.media.get(0).videoInfo.variants.get(i) != null) {
                                    url = result.data.extendedEntities.media.get(0).videoInfo.variants.get(i).url;
                                    i += 1;
                                }
                            } catch (IndexOutOfBoundsException e) {
                                downloadVideo(url,context,handler,text);
                            }
                        }
                        downloadVideo(url,context,handler,text);
                    }
                }

            }
            @Override
            public void failure(TwitterException exception) {
                isAnalyse=false;
                MainService.updateNotification(context,"网络连接失败");
                Log.e(TAG, "失败惹 "+exception.getMessage() );
                if(exception.getMessage().contains("404")){
                    Toast.makeText(context, "链接失效", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "连接失败，联网了吗？开VPN了吗？", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private static void downloadVideo(String url, Context context, Handler handler,String text) {
        if(!isDownloading){
            String filename=WebUtil.genearteFileName();
            SharedPreferences sp=context.getSharedPreferences("text", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();
            e.putString(filename,text);
            e.commit();
            download(handler,url,
                    Environment.getExternalStorageDirectory() +"/.savedPic/",
                    filename,context);
        }else{
            downLoadList.add(url);
        }

    }
    private static Long getTweetId(String s) {
        try {
            String[] split = s.split("\\/");
            String id = split[5].split("\\?")[0];
            return Long.parseLong(id);
        } catch (Exception e) {
            Log.d("TAG", "getTweetId: " + e.getLocalizedMessage());
            return null;
        }
    }
    public synchronized static void download(final Handler handler, final String url, final String path, final String filename, final Context context){
        isDownloading=true;
        downloadId = PRDownloader.download(url,path,filename)
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
                    private boolean is=true;
                    @Override
                    public void onProgress(Progress progress) {
                        if(is){
                            is=false;
                            double d=(new BigDecimal(progress.totalBytes / (1024*1024.0))
                                    .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                            String len=d+"MB";
                            Toast.makeText(context, filename+"开始下载，共"+len, Toast.LENGTH_SHORT).show();
                        }
                        double d=(new BigDecimal(progress.currentBytes / (double)progress.totalBytes)
                                .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                        double sum=(new BigDecimal(progress.totalBytes / (1024*1024.0))
                                .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                        double currentBytes=(new BigDecimal(progress.currentBytes / (1024*1024.0))
                                .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                        String progressStr=currentBytes+"MB/"+sum+"MB";
                        MainService.updateProgress(context,(int)(d*100),progressStr);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        isDownloading=false;
                        Toast.makeText(context, filename+"下载成功", Toast.LENGTH_SHORT).show();
                        Log.e("yyy",filename+"下载成功");
                        Uri uri = null;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainService.update(filename+"下载完成");
                            }
                        },1000);

                        File f=new File(new File(path),filename);
                        if(Build.VERSION.SDK_INT>= 24){
                            uri = FileProvider.getUriForFile(context, "com.ycs.codecreate.provider", f);
                        }else{
                            uri=Uri.fromFile(f);
                        }
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri));

                        if(downLoadList.contains(url)){
                            downLoadList.remove(url);
                        }
                        if(downLoadList.size()==0){
                            handler.sendEmptyMessage(STOP_SERVICE);
                        }else{
                            download(handler,downLoadList.get(0),
                                    Environment.getExternalStorageDirectory() +"/.savedPic/", genearteFileName(),context);
                        }

                    }

                    @Override
                    public void onError(Error error) {
                        isDownloading=false;
                        Toast.makeText(context, "下载失败！"+error.toString(), Toast.LENGTH_SHORT).show();
                        MainService.update(filename+"下载失败，请重试");
                        if(downLoadList.contains(url)){
                            downLoadList.remove(url);
                        }
                        if(downLoadList.size()==0){
                            handler.sendEmptyMessage(STOP_SERVICE);
                        }else{
                            download(handler,downLoadList.get(0),
                                    Environment.getExternalStorageDirectory() +"/.savedPic/", genearteFileName(),context);
                        }

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
