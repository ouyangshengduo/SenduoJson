package senduo.com.senduojson;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

import senduo.com.senduojson.fast.JSON;
import senduo.com.senduojson.fast.TypeReference;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void addition_isCorrect2(){
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


        String s = JSON.toJSONString(t3);
        System.out.println(s);

        String tmp = "{\"value\" : \"123456\"}";
        try {
            JSONObject jsonObject = new JSONObject(tmp);
            System.out.println(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Object object = JSON.parse(s, new TypeReference<List<List<Child>>>() {
        }.getType());
        //Object object = JSON.parse(s, Child.class);
        System.out.println(object);
    }

    @Test
    public void test(){
        Class<ComputerBook> bookClass = ComputerBook.class;
        //获得类以及父类中所有声明为public的属性
        System.out.println("所有public属性:");
        for (Field field : bookClass.getFields()) {
            System.out.println(field.getName());
        }
        //获得类(不包括父类)中所有的属性
        System.out.println("所有属性:");
        for (Field field : bookClass.getDeclaredFields()) {
            System.out.println(field.getName());
        }
        //获得类以及父类中所有声明为public的函数
        System.out.println("所有public函数:");
        for (Method method : bookClass.getMethods()) {
            String methodName = method.getName();
            System.out.println(methodName);
        }

        //获得类(不包括父类)中所有的函数
        System.out.println("所有函数:");
        for (Method method : bookClass.getDeclaredMethods()) {
            String methodName = method.getName();
            System.out.println(methodName);
        }


        try {
            ComputerBook cb = bookClass.newInstance();
            Method method = bookClass.getDeclaredMethods()[0];
            //在obj对象上调用函数
            if(method.getParameters().length == 1) {
                method.invoke(cb, 1);
            }else{
                Object object = method.invoke(cb, null);
                System.out.println((Integer) object);
            }

            Field field = bookClass.getDeclaredFields()[0];
            field.setAccessible(true);
            //获得obj中的属性
            Object value1 = field.get(cb);
            System.out.println(value1);
            //设置obj中的属性
            field.set(cb,2);
            Object value2 = field.get(cb);
            System.out.println(value2);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        try {
            Field list = Book.class.getField("wildcardTypeDatas");

            //判断list是否为List集合类型
            if(List.class.isAssignableFrom(list.getType())){

                Type genericType = list.getGenericType();//获取属性声明时的类型

                //如果是属于参数化类型例如List<String>
                if(genericType instanceof ParameterizedType){
                    //获取泛型类型，这里的0代表第一个参数：String
                    Type type = ((ParameterizedType)genericType).getActualTypeArguments()[0];
                    //判断是否使用了通配符
                    if(type instanceof WildcardType){

                        WildcardType wildcardType = (WildcardType) type;
                        Type[] upperBounds = wildcardType.getUpperBounds();
                        Type[] lowerBounds = wildcardType.getLowerBounds();

                        if(upperBounds.length == 1){
                            Type actualTypeArgument = upperBounds[0];
                            System.out.println("获得泛型上边界类型:" + actualTypeArgument);
                        }

                        if(lowerBounds.length == 1){
                            Type actualTypeArgument = lowerBounds[0];
                            System.out.println("获得泛型下边界类型:" + actualTypeArgument);
                        }
                    }else {
                        System.out.println("获得泛型类型:" + type);
                    }
                }

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}