package com.george.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.george.chapter06.dao.BookDao;
import com.george.chapter06.entity.BookInfo;
import com.george.chapter06.util.ToastUtil;

import java.util.List;

public class RoomWriteActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "GeorgeTag";

    private EditText et_name;
    private EditText et_author;
    private EditText et_press;
    private EditText et_price;

    private BookDao bookDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_write);

        et_name = findViewById(R.id.et_name);
        et_author = findViewById(R.id.et_author);
        et_press = findViewById(R.id.et_press);
        et_price = findViewById(R.id.et_price);

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_query).setOnClickListener(this);

        bookDao = MainApplication.getInstance().getBookDB().bookDao();
    }

    @Override
    public void onClick(View v) {
        String name = et_name.getText().toString();
        String author = et_author.getText().toString();
        String press = et_press.getText().toString();
        String price = et_price.getText().toString();

        switch (v.getId()) {
            case R.id.btn_save:
                BookInfo bookInfo = new BookInfo();
                bookInfo.setName(name);
                bookInfo.setAuthor(author);
                bookInfo.setPress(press);
                if (price != null && !price.equals("")) {
                    bookInfo.setPrice(Double.valueOf(price));
                }
                long row = bookDao.insert(bookInfo);
                if (row > 0) {
                    ToastUtil.show(this, "保存成功");
                }
                break;
            case R.id.btn_update:
                bookInfo = bookDao.queryByName(name);
                if (bookInfo != null) {
                    bookInfo.setName(name);
                    bookInfo.setAuthor(author);
                    bookInfo.setPress(press);
                    if (price != null && !price.equals("")) {
                        bookInfo.setPrice(Double.valueOf(price));
                    }
                }
                row = bookDao.update(bookInfo);
                if (row > 0) {
                    ToastUtil.show(this, "更新成功");
                }
                break;
            case R.id.btn_delete:
                bookInfo = bookDao.queryByName(name);
                if (bookInfo != null) {
                    row = bookDao.delete(bookInfo);
                    if (row > 0) {
                        ToastUtil.show(this, "删除成功");
                    }
                }
                break;
            case R.id.btn_query:
                bookInfo = bookDao.queryByName(name);
                if (bookInfo != null) {
                    et_name.setText(bookInfo.getName());
                    et_author.setText(bookInfo.getAuthor());
                    et_press.setText(bookInfo.getPress());
                    et_price.setText(bookInfo.getPrice() == null ? "" : bookInfo.getPrice().toString());
                }
                List<BookInfo> bookInfos = bookDao.queryList();
                Log.d(TAG, "bookInfos: " + bookInfos.toString());
                break;
        }
    }
}