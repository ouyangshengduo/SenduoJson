package senduo.com.senduojson.fast.serializer;


import senduo.com.senduojson.fast.FieldInfo;
import senduo.com.senduojson.fast.JsonConfig;
import senduo.com.senduojson.fast.Utils;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/17
 * * 文件描述：
 * * 修改历史：2018/6/17 20:36*************************************
 **/

public class FieldSerializer {


    private final FieldInfo fieldInfo;
    private final String key;
    private final boolean isPrimitive;

    public FieldSerializer(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
        this.key = '"' + fieldInfo.name + "\":";
        //value的类型
        Class type = fieldInfo.type;
        //是否是基本数据类型或者它的包装类
        isPrimitive = Utils.isBox(type) || type.isPrimitive();
    }

    //{"age":100,"name":"testname","test":1,"list":["1","2"]}
    public String serializer(SerializerContext serializerContext,JsonConfig config, Object object) {
        Object o = fieldInfo.get(object);
        //属性没有值
        if (null == o) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (isPrimitive) {
            sb.append(key);
            sb.append(o);
        } else if (Utils.isString(fieldInfo.type)) {
            sb.append(key);
            sb.append("\"");
            sb.append(o);
            sb.append("\"");
        } else {
            //JavaBean List

            if(serializerContext.refrence.containsKey(key) && serializerContext.refrence.get(key) == object){
                return sb.toString();
            }
            serializerContext.refrence.put(key,object);
            ObjectSerializer serializer = config.getSerializer(fieldInfo.type);
            sb.append(key);
            serializer.serializer(serializerContext,config, sb, o);
        }
        return sb.toString();
    }
}
