package org.coryphaei.query.parser;

import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.builder.ZWOrderBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;

/**
 * Created by twist on 2017-05-27.
 */
public class ZWOrderParser {

    public static FieldSortBuilder fieldSortBuilder(JSONObject config, JSONObject data) {
        return ZWOrderBuilder.fieldSortBuilder(JSONValueParser.getValue(config.getString("field"), data),
                JSONValueParser.getValue(config.getString("order"), data),
                JSONValueParser.getValue(config.getString("missing"), data),
                JSONValueParser.getValue(config.getString("mode"), data),
                JSONValueParser.getValue(config.getString("unmapped_type"), data));
    }

    public static ScoreSortBuilder scoreSortBuilder() {
        return ZWOrderBuilder.scoreSortBuilder();
    }
}
