package senduo.com.senduojson.fast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/17
 * * 文件描述：
 * * 修改历史：2018/6/17 20:36*************************************
 **/

public class FieldInfo {

    public String name;
    public Field field;
    public Method method;
    public Class type;

    public Type genericType;

    public FieldInfo(String name,Method method,Field field){
        this(name,method,field,false);
    }

    public FieldInfo(String name, Method method, Field field,boolean isSetter) {
        this.name = name;
        this.field = field;
        this.method = method;
        //对应Key的Value类型
        type = method != null ? method.getReturnType() : field.getType();
        if(isSetter){
            //当我们采集set函数的时候 实际上已经过滤了 有且只有一个参数的函数
            if(null != method){
                genericType = method.getGenericParameterTypes()[0];
            }else{
                genericType = field.getGenericType();
            }
        }
    }

    public Object get(Object object) {
        try {
            return method != null ? method.invoke(object) : field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(Object object, Object value) {
        try {
            if (method != null) {
                method.invoke(object, value);
            } else {
                field.set(object, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
