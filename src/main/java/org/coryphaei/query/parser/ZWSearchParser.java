package org.coryphaei.query.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.builder.ZWHighlightBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by twist on 2017-04-13.
 */
public class ZWSearchParser {
    public static SearchSourceBuilder searchSourceBuilder(JSONObject item, JSONObject data) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //query
        if (item.get("query_type") != null) {
            switch (item.getString("query_type")) {
                case "bool_query":
                    searchSourceBuilder.query(ZWBoolQueryParser.boolQueryBuilder(item.getJSONArray("bool_query"), data));
                    break;
                case "terms_level_query":
                    searchSourceBuilder.query(termsQueryBuilder(item.getJSONObject("terms_level_query"), data));
                    break;
                case "text_query":
                    searchSourceBuilder.query(textQueryBuilder(item.getJSONObject("text_query"), data));
                    break;
                default:
                    throw new Exception("query_type not support");
            }
        }

        //from
        if (item.getString("from") != null) {
            String from = JSONValueParser.getValue(item.getString("from"), data);
            if (from != null) {
                searchSourceBuilder.from(Integer.valueOf(from));
            }
        }

        //size
        if (item.getString("size") != null) {
            String size = JSONValueParser.getValue(item.getString("size"), data);
            if (size != null) {
                searchSourceBuilder.size(Integer.valueOf(size));
            }
        }

        searchSourceBuilder.fetchSource(JSONValueParser.convertJSONArr2StringArr(item.getJSONArray("include_source")),
                JSONValueParser.convertJSONArr2StringArr(item.getJSONArray("exclude_source")));

        //sort
        if (item.get("sort") != null) {
            JSONArray sortArray = item.getJSONArray("sort");
            for (int i = 0; i < sortArray.size(); i++) {
                Object sort = sortArray.get(i);
                if (sort instanceof String) {
                    if (sort.equals("_score")) {    //_score sort
                        searchSourceBuilder.sort(ZWOrderParser.scoreSortBuilder());
                    }
                } else {
                    searchSourceBuilder.sort(ZWOrderParser.fieldSortBuilder((JSONObject) sort, data));
                }
            }
        }

        //aggregations
        if (item.get("aggregations") != null) {
            JSONArray aggregationsArr = item.getJSONArray("aggregations");
            for (int i = 0; i < aggregationsArr.size(); i++) {
                JSONObject aggsItem = aggregationsArr.getJSONObject(i);
                searchSourceBuilder.aggregation(aggregationBuilder(aggsItem, data));
            }
        }

        //post_filter
//        if (item.get("post_filter") != null) {
//            JSONObject postFilterObj = item.getJSONObject("post_filter");
//            searchSourceBuilder.postFilter()
//        }

        //high_light
        if (item.get("highlight") != null) {
            searchSourceBuilder.highlight(ZWHighlightBuilder.getHighlightBuilder(item.getJSONObject("highlight")));
        }

