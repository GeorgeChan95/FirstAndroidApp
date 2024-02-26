package com.george.shopping;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.george.shopping.database.ShoppingDBHelper;
import com.george.shopping.entity.GoodsInfo;
import com.george.shopping.utils.FileUtil;
import com.george.shopping.utils.PermissionUtil;
import com.george.shopping.utils.SharedPreferenceUtil;

import java.io.File;
import java.util.ArrayList;

public class ShoppingApplication extends Application {
    private static final String TAG = "GeorgeTag";


    // 声明类的全局静态对象实现单例模式，通过此对象获取该类的其它全局变量
    private static ShoppingApplication shoppingApp;
    // 购物车中的商品总数量
    public int goodsCount;

    public String[] PERMISSIONS_FILE = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    public final int REQUEST_CODE_FILE = 1;

    /**
     * 单例实现
     * @return
     */
    public static ShoppingApplication getInstance() {
        return shoppingApp;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "执行了ShoppingApplication.onTerminate");
        // 给类的全局对象赋值
        shoppingApp = this;
        // 初始化商品信息
        initGoodInfo();
    }

    /**
     * 初始化商品信息
     */
    private void initGoodInfo() {
        // 获取共享参数保存的是否首次打开参数
        boolean firstFlag = SharedPreferenceUtil.getInstance(this).getShoppingFirstFlag("first", true);
        // 获取APP私有下载路径
        String directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+ File.separatorChar;
        Log.d(TAG, "应用私有下载路径：" + directory);
        if (firstFlag) {
            // 模拟网络图片下载
            ArrayList<GoodsInfo> goodsInfos = GoodsInfo.getDefaultList();
            for (GoodsInfo info : goodsInfos) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), info.pic);
                String path = directory + info.id + ".jpg";
                // 将图片保存到应用私有路径下
                FileUtil.saveImage(path, bitmap);
                // 回收位图对象
                bitmap.recycle();
                info.picPath = path;
            }
            // 打开数据库，把商品信息插入到表中
            ShoppingDBHelper dbHelper = ShoppingDBHelper.getInstance(this);
            dbHelper.openWriteLink();
            dbHelper.insertGoodsInfos(goodsInfos);
            dbHelper.closeDatabaseLink();
            // 把是否首次打开写入共享参数
            SharedPreferenceUtil.getInstance(this).setShoppingFirstFlag("first", false);
        }
    }



    /**
     * 此方法为系统开发时使用，应用开发不会调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "执行了onTerminate");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "执行了ShoppingApplication.onConfigurationChanged");
    }
}
