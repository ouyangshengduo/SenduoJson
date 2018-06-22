package senduo.com.senduojson.fast;

import java.lang.reflect.Type;

import senduo.com.senduojson.fast.deserializer.ObjectDeserializer;
import senduo.com.senduojson.fast.serializer.ObjectSerializer;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/17
 * * 文件描述：
 * * 修改历史：2018/6/17 20:18*************************************
 **/
public class JSON {

    /**
     * 类到json格式字符串(序列化)
     * @param object
     * @return
     */
    public static String toJSONString(Object object){

        ObjectSerializer serializer = JsonConfig.getGlobInstance().getSerializer(object.getClass());
        StringBuilder sb = new StringBuilder();
        serializer.serializer(null,JsonConfig.getGlobInstance(),sb,object);
        return sb.toString();
    }

    public static <T> T parse(String json,Class<T> clazz){
        return parse(json,(Type)clazz);
    }

    public static <T> T parse(String json,Type type){
        ObjectDeserializer deserializer = JsonConfig.getGlobInstance().getDeserializer(type);
        try{
            return deserializer.deserializer(JsonConfig.getGlobInstance(),json,null);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
        return null;
    }


}
