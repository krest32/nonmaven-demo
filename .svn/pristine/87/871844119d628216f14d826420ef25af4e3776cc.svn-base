package com.fico.cbs.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public StringUtils() {
    }

    public static boolean isNull(String s) {
        return s == null || "".equals(s.trim());
    }

    public static String getTimeStr(String fmt) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    public static boolean isNumberStr(String s) {
        for(int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) > '9' || s.charAt(i) < '0') {
                return false;
            }
        }

        return true;
    }

    public static boolean isDateStr(String s) {
        try {
            String y = s.substring(0, 4);
            String m = s.substring(5, 7);
            String d = s.substring(8, 10);
            return isNumberStr(y) && isNumberStr(m) && isNumberStr(d);
        } catch (Exception var4) {
            return false;
        }
    }

    public static String replaceAll(String s, String sub, String newS) {
        String res = "";

        for(String ts = ""; s.indexOf(sub) >= 0; res = res + ts + newS) {
            ts = s.substring(0, s.indexOf(sub));
            s = s.substring(s.indexOf(sub) + sub.length());
        }

        res = res + s;
        return res;
    }

    public static String[] split2Array(String s, String sep) {
        if (isNull(s)) {
            return null;
        } else {
            List<String> list = split2List(s, sep);
            return (String[])list.toArray(new String[list.size()]);
        }
    }

    public static int[] splitNumbers(String s, String sep) {
        if (isNull(s)) {
            return null;
        } else {
            List<String> list = new ArrayList();
            String ts = "";

            while(s.indexOf(sep) >= 0) {
                ts = s.substring(0, s.indexOf(sep));
                s = s.substring(s.indexOf(sep) + sep.length());
                list.add(ts);
            }

            list.add(s);
            int[] ret = new int[list.size()];

            for(int i = 0; i < list.size(); ++i) {
                ret[i] = Integer.parseInt((String)list.get(i));
            }

            return ret;
        }
    }

    public static List<String> split2List(String s, String sep) {
        if (isNull(s)) {
            return null;
        } else {
            List<String> list = new ArrayList();
            String ts = "";

            while(s.indexOf(sep) >= 0) {
                ts = s.substring(0, s.indexOf(sep));
                s = s.substring(s.indexOf(sep) + sep.length());
                list.add(ts);
            }

            list.add(s);
            return list;
        }
    }

    public static boolean allSeperator(String s, char sep) {
        for(int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != sep) {
                return false;
            }
        }

        return true;
    }

    public static int getFieldCount(String s, String sep) {
        int count = 0;
        String ts = "";

        while(s.indexOf(sep) >= 0) {
            ts = s.substring(0, s.indexOf(sep));
            s = s.substring(s.indexOf(sep) + sep.length());
            if (!isNull(ts)) {
                ++count;
            }
        }

        return count + 1;
    }

    public static boolean isTrimEmpty(String astr) {
        if (astr != null && astr.length() != 0) {
            return isBlank(astr.trim());
        } else {
            return true;
        }
    }

    public static boolean isBlank(String astr) {
        return astr == null || astr.length() == 0;
    }

    public static String getAllTrimToSpacebar(String str) {
        str = replaceAll(str, "&nbsp;", " ");
        return str;
    }

    public static String getAllTrim(String str) {
        str = replaceAll(str, "&nbsp;", "").trim();
        return str;
    }

    public static String getStrByReplace(String content, String oldChar, String newChar) {
        while(content.indexOf(oldChar) >= 0) {
            content = content.replace(oldChar, newChar);
        }

        return content;
    }

    public static String getStrByRegex(String str, String regex, Pattern pattern, Matcher matcher) {
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(str);
        return matcher.find() ? matcher.group() : null;
    }
}
