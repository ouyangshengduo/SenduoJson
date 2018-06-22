package senduo.com.senduojson.fast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import senduo.com.senduojson.fast.serializer.FieldSerializer;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/17
 * * 文件描述：通过反射的技术收集序列化和反序列化所需要的key与value
 * * 修改历史：2018/6/17 20:36*************************************
 **/

public class Utils {

    public static boolean isBox(Class type) {
        return type == Integer.class ||
                type == Character.class ||
                type == Byte.class ||
                type == Boolean.class ||
                type == Double.class ||
                type == Float.class ||
                type == Short.class;
    }

    public static boolean isString(Class type) {
        return CharSequence.class.isAssignableFrom(type);
    }


    /**
     * 获得对于class包括父class所有的成员属性
     *
     * @param clazz
     * @return
     */
    public static Map<String, Field> parserAllFieldToCache(Map<String, Field> fieldCacheMap, Class<?> clazz) {
        //获得自己的所有属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!fieldCacheMap.containsKey(fieldName)) {
                fieldCacheMap.put(fieldName, field);
            }
        }
        //查找父类 的属性
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            parserAllFieldToCache(fieldCacheMap, clazz.getSuperclass());
        }
        return fieldCacheMap;
    }

    /**
     * 获得需要序列化的成员
     * 包括: 当前类与父类的get函数、public成员属性
     *
     * @param clazz
     */
    public static List<FieldSerializer> computeGetters(Class<?> clazz, Map<String, Field>
            fieldCacheMap) {
        Map<String, FieldInfo> fieldInfoMap = new LinkedHashMap<>();
        //类(父类) 所有的公有函数
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            //不要static
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            //不要返回值是void
            if (method.getReturnType().equals(Void.TYPE)) {
                continue;
            }
            //不要参数
            if (method.getParameterTypes().length != 0) {
                continue;
            }
            // 不要getClass
            if (methodName.equals("getClass")) {
                continue;
            }
            String propertyName;
            // getA
            if (methodName.startsWith("get")) {
                //必须4个或者4个字符以上的函数名
                if (methodName.length() < 4) {
                    continue;
                }
                //get后的第一个字母
                char c3 = methodName.charAt(3);
                // A-> age
                propertyName = Character.toLowerCase(c3) + methodName.substring(4);
                //可能拿到null
                Field field = fieldCacheMap.get(propertyName);
                FieldInfo fieldInfo = new FieldInfo(propertyName, method, field);
                fieldInfoMap.put(propertyName, fieldInfo);
            }
            if (methodName.startsWith("is")) {
                if (methodName.length() < 3) {
                    continue;
                }
                //不是boolean或者Boolean
                if (method.getReturnType() != Boolean.TYPE
                        && method.getReturnType() != Boolean.class) {
                    continue;
                }
                char c2 = methodName.charAt(2);
                propertyName = Character.toLowerCase(c2) + methodName.substring(3);
                //可能已经在get找到了
                if (fieldInfoMap.containsKey(propertyName)) {
                    continue;
                }
                Field field = fieldCacheMap.get(propertyName);
                FieldInfo fieldInfo = new FieldInfo(propertyName, method, field);
                fieldInfoMap.put(propertyName, fieldInfo);
            }
        }
        //所有的公有成员
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            //静态的不要
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String propertyName = field.getName();
            //把公有成员也加入json
            if (!fieldInfoMap.containsKey(propertyName)) {
                FieldInfo fieldInfo = new FieldInfo(propertyName, null, field);
                fieldInfoMap.put(propertyName, fieldInfo);
            }
        }
        //
        List<FieldSerializer> fieldInfos = new ArrayList<>();
        //fieldinfo加入到list集合中
        for (FieldInfo fieldInfo : fieldInfoMap.values()) {
            fieldInfos.add(new FieldSerializer(fieldInfo));
        }
        return fieldInfos;
    }

    /**
     * 反序列化 采集公有set函数与公有属性
     *
     * @param clazz
     * @param fieldCacheMap
     * @return
     */
    public static List<FieldInfo> computeSetters(Class clazz, Map<String, Field> fieldCacheMap) {
        Map<String, FieldInfo> fieldInfoMap = new LinkedHashMap<>();
        //类(父类) 所有的公有函数
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (!method.getReturnType().equals(Void.TYPE)) {
                continue;
            }
            if (method.getParameterTypes().length != 1) {
                continue;
            }
            String propertyName;
            if (methodName.startsWith("set")) {
                if (methodName.length() < 4) {
                    continue;
                }
                //set后的第一个字母
                char c3 = methodName.charAt(3);
                propertyName = Character.toLowerCase(c3) + methodName.substring(4);
                Field field = fieldCacheMap.get(propertyName);
                FieldInfo fieldInfo = new FieldInfo(propertyName, method, field, true);
                fieldInfoMap.put(propertyName, fieldInfo);
            }
        }

        //所有的public成员
        for (Field field : clazz.getFields()) {
            int modifiers = field.getModifiers();
            //静态和final的不要
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            String propertyName = field.getName();
            //把公有成员也加入
            if (!fieldInfoMap.containsKey(propertyName)) {
                FieldInfo fieldInfo = new FieldInfo(propertyName, null, field, true);
                fieldInfoMap.put(propertyName, fieldInfo);
            }
        }


        List<FieldInfo> fieldInfos = new ArrayList<>();
        //fieldinfo加入到list集合中
        for (FieldInfo fieldInfo : fieldInfoMap.values()) {
            fieldInfos.add(fieldInfo);
        }
        return fieldInfos;
    }

    /**
     * 获得
     *
     * @param fieldType
     * @return
     */
    public static Type getItemType(Type fieldType) {

        if (fieldType instanceof ParameterizedType) {
            Type actualTypeArgument = ((ParameterizedType) fieldType)
                    .getActualTypeArguments()[0];
            // 泛型 获得上限 <? extends String> 则获得String
            if (actualTypeArgument instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) actualTypeArgument;
                Type[] upperBounds = wildcardType.getUpperBounds();
                if (upperBounds.length == 1) {
                    actualTypeArgument = upperBounds[0];
                }
            }
            return actualTypeArgument;
        }
        return Object.class;
    }
}
