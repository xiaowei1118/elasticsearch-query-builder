package org.coryphaei.query.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * Created by twist on 2017-04-16.
 */
public class ZWBoolQueryParser {

    public static QueryBuilder queryBuilder(JSONObject item, JSONObject data) throws Exception {

        QueryBuilder queryBuilder;
        switch (item.getString("terms_method")) {
            case "term":
                queryBuilder = ZWTermsLevelQueryParser.termQueryBuilder(item, data);
                break;
            case "terms":
                queryBuilder = ZWTermsLevelQueryParser.termsQueryBuilder(item, data);
                break;
            case "range":
                queryBuilder = ZWTermsLevelQueryParser.rangeQueryBuilder(item, data);
                break;
            case "exists":
                queryBuilder = ZWTermsLevelQueryParser.existsQueryBuilder(item, data);
                break;
            case "match":
                queryBuilder = ZWTextQueryParser.matchQueryBuilder(item, data);
                break;
            case "multi_match":
                queryBuilder = ZWTextQueryParser.multiMatchQueryBuilder(item, data);
                break;
            default:
                throw new Exception("terms_method not support");
        }

        return queryBuilder;
    }

    public static BoolQueryBuilder boolQueryBuilder(JSONArray arr, JSONObject data) throws Exception {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject item = arr.getJSONObject(i);
            JSONArray itemArr = item.getJSONArray("items");
            switch (item.getString("bool_type")) {
                case "must":
                    for (int j = 0; j < itemArr.size(); j++) {
                        QueryBuilder queryBuilder = queryBuilder(itemArr.getJSONObject(j), data);
                        if (queryBuilder != null) {
                            boolQueryBuilder.must(queryBuilder);
                        }
                    }
                    break;
                case "must_not":
                    for (int j = 0; j < itemArr.size(); j++) {
                        QueryBuilder queryBuilder = queryBuilder(itemArr.getJSONObject(j), data);
                        if (queryBuilder != null) {
                            boolQueryBuilder.mustNot(queryBuilder);
                        }
                    }
                    break;
                case "should":
                    for (int j = 0; j < itemArr.size(); j++) {
                        QueryBuilder queryBuilder = queryBuilder(itemArr.getJSONObject(j), data);
                        if (queryBuilder != null) {
                            boolQueryBuilder.should(queryBuilder);
                        }
                    }
                    break;
                case "filter":
                    for (int j = 0; j < itemArr.size(); j++) {
                        QueryBuilder queryBuilder = queryBuilder(itemArr.getJSONObject(j), data);
                        if (queryBuilder != null) {
                            boolQueryBuilder.filter(queryBuilder);
                        }
                    }
                    break;
                default:
                    throw new Exception("bool_type not support");
            }
        }

        return boolQueryBuilder;
    }
}
