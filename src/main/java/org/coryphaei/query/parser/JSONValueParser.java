package org.coryphaei.query.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.DocumentField;
import org.apache.http.util.Asserts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by twist on 2017-06-16.
 */
public class JSONValueParser {

    /**
     * 获取数据值,以${a.value}的形式
     *
     * @param value
     * @param data
     * @return
     */
    public static String getValue(String value, JSONObject data) {
        String result;
        if (value != null && value.matches("\\$\\{(.*?)\\}")) {
            String[] properties = value.replaceAll("\\$\\{(.*?)\\}", "$1").split("\\.");

            JSONObject item = data;
            for (int i = 0; i < properties.length - 1; i++) {
                item = item.getJSONObject(properties[i]);
                if (item == null) {
                    break;
                }
            }

            if (item != null) {
                result = item.getString(properties[properties.length - 1]);
            } else {
                result = null;
            }
        } else {
            result = value;
        }

        return result;
    }

    /**
     * 解析fields,支持,boost跟在^后面
     *
     * @param fields
     * @return
     */
    public static List<DocumentField> getDocumentField(String fields) {
        List<DocumentField> documentFieldList = new ArrayList<>();
        String[] fieldsArr = fields.split(",");
        for (String field : fieldsArr) {
            String[] split = field.split("\\^");
            DocumentField documentField = new DocumentField(split[0], split.length > 1 ? Float.valueOf(split[1]) : 1.0f);
            documentFieldList.add(documentField);
        }

        return documentFieldList;
    }

    /**
     * 解析range,类似[2,3]的形式
     *
     * @param value
     * @return
     */
    public static Object[] getRangeValue(String value) {
        Asserts.notNull(value, "range_value");
        String item = value.replaceAll("\\[(.*?)\\]", "$1").trim();
        String[] split = item.split(",");
        Object[] result = new Object[2];
        if (item.contains(",") && split.length >= 1) {
            if (!"".equals(split[0]) && !"\"\"".equals(split[0])) {
                result[0] = split[0].trim();
            }

            if (split.length > 1 && !"".equals(split[1]) && !"\"\"".equals(split[1])) {
                result[1] = split[1].trim();
            }
        }

        return result;
    }

    /**
     * 向JSON指定位置放入某值
     *
     * @param key   键值
     * @param data  数据
     * @param value 要放入的值
     */
    public static void putValue(String key, JSONObject data, Object value) {
        String[] properties = key.split("\\.");
        JSONObject item = data;
        for (int i = 0; i < properties.length - 1; i++) {
            if (item.getJSONObject(properties[i]) == null) {
                item.put(properties[i], new JSONObject());
            }

            item = item.getJSONObject(properties[i]);
        }

        if (item != null) {
            item.put(properties[properties.length - 1], value);
        }
    }

    public static String[] convertJSONArr2StringArr(JSONArray arr) {
        String[] strArr = new String[]{};
        if (arr != null) {
            strArr = arr.toArray(strArr);
        }

        return strArr;
    }
}
