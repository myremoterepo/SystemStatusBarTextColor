package com.funalways.systemstatusbartextcolor;

/**
 * 昨夜雨疏风骤，浓睡不消残酒，试问卷帘人，却道海棠依旧。知否，知否，应是绿肥红瘦
 * */
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int defaultTextColor = Color.parseColor("#acacac");
    private int mStatusTitleColor = defaultTextColor;
    private int mStatusContentColor = defaultTextColor;

    private NotificationManager mNotMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotMan = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        findViewById(R.id.main_notify_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetColor();
                notifyProgress(342);
            }
        });

        findViewById(R.id.main_get_color_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initStatusBarTextColor();
                notifyProgress(343);
            }
        });
    }

    private void notifyProgress(int id) {
        mNotMan.cancel(id);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.not_progress_layout);
        remoteViews.setTextViewText(R.id.not_title, getResources().getString(R.string.not_progress_title) + id + ":SystemStatusBarTextColor");
        remoteViews.setTextViewText(R.id.not_content, getResources().getString(R.string.not_progress_content));
        remoteViews.setTextColor(R.id.not_title, mStatusTitleColor);
        remoteViews.setTextColor(R.id.not_content, mStatusContentColor);
        mBuilder.setSmallIcon(R.drawable.ic_assignment_returned_white_48dp).setContent(remoteViews);
        mNotMan.notify(id, mBuilder.build());
    }


    /**
     * 最大字体的颜色值是标题
     * 最小字体的颜色值是内容
     * */
    public void initStatusBarTextColor(){
        try {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            Notification notification = mBuilder.build();
            int layoutId = notification.contentView.getLayoutId();
            ViewGroup notRoot = (ViewGroup) LayoutInflater.from(this).inflate(layoutId, null);
            if (notRoot == null) {
                mStatusTitleColor = defaultTextColor;
                mStatusContentColor = defaultTextColor;
                return;
            }
            TextView textView = (TextView) notRoot.findViewById(android.R.id.title);
            List<TextView> textViews = getTextViews(notRoot);
            //内容的颜色
            if (textViews.size() > 0) {
                int smallIndex = getSmallestTextIndex(textViews);
                mStatusContentColor = textViews.get(smallIndex).getCurrentTextColor();
            } else {
                mStatusContentColor = defaultTextColor;
            }
            //标题的颜色
            if (null == textView) {
                if (textViews.size() > 0) {
                    int largeIndex = getLargestTextIndex(textViews);
                    mStatusTitleColor = textViews.get(largeIndex).getCurrentTextColor();
                } else {
                    mStatusTitleColor = defaultTextColor;
                }
            } else {
                mStatusTitleColor = textView.getCurrentTextColor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resetColor();//出错情况使用默认值
        }

    }

    /**
     * 获取系统状态栏所有的TextView
     * */
    private List<TextView> getTextViews(View view){
        final List<TextView> textViews = new ArrayList<TextView>();
        iteratorView(view, new Filter() {
            @Override
            public void filter(View view) {
                if (view instanceof TextView){
                    textViews.add((TextView) view);
                }
            }
        });
        return textViews;
    }

    private void iteratorView(View notRoot, Filter filter) {
        if (notRoot == null || filter == null){
            return;
        }
        filter.filter(notRoot);
        if (notRoot instanceof ViewGroup){
            ViewGroup container = (ViewGroup)notRoot;
            for (int i = 0; i < container.getChildCount(); i++){
                View child = container.getChildAt(i);
                iteratorView(child, filter);
            }
        }
    }

    /**
     * 获取最大字体
     * */
    private int getLargestTextIndex(List<TextView> textViews) {
        float minTextSize = Integer.MIN_VALUE;
        int largeIndex = 0;
        for(int i = 0; i < textViews.size(); i ++){
            float currentSize = textViews.get(i).getTextSize();
            if (currentSize > minTextSize){
                minTextSize = currentSize;
                largeIndex = i;
            }
        }
        return largeIndex;
    }

    /**
     * 获取最小字体
     * */
    private int getSmallestTextIndex(List<TextView> textViews){
        float maxTextSize = Integer.MAX_VALUE;
        int smallIndex = 0;
        for(int i = 0; i < textViews.size(); i ++){
            float currentSize = textViews.get(i).getTextSize();
            if (currentSize < maxTextSize){
                maxTextSize = currentSize;
                smallIndex = i;
            }
        }
        return smallIndex;
    }

    /**
     * 使用默认颜色
     * */
    private void resetColor(){
        mStatusTitleColor = defaultTextColor;
        mStatusContentColor = defaultTextColor;
    }

    private interface Filter{
        void filter(View view);
    }
}
