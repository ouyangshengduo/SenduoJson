# **开篇废话** #

近期利用业余时间，跟着大神把FastJson的框架学习了一下，在这里，记录一下这次学习的心得。
FastJson是一个Json处理工具包，包括“序列化”和“反序列化”两部分。这次学习 ，大概有以下这些知识点：

      1.反射的使用
      2.设计模式之责任链的实践
      3.泛型以及泛型与反射的结合实践
      4.序列化以及反序列化的实现

----------


# **技术详情** #

## **1. 反射** ##
JAVA反射机制提供了运行时动态编程的可能。
当类处于运行状态时，我们可以做如下这些事：

      1.获得这个类的所有属性，方法以及注解等信息
      2.可以调用这个类的任意属性与方法
  
下面举一个例子来说明。

      public class Book {
	    private int page;
	    private String name;
	    public List<String> authors;
		public List<? super Integer> wildcardTypeDatas ;

	    public Book(){
	
	    }
	
	    public int getPage() {
	        return page;
	    }
	
	    public void setPage(int page) {
	        this.page = page;
	    }
	
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	        this.name = name;
	    }
	
	    public List<String> getAuthors() {
	        return authors;
	    }
	
	    public void setAuthors(List<String> authors) {
	        this.authors = authors;
	    }
	}

有一个父类如上，子类如下：

	public class ComputerBook extends Book {

	    private int codeLine;
	
	    public ComputerBook(){

	    }
	
	    public int getCodeLine() {
	        return codeLine;
	    }
	
	    public void setCodeLine(int codeLine) {
	        this.codeLine = codeLine;
	    }
	}


执行下面这段反射的代码：
	
	public void test(){
        Class<ComputerBook> bookClass = ComputerBook.class;
        //获得类以及父类中所有声明为public的属性
        System.out.println("所有public属性:");
        for (Field field : bookClass.getFields()) {
            System.out.println(field.getName());
        }
        //获得类(不包括父类)中所有的属性
        System.out.println("所有属性:");
        for (Field field : bookClass.getDeclaredFields()) {
            System.out.println(field.getName());
        }
        //获得类以及父类中所有声明为public的函数
        System.out.println("所有public函数:");
        for (Method method : bookClass.getMethods()) {
            String methodName = method.getName();
            System.out.println(methodName);
        }

        //获得类(不包括父类)中所有的函数
        System.out.println("所有函数:");
        for (Method method : bookClass.getDeclaredMethods()) {
            String methodName = method.getName();
            System.out.println(methodName);
        }
    }

获得的结果为：

	所有public属性:
	authors
	wildcardTypeDatas
	所有属性:
	codeLine
	所有public函数:
	getCodeLine
	setCodeLine
	getName
	setName
	getPage
	setPage
	getAuthors
	setAuthors
	wait
	wait
	wait
	equals
	toString
	hashCode
	getClass
	notify
	notifyAll
	所有函数:
	getCodeLine
	setCodeLine

从以上的这个例子，能得知：


	1.getDeclaredXX :会获得当前Class中的所有内容

	2.getXX: 获得当前类以及父类的内容，但是不包括非public


类中的属性对应反射中的Field，而函数则为Method。但是获取构造方法，则需要通过Constructor:

	Constructor<?>[] constructors = bookClass.getConstructors();

