package com.ycs.servicetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//
//import com.geccocrawler.gecco.annotation.Gecco;
//import com.geccocrawler.gecco.annotation.RequestParameter;
//import com.geccocrawler.gecco.annotation.Text;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebActivity extends AppCompatActivity {
//    private WebView webView;
//    private TextView tv;
//    private TextView tvWeb;
    private ItemAdapter adapter;
    private String url;
    private CustomLinearLayoutManager layoutManager ;
    private RecyclerView recyclerView;
    private ArrayList<Items> itemsList=new ArrayList<>();
    private ArrayList<String> srcList=new ArrayList<>();

    static final String TAG="yangchaosheng";
    @SuppressLint("HandlerLeak")
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                String text=msg.obj.toString();
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            try {
//                                    Connection con = Jsoup.connect("https://www.amazon.cn/s/ref=sr_pg_1?rh=n%3A1852543071&sort=popularity-rank&ie=UTF8&qid=1511322011");
//                                    // UserAgent是发送给服务器的当前浏览器的信息。
//                                    // 这是我的电脑的chrome的userAgent。
//                                    con.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
                                   // 爬亚马逊
                                    Document document= Jsoup.connect("https://www.amazon.cn/b?node=1852543071").get();
                                    Elements elements = document.getElementsByClass("a-carousel-card");

                                    Log.e("yyy",elements.size()+"");
                                    for (Element li : elements) {
                                        Element content =li.select("img").first();
                                        String bookTitle = content.attr("alt");
                                        String bookImgUrl=content.attr("src");
                                        Items i=new Items();
                                        i.setText(bookTitle);
                                        srcList.add(bookImgUrl);
                                        i.setSrc(getBitmap(bookImgUrl));
                                        itemsList.add(i);
                                        Log.e("yyy", bookTitle);
                                    }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int postion) {
                                                AlertDialog dialog=new AlertDialog.Builder(WebActivity.this).create();
                                                View v=LayoutInflater.from(WebActivity.this).inflate(R.layout.big_bitmap,null);
                                                ImageView i=v.findViewById(R.id.src);
                                                i.setImageBitmap(itemsList.get(postion).getSrc());
//                                                i.setImageBitmap(getBitmap(srcList.get(postion)));
                                                dialog.setView(v);
                                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                dialog.show();
                                            }
                                        });
                                        adapter.setOnItemLongClickListener(new ItemAdapter.OnItemLongClickListener() {
                                            @Override
                                            public void onItemLongClick(View view, final int postion) {
                                                new IosAlertDialog(WebActivity.this).builder()
                                                        .setPositiveButton("下载", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                //downloadPicture(srcList.get(postion));
                                                                WebUtil.download(srcList.get(postion),
                                                                        Environment.getExternalStorageDirectory() +"/savedPic/",
                                                                        WebUtil.genearteFileName(),WebActivity.this);

                                                            }
                                                        })
                                                        .setMsg("确认下载？")
                                                        .setTitle("提示")
                                                        .show();
                                            }
                                        });
                                        adapter.update(itemsList);
                                        LoadingUtil.Loading_close();
                                    }
                                });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }
                    }).start();



            }else if(msg.what==2){
                LoadingUtil.Loading_close();
                Items i=new Items();
                i.setText("网络未打开");
                itemsList.add(i);
                adapter.update(itemsList);
                Toast.makeText(WebActivity.this, "网络未打开", Toast.LENGTH_SHORT).show();
            }else if(msg.what==3){
                LoadingUtil.Loading_close();
                Items i=new Items();
                i.setText("连接失败\n错误原因为："+msg.obj.toString());
                itemsList.add(i);
                adapter.update(itemsList);
                Toast.makeText(WebActivity.this, "连接失败\n错误原因为：", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().hide();
        recyclerView=findViewById(R.id.recyclerview);
        layoutManager = new CustomLinearLayoutManager(this);
        layoutManager.setScrollEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new ItemAdapter(itemsList);
       // Log.e("yyy", ""+itemsList.size() );
        recyclerView.setAdapter(adapter);
        LoadingUtil.Loading_show(this);
//        webView=findViewById(R.id.wv);
        url=getIntent().getStringExtra("url");
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl(url);
//        tv=findViewById(R.id.tv);
//        tvWeb=findViewById(R.id.tv_web);
        if(isNetworkConnected(this)){
            try {
                goWeb(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            mhandler.sendEmptyMessage(2);
        }


    }
    private void goWeb(final String s) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                //构建请求报文
                String str=s;
                if(!s.contains("http")){
                    str="https://"+s;
                }
                Log.e(TAG, str);
                Request request = new Request.Builder()
                        .url(str)//配置请求百度首页地址
                        .build();
                //Response response = null;
//                try {
//                    response = okHttpClient.newCall(request).execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Message m=new Message();
                        m.what=3;
                        m.obj=e.getMessage();
                        mhandler.sendMessage(m);
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
//                        Headers headers = response.headers();
//                        for (int i = 0; i < headers.size(); i++) {
//                            //分别获取请求头的name,value值
//                            String headName = headers.name(i);
//                            String headValue = headers.value(i);
//                            Log.e("yangchaosheng", headName );
//                            Log.e("yangchaosheng", headValue );
//                        }
                        Message msg=new Message();
                        msg.what=1;
                        msg.obj=response.body().string();
                        mhandler.sendMessage(msg);
                    }
                });




            }
        }).start();
        //构建okHttp客户端

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
    public Bitmap getBitmap(String path) throws IOException {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public void downloadPicture(final String uurl){

        new Thread(new Runnable() {
            @Override
            public void run() {

                URL url = null;
                HttpURLConnection con = null;
                try {

                    url = new URL(uurl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setReadTimeout(5000);
                    con.setDoInput(true);
                    double length=con.getContentLength();
                    String filename = Environment.getExternalStorageDirectory() +"/savedPic/";
                    File file = new File(filename);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    File f = new File(file,String.valueOf(System.currentTimeMillis()+".jpg"));
                    final String fname=f.getName();
                    Uri uri = null;
                    if(Build.VERSION.SDK_INT>= 24){
                        uri = FileProvider.getUriForFile(WebActivity.this, "com.ycs.servicetest.provider", f);
                    }else{
                        uri= Uri.fromFile(f);
                    }
                    OutputStream fos= (OutputStream) getContentResolver().openOutputStream(uri);
//                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream in = con.getInputStream();
                    byte ch[] = new byte[2 * 1024];
                    int len=0;
                    long haswrite=0L;
                    int percent=0;
                    if (fos!= null){
                        while ((len = in.read(ch)) != -1){
                            fos.write(ch,0,len);
                            haswrite += len;
                            //BidDecimal精确数
                            double d = (new BigDecimal(haswrite / length)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                            percent = (int) (d * 100);
                            Log.e("yyy", "进度"+percent);
                            MainService.updateProgress(WebActivity.this,percent);

                        }
                        if(percent==100){
                            Looper.prepare();
                            mhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WebActivity.this, fname+"下载成功", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        in.close();
                        fos.close();

                    }else{
                        Log.e("yyy", "fou为空" );
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("yyy","失败"+e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("yyy","失败"+e.getMessage());
                }
            }
        }).start();
    }

}