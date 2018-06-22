package senduo.com.senduojson.fast.deserializer;

import senduo.com.senduojson.fast.JsonConfig;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/19
 * * 文件描述：
 * * 修改历史：2018/6/19 15:19*************************************
 **/
public interface ObjectDeserializer {
    <T> T deserializer(JsonConfig config,String json,Object object) throws Throwable;
}
