package test;

import anno.ORMConverter;
import support.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */

public class MyConverter extends Converter<List<String>> {


    public static void main(String[] args) {
        MyConverter myConverter = new MyConverter();
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("4");

        System.out.println(myConverter.ConverterColumn(list));
    }

    public String ConverterColumn(List<String> fieldObject) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : fieldObject) {
            stringBuilder.append(s).append("\\$");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 2).toString();
    }

    public List<String> ConverterFiled(Object ColumnObject) {
        String test = ColumnObject.toString();
        List<String> list = new ArrayList<String>();
        String[] strings = test.split("\\$");
        for (String string : strings) {
            list.add(string);
        }

        return list;
    }


}
