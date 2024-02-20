package com.george.chapter06;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.george.chapter06.database.BookDatabase;

public class MainApplication extends Application {

    private String TAG = "GeorgeTag";

    private static MainApplication mApp;
    // 书籍数据库实例
    private BookDatabase bookDatabase;

    /**
     * 全局单例
     * @return
     */
    public static MainApplication getInstance() {
        return mApp;
    }

    /**
     * 在App启动时调用。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate......");
        mApp = this;

        // 构建书籍数据库实例
        bookDatabase = Room.databaseBuilder(mApp, BookDatabase.class, "book")
                .addMigrations() // 允许迁移数据库（发生数据库变更时，Room默认删除原数据库再创建新数据库。如此一来原来的记录会丢失，故而要改为迁移方式以便保存原有记录）
                .allowMainThreadQueries() // 允许在主线程中操作数据库（Room默认不能在主线程中操作数据库）
                .build();

    }

    /**
     * 获取书籍数据库实例
     * @return
     */
    public BookDatabase getBookDB() {
        return bookDatabase;
    }

    /**
     * 在App终止时调用（按字面意思）。
     * onTerminate方法就是个摆设，中看不中用。如果读者想在App退出前回收系统资源，
     * 就不能指望onTerminate方法的回调了。
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate......");
    }

    /**
     * 在配置改变时调用，例如从竖屏变为横屏
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged......");
    }
}
