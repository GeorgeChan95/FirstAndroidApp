package com.george.chapter06.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.george.chapter06.entity.BookInfo;

import java.util.List;

@Dao // 该注解表示此类为持久化类
public interface BookDao {

    /**
     * 新增数据， 可以同时添加一条或者多条数据
     * onConflict 数据冲突时，保存策略：替换
     * @param bookInfo
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BookInfo bookInfo);

    /**
     * 批量保存数据
     * onConflict 数据冲突时，保存策略：替换
     * @param bookList
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<BookInfo> bookList);

    /**
     * 删除一个或多个
     * @param books
     * @return
     */
    @Delete
    int delete(BookInfo... books);

    /**
     * 删除所有
     * @return
     */
    @Query(value = "delete from book_info where 1=1")
    int deleteAll();

    /**
     * 单个或批量更新
     * @param books
     * @return
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(BookInfo... books);

    /**
     * 批量更新
     * @param bookList
     * @return
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateList(List<BookInfo> bookList);

    /**
     * 根据书名查询
     * @param name
     * @return
     */
    @Query(value = "select * from book_info where name = :name limit 1")
    BookInfo queryByName(String name);

    /**
     * 查询所有
     * @return
     */
    @Query(value = "select * from book_info")
    List<BookInfo> queryList();
}
