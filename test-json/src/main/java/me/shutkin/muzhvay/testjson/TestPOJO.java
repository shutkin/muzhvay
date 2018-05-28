package me.shutkin.muzhvay.testjson;

import java.util.ArrayList;

public class TestPOJO {
  public static class TestObject {
    public Long id;
    public String title;
    public ArrayList<TestChildObject> children;
    public TestObject next;

    @Override
    public String toString() {
      return "TestObject{" +
              "id=" + id +
              ", title='" + title + '\'' +
              ", children=" + children +
              ", next=" + next +
              '}';
    }
  }

  public static class TestChildObject {
    public Integer index;
    public String name;
    public ArrayList<Integer> data;
    public Double factor;
    public Boolean isAlive;

    @Override
    public String toString() {
      return "TestChildObject{" +
              "index=" + index +
              ", name='" + name + '\'' +
              ", data=" + data +
              ", factor=" + factor +
              ", isAlive=" + isAlive +
              '}';
    }
  }
}
