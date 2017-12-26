package org.coryphaei.query.parser;

import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.builder.ZWQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * Created by twist on 2017-04-16.
 */
public class ZWMatchAllQueryParser {

    public static QueryBuilder matchAllQuery(JSONObject item){
        return ZWQueryBuilder.matchAllQueryBuilder(item.getFloat("boost"));
    }
}
