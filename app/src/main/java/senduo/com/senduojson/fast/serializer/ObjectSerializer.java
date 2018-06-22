package senduo.com.senduojson.fast.serializer;

import senduo.com.senduojson.fast.JsonConfig;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/17
 * * 文件描述：
 * * 修改历史：2018/6/17 20:23*************************************
 **/
public interface ObjectSerializer {
    void serializer(SerializerContext serializerContext,JsonConfig config, StringBuilder out, Object object);
}
