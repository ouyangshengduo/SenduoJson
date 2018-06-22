package senduo.com.senduojson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import senduo.com.senduojson.fast.JSON;
import senduo.com.senduojson.fast.TypeReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test();
    }

    private void test() {

        List<List<Child>> childLists = new ArrayList<>();
        List<Child> child1 = new ArrayList<>();
        child1.add(new Child("T1", 100));
        child1.add(new Child("T2", 200));
        childLists.add(child1);
        List<Child> child2 = new ArrayList<>();
        Child t3 = new Child("T3", 300);
/*        t3.childs = new ArrayList<>();
        t3.childs.add(new Child("T3_1", 3100));
        t3.childs.add(new Child("T3_2", 3200));*/
        child2.add(t3);
        child2.add(new Child("T4", 400));
        childLists.add(child2);


        String s = JSON.toJSONString(childLists);
        Log.e(TAG,s);

        Object object = JSON.parse(s, new TypeReference<List<List<Child>>>() {
        }.getType());
        //Object object = JSON.parse(s, Child.class);
        Log.e(TAG,object.toString());
    }
}
