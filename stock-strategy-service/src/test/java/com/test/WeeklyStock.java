package com.test;

public class WeeklyStock implements Stock{

    private String name = null;

    private int age = 0;

    private int weeklyAge = 0;

    private String weeklyName = null;

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

    public int getWeeklyAge() {
        return weeklyAge;
    }

    public void setWeeklyAge(int weeklyAge) {
        this.weeklyAge = weeklyAge;
    }

    public String getWeeklyName() {
        return weeklyName;
    }

    public void setWeeklyName(String weeklyName) {
        this.weeklyName = weeklyName;
    }
}
