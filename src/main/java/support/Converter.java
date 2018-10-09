package support;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */

/**
 * 如果mapper绑定中的字段为复杂类型（如list,map等）需要
 * 自己实现转换器，进行转换，否则将不能正常工作
 * <p>
 * 在使用转换器时请遵守下面规则 调用ConverterFiled后返回的类型
 * 确保与字段类型一致否则将会导致错误
 * 转换器转换后的得到的字符串请不要带有 #{} 串，否则将出现不可预料的错误
 * 在转换器转化为字符串的过程中，最有不要带有正则匹配式中的符号，
 * 如果有请使用转意符，否则将出现错误,
 * 使用转换器转换时，确保转换后不会出现数据库运算符号
 */

public abstract class Converter<K,V> {

    //转换到数据库进行存储
    //一般对于list,map或者对象等使用
    public abstract  V ConverterColumn(K fieldObject);

    //用于转换为对应model的字段
    public abstract  K ConverterFiled(V ColumnObject);

}
