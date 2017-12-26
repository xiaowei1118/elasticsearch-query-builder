package org.coryphaei.query.builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.parser.JSONValueParser;
import org.elasticsearch.search.highlight.HighlightBuilder;

/**
 * Created by twist on 2017/4/20.
 */
public class ZWHighlightBuilder {

    public static HighlightBuilder getHighlightBuilder(JSONObject highlight) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        JSONArray highlightArr = highlight.getJSONArray("fields");
        for (Object item : highlightArr) {
            if (item != null) {
                highlightBuilder.field(((JSONObject) item).getString("field"),
                        ((JSONObject) item).getInteger("fragment_size") != null ? ((JSONObject) item).getInteger("fragment_size") : 100,
                        ((JSONObject) item).getInteger("number_of_fragment") != null ? ((JSONObject) item).getInteger("number_of_fragment") : 5);
            }
        }

        highlightBuilder.preTags(JSONValueParser.convertJSONArr2StringArr(highlight.getJSONArray("pre_tags")));
        highlightBuilder.postTags(JSONValueParser.convertJSONArr2StringArr((highlight.getJSONArray("post_tags"))));
        highlightBuilder.tagsSchema(highlight.getString("tags_schema"));
        highlightBuilder.order(highlight.getString("order"));

        return highlightBuilder;
    }

}
