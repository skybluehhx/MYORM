package support;

/**
 * Created by zoujianglin
 * 2018/8/24 0024.
 */

import sql.SqlAndField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提供对字符串的基本操作，如将首字母变小写等
 */
public final class StringsUtil {

    /**
     * 将字符串首字母变为小写，在操作前，会将字符
     * 串两端的空格给去掉
     */
    public static String toLowerCaseFirstOne(String s) {
        s = s.trim();
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    /**
     * 将字符串首字母变为大写，在操作前，会将字符
     * 串两端的空格给去掉
     */
    public static String toUpperCaseFirstOne(String s) {
        s = s.trim();
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    /**
     * 对字符串进行基本的操作形如,提取其中的属性值，并使用问号代替
     * <p>
     * String sql2 = "select * from TestPerson where age < #{user.age} and age > #{user.age}";
     * String sql3 = "update TestPerson set #{age}=#{user.age} where #{id} =#{user.id}"
     *
     * @param url 配置的字符串
     * @param s   匹配的目标串
     * @param i   第几次出现的位置
     * @return
     */
    public static int HandPreSql(String url, String s, int i) {
        Matcher slashMatcher = Pattern.compile(s).matcher(url);
        int mIdx = 0;
        while (slashMatcher.find()) {
            mIdx++;
            //当"/"符号第i次出现的位置
            if (mIdx == i) {
                break;
            }
        }

        return slashMatcher.end();


    }

    public static SqlAndField<String, String> fetchFieldNameAndReplaceFromSql(String sql, String modelName) {
        String pattern = "\\#\\{" + modelName + "\\.";
        StringBuilder stringBuilder = new StringBuilder();
        int endPosition = HandPreSql(sql, pattern, 1);
        int i = endPosition;
        for (; i < sql.length(); i++) {
            if (sql.charAt(i) == '}') {
                break;
            }
        }
        String fieldName = sql.substring(endPosition, i);
        String replace = pattern + fieldName + "\\}";
        sql.replaceFirst(replace, "?");

        SqlAndField<String, String> sqlAndField = new SqlAndField(sql.replaceFirst(replace, "?"), fieldName);

        return sqlAndField;

    }

    public static void main(String[] args) {

        String preSql = "select * from TestPerson where age < #{user.age} and age > #{user.age}";
        SqlAndField<String, String> sqlAndField = fetchFieldNameAndReplaceFromSql(preSql, "user");
        System.out.println(sqlAndField.getSql());
        System.out.println(sqlAndField.getFiledName());
        //System.out.print(preSql.substring(44, 47));
    }


}
