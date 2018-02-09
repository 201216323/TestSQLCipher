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
