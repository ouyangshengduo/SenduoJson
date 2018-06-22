package senduo.com.senduojson;

import java.util.List;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/21
 * * 文件描述：
 * * 修改历史：2018/6/21 14:21*************************************
 **/
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

