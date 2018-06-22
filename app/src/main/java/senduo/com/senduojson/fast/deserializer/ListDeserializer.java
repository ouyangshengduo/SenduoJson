package senduo.com.senduojson.fast.deserializer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import senduo.com.senduojson.fast.JsonConfig;
import senduo.com.senduojson.fast.Utils;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/19
 * * 文件描述：
 * * 修改历史：2018/6/19 16:36*************************************
 **/
public class ListDeserializer implements ObjectDeserializer {

    private final ParameterizedType type;
    public ListDeserializer(ParameterizedType type){
        this.type = type;
    }

    @Override
    public <T> T deserializer(JsonConfig config, String json, Object object) throws Throwable {
        JSONArray jsonArray;
        if(null == object){
            jsonArray = new JSONArray(json);
        }else{
            jsonArray = (JSONArray) object;
        }

        List list = new ArrayList();

        for(int i = 0 ; i < jsonArray.length(); i ++){
            Object itemObj = jsonArray.get(i);
            if(itemObj instanceof JSONArray
                    || itemObj instanceof JSONObject){
                Type itemType = Utils.getItemType(type);
                ObjectDeserializer deserializer = config.getDeserializer(itemType);
                Object item = deserializer.deserializer(config,null,itemObj);
                list.add(item);
            }else{
                list.add(itemObj);
            }
        }
        return (T)list;
    }
}