操作属性、调用函数的方法则需要编写:

		try {
            ComputerBook cb = bookClass.newInstance();//相当于实例化一个对象
            Method method = bookClass.getDeclaredMethods()[0];
            //在obj对象上调用函数
            if(method.getParameters().length == 1) {
                method.invoke(cb, 1);
            }else{
                Object object = method.invoke(cb, null);
                System.out.println((Integer) object);
            }

            Field field = bookClass.getDeclaredFields()[0];
			//对于非public的field或者method,需要先设置这个参数为true
			field.setAccessible(true);
            //获得obj中的属性
            Object value1 = field.get(cb);
            System.out.println(value1);
            //设置obj中的属性
            field.set(cb,2);
            Object value2 = field.get(cb);
            System.out.println(value2);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


	
## **2. 带泛型的反射** ##

首先，应该知道是，带泛型的在运行阶段是会被擦除的，也就是List<String>  与 List<Integer>在运行阶段，都是List.class,因此，如果，我们需要获得参数类型的具体泛型类型，就需要利用Type接口了

如果我们要获取一个List<String> authors集合的泛型类型，我们应该按下面这种方式来获取：

		try {
            Field list = Book.class.getField("authors");

            //判断list是否为List集合类型
            if(List.class.isAssignableFrom(list.getType())){

                Type genericType = list.getGenericType();//获取属性声明时的类型

                //如果是属于参数化类型例如List<String>
                if(genericType instanceof ParameterizedType){
                    //获取泛型类型，这里的0代表第一个参数：String
                    Type type = ((ParameterizedType)genericType).getActualTypeArguments()[0];
                    System.out.println("获得泛型类型:" + type);
                }

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


另外，泛型中，还有可能存在通配符，例如这种形式的: List<? extends Book> list (存在上边界)或者 List<? super Integer> list（存在下边界），这个时候，我们就需要通过判断是否属于通配符参数类型来处理，例如： List<? super Integer> wildcardTypeDatas,那么，修改上面那段代码如下：

		try {
            Field list = Book.class.getField("wildcardTypeDatas");

            //判断list是否为List集合类型
            if(List.class.isAssignableFrom(list.getType())){

                Type genericType = list.getGenericType();//获取属性声明时的类型

                //如果是属于参数化类型例如List<String>
                if(genericType instanceof ParameterizedType){
                    //获取泛型类型，这里的0代表第一个参数：String
                    Type type = ((ParameterizedType)genericType).getActualTypeArguments()[0];
                    //判断是否使用了通配符
                    if(type instanceof WildcardType){

                        WildcardType wildcardType = (WildcardType) type;
                        Type[] upperBounds = wildcardType.getUpperBounds();
                        Type[] lowerBounds = wildcardType.getLowerBounds();

                        if(upperBounds.length == 1){
                            Type actualTypeArgument = upperBounds[0];
                            System.out.println("获得泛型上边界类型:" + actualTypeArgument);
                        }

                        if(lowerBounds.length == 1){
                            Type actualTypeArgument = lowerBounds[0];
                            System.out.println("获得泛型下边界类型:" + actualTypeArgument);
                        }
                    }else {
                        System.out.println("获得泛型类型:" + type);
                    }
                }

            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

## **3. 序列化的实现** ##

对于反射有了一定了解后，就可以开始接下来的序列化的工作了，序列化的工作，主要有两步

		第一步：获取需要序列化的（"key": "value"）的成员
		第二步：根据获得的信息，拼接json字符串


为实现第一步，需要使用到上面介绍的反射技术，先获得

第一步，需要序列化的数据收集（使用递归）

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

然后根据类中以及父类中所有的属性，来获取需要序列化的成员，

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


第二步，就是拼接json字符串了，按照json的语法，按照(key,value)的形式拼接起来：

	public void serializer(SerializerContext context,JsonConfig config, StringBuilder out, Object object) {

        //{"age":100,"name":"testname","test":1,"list":["1","2"]}
        out.append("{");
        boolean lastEmpty = false;
        for(FieldSerializer fieldSerializer : fieldSerializers){
            //"name":"testname" 、 "age":100
            // 如果遇到属性没有值 (null) 则返回 ""
            String serializer = fieldSerializer.serializer(context != null ?context : new SerializerContext(),config,object);

            if(lastEmpty && !serializer.isEmpty()){
                out.append(",");
            }

            if(!lastEmpty){
                lastEmpty = !serializer.isEmpty();
            }
            out.append(serializer);
        }
        out.append("}");
    }

由于，类中的属性可能是基本数据类型，String,List,Map,或者Javabean等类型，所以，就需要针对不同的类型进行，这里就使用了责任链模式，根据不同类型，交由不同的序列化器进行序列化：

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

通过调用JsonConfig中的getSerializer来获取相应的序列化器：

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



## **4. 反序列化的实现** ##

反序列化的设计与序列化差不多，也是先收集反序列化的成员，然后通过反射设置实例化对象的值

第一步，收集反序列化成员，和序列化一样


第二步，通过反射实例化对象，并设置对应的值
	
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




以下是此项目的简书地址：

[SenduoJson](https://www.jianshu.com/p/f4a828bec8f0)


