# TestSQLCipher
项目中使用到了SQLCipher数据库，之前没有接触到这个框架，所以本片就详细的说明一下SQLCipher数据库。


## 1：简介
[SQLCipher](https://www.zetetic.net/sqlcipher/)是一个在SQLite基础上进行扩展的一款开源数据库，比起Android原生的SQLite数据库来说，最主要的优势就是可加密解密，SQLCipher具有较小的占用空间和出色的性能，因此它非常适合于保护嵌入式应用程序数据库，并且非常适合于移动开发。

具体优势为：

- 加密性能高、只有5%--15%的加密开销
- 完全对数据库中的文件进行加密
- 采用良好的加密方式（CBS加密模式）
- 配置简单，做到应用级别加密
- 采用OpenSSL加密库提供的算法

同时，SQLCipher也提供其它平台的支持，例如：C/C++、Obj-C、Java、Python、Ruby、Android 等。

## 2：使用方式
下面说明我的使用方式，由于版本差异，现在以最新的版本说明，后面将会结合我项目中的版本以及使用方式再说明一下。
#### （1.）在buile.gralde文件中添加SQLCipher的引用，当前最新版本为3.5.9，[点我查看最新版本](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/)


```
compile 'net.zetetic:android-database-sqlcipher:3.5.9@aar'
```
#### （2.）SQLiteOpenHelper注意接下来所以有关Sqlite相关类全部引用net.sqlcipher.database的类


```
package bruce.chang.testsqlcipher;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by: Bruce Chang
 * Data on: 2018/2/8.
 * Time on: 11:13
 * Day on：星期四
 * Project Name：AndroidStudioNew
 * Package Name：bruce.chang.testsqlcipler
 * Function Desc：功能描述区域
 */

public class DBCipherHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "test.db";//数据库名字
    private static final int DB_VERSION = 1;   // 数据库版本

    public static final String DB_PWD="123456";//数据库密码
    public static String TABLE_NAME = "person";// 表名
    public static String FIELD_ID = "id";// 列名
    public static String  FIELD_NAME= "name";// 列名


    public DBCipherHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        //不可忽略的 进行so库加载
        SQLiteDatabase.loadLibs(context);
    }

    public DBCipherHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 创建数据库
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        createTable(db);
    }

    private void createTable(SQLiteDatabase db){
        //create table person （id integer primary key autoincrement , name text not null）
        String  sql = "CREATE TABLE " + TABLE_NAME + "(" + FIELD_ID + " integer primary key autoincrement , " + FIELD_NAME + " text not null);";
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            Log.e(TAG, "onCreate " + TABLE_NAME + "Error" + e.toString());
            return;
        }
    }

    /**
     * 数据库升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

```
==注意：SQLiteDatabase.loadLibs(context);这个千万别忘记调用==


#### （3.）创建一个DBCipherManager数据库管理

具体实现传统的SQLiteOpenHelper都是完全相同的，不同的地方在获取数据库句柄的地方

-   传统方式：
```
      //获取可写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //获取可读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase();  
```

-   现在的方式：需要传入一个password，这个password就是用于加密的秘钥 
```
      //获取写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase(DBCipherHelper.DB_PWD);
        //获取可读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD);
```
  
接下来就是具体实现：


```
package bruce.chang.testsqlcipher;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by: Bruce Chang
 * Data on: 2018/2/8.
 * Time on: 11:16
 * Day on：星期四
 * Project Name：AndroidStudioNew
 * Package Name：bruce.chang.testsqlcipler
 * Function Desc：功能描述区域
 */

public class DBCipherManager {

    private static final String TAG = "DatabaseManager";
    // 静态引用
    private volatile static DBCipherManager mInstance;
    // DatabaseHelper
    private DBCipherHelper dbHelper;

    private DBCipherManager(Context context) {
        dbHelper = new DBCipherHelper(context.getApplicationContext());
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBCipherManager getInstance(Context context) {
        DBCipherManager inst = mInstance;
        if (inst == null) {
            synchronized (DBCipherManager.class) {
                inst = mInstance;
                if (inst == null) {
                    inst = new DBCipherManager(context);
                    mInstance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 插入数据
     */
    public void insertData(String name) {
        //获取写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase(DBCipherHelper.DB_PWD);
        //生成要修改或者插入的键值
        ContentValues cv = new ContentValues();
        cv.put(DBCipherHelper.FIELD_NAME, name);
        // insert 操作
        db.insert(DBCipherHelper.TABLE_NAME, null, cv);
        //关闭数据库
        db.close();
    }

    /**
     * 未开启事务批量插入
     *
     * @param testCount
     */
    public void insertDatasByNomarl(int testCount) {
        //获取写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase(DBCipherHelper.DB_PWD);
        for (int i = 0; i < testCount; i++) {
            //生成要修改或者插入的键值
            ContentValues cv = new ContentValues();
            cv.put(DBCipherHelper.FIELD_NAME, String.valueOf(i));
            // insert 操作
            db.insert(DBCipherHelper.TABLE_NAME, null, cv);
            Log.e(TAG, "insertDatasByNomarl");
        }
        //关闭数据库
        db.close();
    }

    /**
     * 测试开启事务批量插入
     *
     * @param testCount
     */
    public void insertDatasByTransaction(int testCount) {
        //获取写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase(DBCipherHelper.DB_PWD);
        db.beginTransaction();  //手动设置开始事务
        try {
            //批量处理操作
            for (int i = 0; i < testCount; i++) {
                //生成要修改或者插入的键值
                ContentValues cv = new ContentValues();
                cv.put(DBCipherHelper.FIELD_NAME, String.valueOf(i));
                // insert 操作
                db.insert(DBCipherHelper.TABLE_NAME, null, cv);
                Log.e(TAG, "insertDatasByTransaction");
            }
            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
        } catch (Exception e) {

        } finally {
            db.endTransaction(); //处理完成
            //关闭数据库
            db.close();
        }
    }

    /**
     * 删除数据
     */
    public void deleteData(String name) {
        //生成条件语句
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append(DBCipherHelper.FIELD_NAME).append(" = ").append("'").append(name).append("'");
        //获取写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase(DBCipherHelper.DB_PWD);
        // delete 操作
        db.delete(DBCipherHelper.TABLE_NAME, whereBuffer.toString(), null);
        //关闭数据库
        db.close();
    }

    /**
     * 删除所有数据
     */
    public void deleteDatas() {
        String sql = "delete from " + DBCipherHelper.TABLE_NAME;
        execSQL(sql);
    }

    /**
     * 更新数据
     */
    public void updateData(String name) {
        //生成条件语句
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append(DBCipherHelper.FIELD_NAME).append(" = ").append("'").append(name).append("'");
        //生成要修改或者插入的键值
        ContentValues cv = new ContentValues();
        cv.put(DBCipherHelper.FIELD_NAME, name + name);
        //获取写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase(DBCipherHelper.DB_PWD);
        // update 操作
        db.update(DBCipherHelper.TABLE_NAME, cv, whereBuffer.toString(), null);
        //关闭数据库
        db.close();
    }

    /**
     * 指定条件查询数据
     */
    public void queryDatas(String name) {
        //生成条件语句
        StringBuffer whereBuffer = new StringBuffer();
        whereBuffer.append(DBCipherHelper.FIELD_NAME).append(" = ").append("'").append(name).append("'");
        //指定要查询的是哪几列数据
        String[] columns = {DBCipherHelper.FIELD_NAME};
        //获取可读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD);
        //查询数据库
        Cursor cursor = null;
        try {
            cursor = db.query(DBCipherHelper.TABLE_NAME, columns, whereBuffer.toString(), null, null, null, null);
            while (cursor.moveToNext()) {
                int count = cursor.getColumnCount();
                String columName = cursor.getColumnName(0);
                String tname = cursor.getString(0);
                Log.e(TAG, "count = " + count + " columName = " + columName + "  name =  " + tname);
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "queryDatas" + e.toString());
        }
        //关闭数据库
        db.close();
    }

    /**
     * 查询全部数据
     */
    public void queryDatas() {
        //指定要查询的是哪几列数据
        String[] columns = {DBCipherHelper.FIELD_NAME};
        //获取可读数据库
        SQLiteDatabase db = dbHelper.getReadableDatabase(DBCipherHelper.DB_PWD);
        //查询数据库
        Cursor cursor = null;
        try {
            cursor = db.query(DBCipherHelper.TABLE_NAME, columns, null, null, null, null, null);//获取数据游标
            while (cursor.moveToNext()) {
                int count = cursor.getColumnCount();
                String columeName = cursor.getColumnName(0);//获取表结构列名
                String name = cursor.getString(0);//获取表结构列数据
//                String name2 = cursor.getString(1);//获取表结构列数据
                Log.e(TAG, "count = " + count + " columName = " + columeName + "  name =  " + name );//+ "  name2 =  " + name2
            }
            //关闭游标防止内存泄漏
            if (cursor != null) {
                cursor.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "queryDatas" + e.toString());
        }
        //关闭数据库
        db.close();
    }

    /**
     * 执行sql语句
     */
    private void execSQL(String sql) {
        //获取写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase(DBCipherHelper.DB_PWD);
        //直接执行sql语句
        db.execSQL(sql);//或者
        //关闭数据库
        db.close();
    }

    /**
     * 事务测试
     *
     * @param testCount
     */
    public void testTransaction(int testCount) {
        //获取写数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase(DBCipherHelper.DB_PWD);
        db.beginTransaction();  //手动设置开始事务
        try {
            //在此处理批量操作
            for (int i = 0; i < testCount; i++) {
                //生成要修改或者插入的键值
                ContentValues cv = new ContentValues();
                cv.put(DBCipherHelper.FIELD_NAME, "原有" + String.valueOf(i));
                // insert 操作
                db.insert(DBCipherHelper.TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful(); //设置事务处理成功，不设置会自动回滚不提交
        } catch (Exception e) {

        } finally {
            db.endTransaction(); //处理完成
            //关闭数据库
            db.close();
        }
    }

}

```

#### （4.）具体在MainaAtivity调用：


```
      //清空数据
                DBCipherManager.getInstance(MainActivity.this).deleteDatas();
                //插入数据
                for (int i = 0; i < 10; i++) {
                    DBCipherManager.getInstance(MainActivity.this).insertData(String.valueOf(i));
                }
                //删除数据
                DBCipherManager.getInstance(MainActivity.this).deleteData(String.valueOf(5));
                //更新数据
                DBCipherManager.getInstance(MainActivity.this).updateData(String.valueOf(3));
                //查询数据
                DBCipherManager.getInstance(MainActivity.this).queryDatas();
```

#### （5.）温习一下原生的SQLite数据库，后面要根据这两个比较生成的这两个数据库


```
package bruce.chang.testsqlcipher;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

/**
 * Created by: Bruce Chang
 * Data on: 2018/2/8.
 * Time on: 13:16
 * Day on：星期四
 * Project Name：AndroidStudioNew
 * Package Name：bruce.chang.testsqlcipler
 * Function Desc：原生的Android数据库
 */

public class OriginSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "test2.db";
//    String sql = "CREATE TABLE " + "aaa" + "(" + "id" + " integer primary key autoincrement , " + "name" + " text not null);";
    private static final String SQL_CREATE_TEST_TABLE = "CREATE TABLE " + TestTable.TABLE_NAME +
            " (" +
            TestTable._ID + " integer primary key autoincrement default 0, " +
            TestTable.COLUMN_NAME_TEST_NAME + " text, " +
            TestTable.COLUMN_NAME_TEST_TIME_YEAR + " integer, " +
            TestTable.COLUMN_NAME_TEST_TIME_MONTH + " integer, " +
            TestTable.COLUMN_NAME_TEST_TIME_DAY + " integer" +
            " )";

    private static final String SQL_DELETE_TEST_TABLE = "DROP TABLE IF EXISTS " +
            TestTable.TABLE_NAME;

    private static OriginSQLiteOpenHelper mInstance;


    private OriginSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static OriginSQLiteOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (OriginSQLiteOpenHelper.class) {
                if (mInstance == null) {
                    mInstance = new OriginSQLiteOpenHelper(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TEST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public class TestTable implements Serializable {
        public static final String TABLE_NAME = "test";
        public static final String _ID = "id";
        public static final String COLUMN_NAME_TEST_NAME = "TEST_name";
        public static final String COLUMN_NAME_TEST_TIME_YEAR = "TEST_year";
        public static final String COLUMN_NAME_TEST_TIME_MONTH = "TEST_month";
        public static final String COLUMN_NAME_TEST_TIME_DAY = "TEST_day";
    }
}

```

到此为止：两种数据库完事，运行程序后，我们可以在APP的私有文件下看到两种数据库如下：