        return searchSourceBuilder;
    }

    /**
     * get aggregations
     *
     * @param data
     * @param aggregationsArr
     * @return
     * @throws Exception
     */
    public static List<AbstractAggregationBuilder> addAggregations(JSONObject data, JSONArray aggregationsArr) throws Exception {
        List<AbstractAggregationBuilder> aggregationBuilderList = new ArrayList<>();
        for (int i = 0; i < aggregationsArr.size(); i++) {
            JSONObject aggsItem = aggregationsArr.getJSONObject(i);
            aggregationBuilderList.add(aggregationBuilder(aggsItem, data));
        }

        return aggregationBuilderList;
    }

    private static AbstractAggregationBuilder aggregationBuilder(JSONObject item, JSONObject data) throws Exception {
        AbstractAggregationBuilder aggregationBuilder;
        switch (item.getString("aggregation_type")) {
            case "avg":
                aggregationBuilder = ZWAggregationParser.avgBuilder(item);
                break;
            case "terms":
                TermsBuilder termsBuilder = ZWAggregationParser.termsAggregation(item, data);
                if (item.getJSONArray("sub_aggregations") != null) {
                    JSONArray subArr = item.getJSONArray("sub_aggregations");
                    for (int i = 0; i < subArr.size(); i++) {
                        JSONObject aggsItem = subArr.getJSONObject(i);
                        termsBuilder.subAggregation(aggregationBuilder(aggsItem, data));
                    }
                }

                aggregationBuilder = termsBuilder;
                break;
            case "cardinality":
                aggregationBuilder = ZWAggregationParser.cardinalityBuilder(item);
                break;
            case "extended_stats":
                aggregationBuilder = ZWAggregationParser.extendedStatsBuilder(item, data);
                break;
            case "max":
                aggregationBuilder = ZWAggregationParser.maxBuilder(item, data);
                break;
            case "min":
                aggregationBuilder = ZWAggregationParser.minBuilder(item, data);
                break;
            case "range":
                RangeBuilder rangeBuilder = ZWAggregationParser.rangeBuilder(item, data);
                if (item.getJSONArray("sub_aggregations") != null) {
                    JSONArray subArr = item.getJSONArray("sub_aggregations");
                    for (int i = 0; i < subArr.size(); i++) {
                        JSONObject aggsItem = subArr.getJSONObject(i);
                        rangeBuilder.subAggregation(aggregationBuilder(aggsItem, data));
                    }
                }

                aggregationBuilder = rangeBuilder;
                break;
            case "missing":
                MissingBuilder missingBuilder = ZWAggregationParser.missingBuilder(item);
                if (item.getJSONArray("sub_aggregations") != null) {
                    JSONArray subArr = item.getJSONArray("sub_aggregations");
                    for (int i = 0; i < subArr.size(); i++) {
                        JSONObject aggsItem = subArr.getJSONObject(i);
                        missingBuilder.subAggregation(aggregationBuilder(aggsItem, data));
                    }
                }

                aggregationBuilder = missingBuilder;
                break;
            case "histogram":
                aggregationBuilder = ZWAggregationParser.histogramBuilder(item);
                break;
            case "date_histogram":
                aggregationBuilder = ZWAggregationParser.dateHistogramBuilder(item);
                break;
            default:
                throw new Exception("aggregation_type not support yet");
        }

        return aggregationBuilder;
    }

    private static QueryBuilder textQueryBuilder(JSONObject item, JSONObject data) throws Exception {
        QueryBuilder queryBuilder;
        String key = item.getString("text_query_type");
        JSONObject queryJSON = item.getJSONObject(key);
        switch (key) {
            case "match_query":
                queryBuilder = ZWTextQueryParser.matchQueryBuilder(queryJSON, data);
                break;
            case "multi_match_query":
                queryBuilder = ZWTextQueryParser.multiMatchQueryBuilder(queryJSON, data);
                break;
            case "query_string":
                queryBuilder = ZWTextQueryParser.queryStringBuilder(queryJSON, data);
                break;
            case "simple_query_string":
                queryBuilder = ZWTextQueryParser.simpleQueryStringBuilder(queryJSON, data);
                break;
            default:
                throw new Exception("text_query_type not support");
        }

        return queryBuilder;

    }

    private static QueryBuilder termsQueryBuilder(JSONObject item, JSONObject data) throws Exception {
        QueryBuilder queryBuilder;
        String key = item.getString("terms_level_type");
        JSONObject queryJSON = item.getJSONObject(key);
        switch (key) {
            case "term_query":
                queryBuilder = ZWTermsLevelQueryParser.termQueryBuilder(queryJSON, data);
                break;
            case "terms_query":
                queryBuilder = ZWTermsLevelQueryParser.termsQueryBuilder(queryJSON, data);
                break;
            case "range_query":
                queryBuilder = ZWTermsLevelQueryParser.rangeQueryBuilder(queryJSON, data);
                break;
            case "exists_query":
                queryBuilder = ZWTermsLevelQueryParser.existsQueryBuilder(queryJSON, data);
                break;
            case "prefix_query":
            case "wildcard_query":
            case "regexp_query":
            case "ids_query":
            default:
                throw new Exception("terms_level_type not support");
        }

        return queryBuilder;
    }
}
