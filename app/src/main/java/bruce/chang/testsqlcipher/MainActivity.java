package bruce.chang.testsqlcipher;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_origin:
                OriginSQLiteOpenHelper dbHelper = OriginSQLiteOpenHelper.getInstance(this);

                for (int i = 0; i < 1000; i++) {
                    android.database.sqlite.SQLiteDatabase db = dbHelper.getWritableDatabase();
                    //生成要修改或者插入的键值
                    ContentValues cv = new ContentValues();
                    cv.put("TEST_name", "TEST_name");
                    cv.put("TEST_year", 2017 + i);
                    cv.put("TEST_month", 2 + i);
                    cv.put("TEST_day", 8 + i);

                    // insert 操作
                    db.insert("test", null, cv);
                    //关闭数据库
                    db.close();
                }

                break;
            case R.id.bt_clear:
                //清空数据
                DBCipherManager.getInstance(this).deleteDatas();
                break;

            case R.id.bt_test_transaction:
                DBCipherManager.getInstance(this).testTransaction(3);
                break;

            case R.id.bt_add:
                //插入数据
                for (int i = 0; i < 1000; i++) {
                    DBCipherManager.getInstance(this).insertData(String.valueOf(i));
                }
                break;
            case R.id.bt_deleate:
                //删除数据
                DBCipherManager.getInstance(this).deleteData(String.valueOf(5));
                break;
            case R.id.bt_alter:
                //更新数据
                DBCipherManager.getInstance(this).updateData(String.valueOf(3));
                break;
            case R.id.bt_select:
                //查询数据
                DBCipherManager.getInstance(this).queryDatas();
                break;
        }
    }
}
