package senduo.com.senduojson.fast.deserializer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import senduo.com.senduojson.fast.FieldInfo;
import senduo.com.senduojson.fast.JsonConfig;
import senduo.com.senduojson.fast.Utils;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/19
 * * 文件描述：
 * * 修改历史：2018/6/19 15:23*************************************
 **/
public class JavabeanDeserializer implements ObjectDeserializer {

    private final Class<?> beanType;
    private final List<FieldInfo> fieldInfos;

    public JavabeanDeserializer(Class<?> clazz){
        this.beanType = clazz;
        Map<String,Field> fieldCacheMap = new HashMap<>();
        Utils.parserAllFieldToCache(fieldCacheMap,beanType);
        fieldInfos = Utils.computeSetters(beanType,fieldCacheMap);
    }

    @Override
    public <T> T deserializer(JsonConfig config, String json, Object object) throws Throwable {
        //JSONObject jsonObject = new JSONObject(json);
        JSONObject jsonObject = null;
        if(null == object){
            jsonObject = new JSONObject(json);
        }else{
            jsonObject = (JSONObject) object;
        }

        T t = null;
        try{
            t = (T) beanType.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        //{"age":100,"name":"testname","test":1,"list":["1","2"]}
        for(FieldInfo fieldInfo : fieldInfos){
            //json数据中没有对应的key
            if(!jsonObject.has(fieldInfo.name)){
                continue;
            }

            Object value = jsonObject.get(fieldInfo.name);

            if(value instanceof JSONObject
                    || value instanceof JSONArray){
                ObjectDeserializer deserializer = config.getDeserializer(fieldInfo.genericType);
                Object obj = deserializer.deserializer(config,null,value);
                fieldInfo.set(t,obj);
            }else{
                if(value != JSONObject.NULL){
                    fieldInfo.set(t,value);
                }
            }
        }

        return t;
    }



}
