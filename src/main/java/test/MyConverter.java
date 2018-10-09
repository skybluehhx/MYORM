package test;

import anno.ORMConverter;
import support.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */

public class MyConverter extends Converter<List<Integer>,String> {


    public static void main(String[] args) {
        MyConverter myConverter = new MyConverter();
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(4);

        System.out.println(myConverter.ConverterColumn(list));
        System.out.println(myConverter.ConverterFiled("2$7"));
    }

    public String ConverterColumn(List<Integer> fieldObject) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer s : fieldObject) {
            stringBuilder.append(s).append("$");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1).toString();
    }

    public List<Integer> ConverterFiled(String ColumnObject) {
        String test = ColumnObject;
        List<Integer> list = new ArrayList<Integer>();
        String[] strings = test.split("\\$");
        for (String string : strings) {
            list.add(Integer.parseInt(string));
        }

        return list;
    }




}
