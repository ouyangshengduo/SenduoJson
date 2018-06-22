package senduo.com.senduojson.fast;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import senduo.com.senduojson.fast.deserializer.JavabeanDeserializer;
import senduo.com.senduojson.fast.deserializer.ListDeserializer;
import senduo.com.senduojson.fast.deserializer.ObjectDeserializer;
import senduo.com.senduojson.fast.serializer.JavaBeanSerializer;
import senduo.com.senduojson.fast.serializer.ListSerializer;
import senduo.com.senduojson.fast.serializer.ObjectSerializer;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/17
 * * 文件描述：
 * * 修改历史：2018/6/17 20:20*************************************
 **/
public class JsonConfig {

    private static JsonConfig globInstance = new JsonConfig();

    //序列化缓存
    private Map<Class,ObjectSerializer> serializers = new HashMap<>();
    private Map<Type,ObjectDeserializer> deserializers = new HashMap<>();

    public static JsonConfig getGlobInstance(){
        return globInstance;
    }

    public ObjectSerializer getSerializer(Class<?> clazz){
        ObjectSerializer objectSerializer = serializers.get(clazz);

        if(null != objectSerializer){
            return objectSerializer;
        }

        if(List.class.isAssignableFrom(clazz)){
            objectSerializer = ListSerializer.instance;
        }else if(Map.class.isAssignableFrom(clazz)){
            throw new RuntimeException("Map序列化未实现");
        }else if(clazz.isArray()){
            throw new RuntimeException("数组序列化未实现");
        }else{
            objectSerializer = new JavaBeanSerializer(clazz);
        }
        serializers.put(clazz,objectSerializer);
        return objectSerializer;
    }

    public ObjectDeserializer getDeserializer(Type type){
        ObjectDeserializer objectDeserializer = deserializers.get(type);

        if(objectDeserializer != null){
            return objectDeserializer;
        }

        if(type instanceof Class){
            objectDeserializer = new JavabeanDeserializer((Class<?>) type);
        }else if(type instanceof ParameterizedType){
            //List<Items> 带参数类型
            objectDeserializer = new ListDeserializer((ParameterizedType) type);
        }
        deserializers.put(type,objectDeserializer);
        return objectDeserializer;
    }

}
