package org.coryphaei.query.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.Range;
import org.coryphaei.query.builder.ZWAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ipv4.IPv4RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.StatsBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by twist on 2017-06-20.
 */
public class ZWAggregationParser {

    public static TermsBuilder termsAggregation(JSONObject item, JSONObject data) {
        Boolean ifAsc;
        if (item.getString("sort") != null && "desc".equals(item.getString("sort"))) {
            ifAsc = false;
        } else {
            ifAsc = true;
        }

        String sizeStr = JSONValueParser.getValue(item.getString("size"), data);
        Integer size = null;
        if (sizeStr != null) {
            size = Integer.valueOf(sizeStr);
            if (size < 0) size = null;
        }

        Long minDocCount = null;
        String minDocCountStr = JSONValueParser.getValue(item.getString("min_doc_count"), item);
        if (minDocCountStr != null) {
            minDocCount = Long.valueOf(minDocCountStr);
        }

        return ZWAggregationBuilder.termsAggregationBuilder(item.getString("name"), item.getString("field")
                , size, ifAsc, item.getString("sort_by"), minDocCount);
    }

    public static AvgBuilder avgBuilder(JSONObject item) {
        return ZWAggregationBuilder.avgBuilder(item.getString("name"), item.getString("field"), JSONValueParser.getValue(item.getString("missing_value"), item));
    }

    public static CardinalityBuilder cardinalityBuilder(JSONObject item) {
        return ZWAggregationBuilder.cardinalityBuilder(item.getString("name"), item.getString("field"));
    }

    public static AggregationBuilder histogramBuilder(JSONObject item) {
        return ZWAggregationBuilder.histogramBuilder(item.getString("name"), item.getString("field"), item.getInteger("interval"));
    }

    public static MissingBuilder missingBuilder(JSONObject item) {
        return ZWAggregationBuilder.missingBuilder(item.getString("name"), item.getString("field"));
    }

    public static ExtendedStatsBuilder extendedStatsBuilder(JSONObject item, JSONObject data) {
        String missingValue = JSONValueParser.getValue(item.getString("missing_value"), data);
        return ZWAggregationBuilder.extendedStatsBuilder(item.getString("name"), item.getString("field"), missingValue);
    }

    public static MaxBuilder maxBuilder(JSONObject item, JSONObject data) {
        String missingValue = JSONValueParser.getValue(item.getString("missing_value"), data);
        return ZWAggregationBuilder.maxBuilder(item.getString("name"), item.getString("field"), missingValue);
    }

    public static MinBuilder minBuilder(JSONObject item, JSONObject data) {
        String missingValue = JSONValueParser.getValue(item.getString("missing_value"), data);
        return ZWAggregationBuilder.minBuilder(item.getString("name"), item.getString("field"), missingValue);
    }

    public static StatsBuilder statsBuilder(JSONObject item, JSONObject data) {
        String missingValue = JSONValueParser.getValue(item.getString("missing_value"), data);
        return ZWAggregationBuilder.statsBuilder(item.getString("name"), item.getString("field"), missingValue);
    }

    public static SumBuilder sumBuilder(JSONObject item, JSONObject data) {
        String missingValue = JSONValueParser.getValue(item.getString("missing_value"), data);
        return ZWAggregationBuilder.sumBuilder(item.getString("name"), item.getString("field"), missingValue);
    }

    public static ValueCountBuilder valueCountBuilder(JSONObject item, JSONObject data) {
        String missingValue = JSONValueParser.getValue(item.getString("missing_value"), data);
        return ZWAggregationBuilder.valueCountBuilder(item.getString("name"), item.getString("field"), missingValue);
    }

    public static RangeBuilder rangeBuilder(JSONObject item, JSONObject data) {
        JSONArray rangeList = item.getJSONArray("range_list");
        List<Range.NumberRange> ranges = new ArrayList<>();

        for (int i = 0; i < rangeList.size(); i++) {
            Range.NumberRange range = new Range().new NumberRange(JSONValueParser.getValue(item.getString("key"), data),
                    Double.valueOf(JSONValueParser.getValue(item.getString("from"), data)),
                    Double.valueOf(JSONValueParser.getValue(item.getString("to"), data)));

            ranges.add(range);
        }

        return ZWAggregationBuilder.rangeBuilder(item.getString("name"), item.getString("field"), ranges);

    }

    public static DateRangeBuilder dateRangeBuilder(JSONObject item, JSONObject data) {
        JSONArray rangeList = item.getJSONArray("range_list");
        List<Range.StringRange> ranges = new ArrayList<>();

        for (int i = 0; i < rangeList.size(); i++) {
            Range.StringRange range = new Range().new StringRange(JSONValueParser.getValue(item.getString("key"), data),
                    JSONValueParser.getValue(item.getString("from"), data),
                    JSONValueParser.getValue(item.getString("to"), data));

            ranges.add(range);
        }

        return ZWAggregationBuilder.dateRangeBuilder(item.getString("name"), item.getString("field"), item.getString("format"), ranges);
    }

    public static IPv4RangeBuilder iPv4RangeBuilder(JSONObject item, JSONObject data) {
        JSONArray rangeList = item.getJSONArray("range_list");
        List<Range.StringRange> ranges = new ArrayList<>();

        for (int i = 0; i < rangeList.size(); i++) {
            Range.StringRange range = new Range().new StringRange(JSONValueParser.getValue(item.getString("key"), data),
                    JSONValueParser.getValue(item.getString("from"), data),
                    JSONValueParser.getValue(item.getString("to"), data));

            ranges.add(range);
        }

        return ZWAggregationBuilder.iPv4RangeBuilder(item.getString("name"), item.getString("field"), ranges);
    }

    public static DateHistogramBuilder dateHistogramBuilder(JSONObject item) {
        return ZWAggregationBuilder.dateHistogramBuilder(item.getString("name"), item.getString("field"),
                item.getString("interval"), item.getString("format"), item.getString("time_zone"), item.getString("offset"),
                item.getString("sort"), item.get("missing"));
    }

}
