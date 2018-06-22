package senduo.com.senduojson.fast.serializer;

import java.util.List;

import senduo.com.senduojson.fast.JsonConfig;
import senduo.com.senduojson.fast.Utils;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/19
 * * 文件描述：
 * * 修改历史：2018/6/19 11:29*************************************
 **/
public class ListSerializer implements ObjectSerializer {

    public static final ListSerializer instance = new ListSerializer();

    @Override
    public void serializer(SerializerContext serializerContext, JsonConfig config, StringBuilder out, Object object) {

        List<?> list = (List<?>) object;

        if(list.isEmpty()){
            out.append("[]");
            return;
        }

        out.append("[");
        for(int i = 0 ; i < list.size() ; i ++){
            if(i != 0){
                out.append(",");
            }
            Object item = list.get(i);
            if(null == item){
                out.append("null");
            }else{
                Class<?> clazz = item.getClass();
                if(Utils.isBox(clazz)){
                    //如果是基本数据类型
                    out.append(item);
                }else if(Utils.isString(clazz)){
                    //如果是字符串类型
                    out.append("\"");
                    out.append(item);
                    out.append("\"");
                }else{
                    //如果是其他类型，交给其他序列化器
                    ObjectSerializer serializer = config.getSerializer(clazz);
                    serializer.serializer(serializerContext != null ? serializerContext: new SerializerContext(),config,out,object);
                }
            }

        }

        out.append("]");


    }
}
