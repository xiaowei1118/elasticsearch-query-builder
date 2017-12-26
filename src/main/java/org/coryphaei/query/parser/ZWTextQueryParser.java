package org.coryphaei.query.parser;

import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.builder.ZWQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * Created by twist on 2017-04-13.
 */
public class ZWTextQueryParser {
    public static QueryBuilder matchQueryBuilder(JSONObject item, JSONObject data) {
        String value = JSONValueParser.getValue(item.getString("value"), data);
        if (value != null) {
            return ZWQueryBuilder.matchQueryBuilder(value, item.getString("key"), item.getFloat("boost"), item.getString("zero_terms_query"));
        }

        return null;
    }

    public static QueryBuilder multiMatchQueryBuilder(JSONObject item, JSONObject data) {
        String value = JSONValueParser.getValue(item.getString("value"), data);
        if (value != null) {
            return ZWQueryBuilder.multiMatchQueryBuilder(value, JSONValueParser.getDocumentField(item.getString("fields")), item.getString("zero_terms_query"));
        }

        return null;
    }

    public static QueryBuilder queryStringBuilder(JSONObject item, JSONObject data) {
        String value = JSONValueParser.getValue(item.getString("value"), data);
        if (value != null) {
            return ZWQueryBuilder.queryStringQueryBuilder(value, JSONValueParser.getDocumentField(item.getString("fields")));
        }

        return null;
    }

    public static QueryBuilder simpleQueryStringBuilder(JSONObject item, JSONObject data) {
        String value = JSONValueParser.getValue(item.getString("value"), data);
        if (value != null) {
            return ZWQueryBuilder.simpleQueryStringBuilder(value, JSONValueParser.getDocumentField(item.getString("fields")));
        }

        return null;
    }
}
