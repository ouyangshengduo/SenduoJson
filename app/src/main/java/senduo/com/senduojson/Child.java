package senduo.com.senduojson;

import java.util.ArrayList;
import java.util.List;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/17
 * * 文件描述：
 * * 修改历史：2018/6/17 20:36*************************************
 **/
class Parent {
    String name;

    public Parent() {
    }

    public Parent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}

public class Child extends Parent {
    private int age;
    public List<String> list;

    public List<Child> childs;

    public Child() {

    }

    /**
     * 指定FastJson反序列化构造函数
     * @param name
     * @param age
     */
    public Child(String name, int age) {
        super(name);
        this.age = age;
        list = new ArrayList<>();
        list.add("1");
        list.add("2");
    }

    public int getTest() {
        return 1;
    }

    //非公有属性需要有
    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Child{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", list=" + list +
                ", childs=" + childs +
                '}';
    }
}


