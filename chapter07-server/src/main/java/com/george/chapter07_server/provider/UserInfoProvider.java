package com.george.chapter07_server.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.george.chapter07_server.database.UserDBHelper;

public class UserInfoProvider extends ContentProvider {
    private String TAG = "GeorgeTag";
    private UserDBHelper dbHelper;
    private static final int USERS = 1; // 表示一次操作1-n条数据
    private static final int USER = 2; // 表示一次仅操作一个用户
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private SQLiteDatabase rDB;
    private SQLiteDatabase wDB;

    static {
        // 往Uri匹配器中添加指定的数据路径
        // URI路径为：content://com.george.chapter07_server.provider.UserInfoProvider/user
        URI_MATCHER.addURI(UserInfoContent.AUTHORITIES, "/user", USERS);
        // 添加路径为 content://com.george.chapter07_server.provider.UserInfoProvider/user/2
        URI_MATCHER.addURI(UserInfoContent.AUTHORITIES, "/user/#", USER);
    }

    public UserInfoProvider() {
    }

    @Override
    public boolean onCreate() {
        // 加载DBHelper实例
        dbHelper = UserDBHelper.getInstance(getContext());
        // 打开数据库帮助器的读写连接
        rDB = dbHelper.openReadLink();
        wDB = dbHelper.openWriteLink();
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        dbHelper.closeLink();
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 插入数据
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (URI_MATCHER.match(uri) == USERS) { // 匹配到了用户信息表
            // 获取SQLite数据库的写连接
            SQLiteDatabase wDB = dbHelper.getWritableDatabase();
            // 向指定的表插入数据，返回记录的行号
            long row = wDB.insert(UserInfoContent.TABLE_NAME, null, values);
            if (row > 0) { // 判断数据是否插入成功
                // 如果添加成功，就利用新记录的行号生成新的地址
                Uri newUri = ContentUris.withAppendedId(uri, row);
                // 通知监听器，数据已经改变
                getContext().getContentResolver().notifyChange(newUri, null);
            }
        }
        return uri;
    }

    /**
     * 根据指定条件删除数据
     * @param uri 用户匹配请求的Uri
     * @param selection 删除条件语句
     * @param selectionArgs 条件参数
     * @return 删除的记录数
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        // 获取SQLite数据库的写连接
        SQLiteDatabase wDB = dbHelper.getWritableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case USERS: // 一次操作多条数据
                // 执行SQLite的删除操作，并返回删除记录的数目
                count = wDB.delete(UserInfoContent.TABLE_NAME, selection, selectionArgs);
                break;
            case USER:
                // URI中获取参数id
                String id = uri.getLastPathSegment();
                // 执行SQLite的删除操作，并返回删除记录的数目
                count = wDB.delete(UserInfoContent.TABLE_NAME, "id=?", new String[]{id});
                break;
        }
        return count;
    }

    /**
     * 数据查询
     * @param uri 请求Uri
     * @param projection 查询的列数组，为NULL表示查询所有列
     * @param selection 查询条件语句
     * @param selectionArgs 查询参数
     * @param sortOrder 排序字段
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        if (URI_MATCHER.match(uri) == USERS) {
            // 获取SQLite数据库的读连接
            SQLiteDatabase rDB = dbHelper.getReadableDatabase();
            // 执行SQLite的查询操作
            cursor = rDB.query(UserInfoContent.TABLE_NAME, null, selection, selectionArgs, null, null, sortOrder);
            // 设置内容解析器的监听
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    /**
     * 数据更新
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        if (URI_MATCHER.match(uri) == USERS) {
            SQLiteDatabase wDB = dbHelper.getWritableDatabase();
            count = wDB.update(UserInfoContent.TABLE_NAME, values, selection, selectionArgs);
            if (count > 0) {
                Log.d(TAG, "UserInfo更新操作成功，count: +" + count);
            }
        }
        return count;
    }
}