package com.jkgeekjack.usemorerecyclerview;

/**
 * Created by Administrator on 2016/6/26.
 */
public class UserBean {
    String name;
    int age;

    public UserBean(String name,int age){
        this.age=age;
        this.name=name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
