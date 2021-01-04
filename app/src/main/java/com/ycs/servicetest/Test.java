package com.ycs.servicetest;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import retrofit2.Call;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;



public class Test extends AppCompatActivity {
//    String url="https://twitter.com/chenhui5201315/status/1299211914992472065";
//    String TAG="yyy";
//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            if(msg.what==1){
//                Toast.makeText(Test.this, "成功！", Toast.LENGTH_SHORT).show();
//                LoadingUtil.Loading_close();
//            }
//        }
//    };
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
//        setContentView(R.layout.test);
//        LoadingUtil.Loading_show(this);
//        TwitterConfig config = new TwitterConfig.Builder(this)
//                .twitterAuthConfig(new TwitterAuthConfig(Constant.TWITTER_KEY, Constant.TWITTER_SECRET))
//                .build();
//        Twitter.initialize(config);
//        Long id = getTweetId(url);
//        final String fname = String.valueOf(id);
//        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
//        StatusesService statusesService = twitterApiClient.getStatusesService();
//        Call<Tweet> tweetCall = statusesService.show(id, null, null, null);
//        tweetCall.enqueue(new Callback<Tweet>() {
//            @Override
//            public void success(Result<Tweet> result) {
//                //Check if media is present
//                if (result.data.extendedEntities == null && result.data.entities.media == null) {
//                    //alertNoMedia();
//                    Toast.makeText(Test.this, "没有文件", Toast.LENGTH_SHORT).show();
//                    LoadingUtil.Loading_close();
//                } else if (result.data.extendedEntities != null) {
//                    if (!(result.data.extendedEntities.media.get(0).type).equals("video") &&
//                            !(result.data.extendedEntities.media.get(0).type).equals("animated_gif")) {
//                        //alertNoVideo();
//                        Toast.makeText(Test.this, "没有视频", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "没有视频" );
//                        LoadingUtil.Loading_close();
//                    } else {
//                        String filename = fname;
//                        String url;
//
//                        //Set filename to gif or mp4
//                        if ((result.data.extendedEntities.media.get(0).type).equals("video") ||
//                                (result.data.extendedEntities.media.get(0).type).equals("animated_gif")) {
//                            filename = filename + ".mp4";
//                            Log.d("TAG", "filenme for video is " + filename);
//
//                        }
//
//                        int i = 0;
//                        url = result.data.extendedEntities.media.get(0).videoInfo.variants.get(i).url;
//
//                        Log.d("TAG", "url is " + url);
//
//                        while (!url.contains(".mp4")) {
//                            try {
//                                if (result.data.extendedEntities.media.get(0).videoInfo.variants.get(i) != null) {
//                                    url = result.data.extendedEntities.media.get(0).videoInfo.variants.get(i).url;
//                                    Log.d("TAG", "url2 is " + url);
//                                    i += 1;
//                                }
//                            } catch (IndexOutOfBoundsException e) {
//                                downloadVideo(url, filename);
//                            }
//                        }
//                        downloadVideo(url, filename);
//                    }
//                }
//
//            }
//
//
//            @Override
//            public void failure(TwitterException exception) {
//                Log.e(TAG,exception.getMessage());
//                Toast.makeText(Test.this, "Request failed, check your Internet connection", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    private void downloadVideo(String url, String filename) {
//
//        //Check if External Storage permission js allowed
//       WebUtil.download(handler,url,
//               Environment.getExternalStorageDirectory() +"/.savedPic/"+filename,
//               WebUtil.genearteFileName(),Test.this);
//    }
//    private Long getTweetId(String s) {
//        Log.d("TAG", "link is :" + s);
//
//        try {
//            String[] split = s.split("\\/");
//            String id = split[5].split("\\?")[0];
//            return Long.parseLong(id);
//        } catch (Exception e) {
//            Log.d("TAG", "getTweetId: " + e.getLocalizedMessage());
//            return null;
//        }
//    }
}
