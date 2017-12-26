package org.coryphaei.query.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.builder.ZWQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * Created by twist on 2017-04-13.
 */
public class ZWTermsLevelQueryParser {
    public static QueryBuilder termQueryBuilder(JSONObject item, JSONObject data) {
        String query = JSONValueParser.getValue(item.getString("value"), data);
        if (query != null) {
            return ZWQueryBuilder.termQueryBuilder(query, item.getString("key"), item.getFloat("boost"));
        }

        return null;
    }

    public static QueryBuilder termsQueryBuilder(JSONObject item, JSONObject data) {
        String value = JSONValueParser.getValue(item.getString("value"), data);
        if (value != null) {
            Object[] values = JSON.parseArray(value).toArray();
            return ZWQueryBuilder.termsQueryBuilder(values, item.getString("key"), item.getFloat("boost"));
        }

        return null;
    }

    public static QueryBuilder rangeQueryBuilder(JSONObject item, JSONObject data) {
        String value = JSONValueParser.getValue(item.getString("range"), data);
        if (value != null) {
            Object[] values = JSONValueParser.getRangeValue(value);
            return ZWQueryBuilder.rangeQueryBuilder(item.getString("key"), values[0], values[1],
                    item.getString("format"), item.getFloat("boost"), item.getBoolean("include_lower"), item.getBoolean("include_upper"));
        }

        return null;
    }

    public static QueryBuilder existsQueryBuilder(JSONObject item, JSONObject data) {
        String value = JSONValueParser.getValue(item.getString("value"), data);
        if ((item.getString("value") == null && item.getString("must") == null) || item.getString("must").equals(value)  ) {
            return ZWQueryBuilder.existsQueryBuilder(item.getString("key"));
        }

        return null;
    }

//    public static QueryBuilder idsQueryBuilder(JSONObject item,JSONObject data){
//        ZWQueryBuilder.idsQueryBuilder()
//    }
}
