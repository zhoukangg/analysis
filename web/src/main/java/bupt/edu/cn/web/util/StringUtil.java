package bupt.edu.cn.web.util;

import org.apache.calcite.util.Static;

import java.util.List;
import java.util.Map;

public class StringUtil {
    /**
     * 去掉指定字符串前面和后面指定的字符
     *
     * @param str
     * @param c
     * @return
     */
    public static String custom_trim(String str, char c) {
        char[] chars = str.toCharArray();
        int len = chars.length;
        int st = 0;
        while ((st < len) && (chars[st] == c)) {
            st++;
        }
        while ((st < len) && (chars[len - 1] == c)) {
            len--;
        }
        return (st > 0) || (len < chars.length) ? str.substring(st, len) : str;
    }

    /**
     * 去掉指定字符串前面指定的字符
     *
     * @param str
     * @param c
     * @return
     */
    public static String custom_ltrim(String str, char c) {
        char[] chars = str.toCharArray();
        int len = chars.length;
        int st = 0;
        while ((st < len) && (chars[st] == c)) {
            st++;
        }
        return (st > 0) ? str.substring(st, len) : str;
    }

    /**
     * 去掉指定字符串后面指定的字符
     *
     * @param str
     * @param c
     * @return
     */
    public static String custom_rtrim(String str, char c) {
        char[] chars = str.toCharArray();
        int len = chars.length;
        int st = 0;
        while ((st < len) && (chars[len - 1] == c)) {
            len--;
        }
        return (len < chars.length) ? str.substring(st, len) : str;
    }

    /**
     * 判断字符串是否为空
     *
     * @param string
     * @return
     */
    public static Boolean isEmpty(String string) {
        if (string == null || string.equals("") || string.equals(""))
            return true;
        return false;
    }

    public static String getcolname(List<Map> listJson) {
        String dateColName = "year";
        for (Object key : listJson.get(0).keySet()) {
            String keyString = key.toString();
            if (!keyString.contains("_"))
                dateColName = keyString;
        }
        return dateColName;
    }
}
