package org.coryphaei.query.builder;

import org.coryphaei.query.Range;
import org.coryphaei.query.SortField;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.missing.MissingBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.ipv4.IPv4RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.StatsBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;

import java.util.List;

/**
 * Created by twist on 2017-04-11.
 */
public class ZWAggregationBuilder {

    /**
     * 平均数聚合
     *
     * @param name  聚合名称
     * @param field 聚合字段
     * @return
     */
    public static AvgBuilder avgBuilder(String name, String field, Object missingValue) {
        return AggregationBuilders.avg(name).field(field).missing(missingValue);
    }

    /**
     * 添加terms聚合构建
     */
    public static TermsBuilder termsAggregationBuilder(String name, String field, Integer size, boolean ifAsc, String sortBy, Long minDocCount) {
        TermsBuilder termsBuilder = AggregationBuilders.terms(name).field(field);
        if (size != null) {
            termsBuilder.size(size);
        }

        if (sortBy != null && "_count".equals(sortBy)) {
            termsBuilder.order(Terms.Order.count(ifAsc));
        } else {
            termsBuilder.order(Terms.Order.term(ifAsc));
        }

        if (minDocCount != null) {
            termsBuilder.minDocCount(minDocCount);
        }

        return termsBuilder;
    }

    /**
     * 对单值进行基数(个数)统计
     *
     * @param name
     * @param field
     * @return
     */
    public static CardinalityBuilder cardinalityBuilder(String name, String field) {
        return AggregationBuilders.cardinality(name).field(field);
    }

    /**
     * 单值统计,包括min,max,avg,sum,方差,等
     *
     * @param name
     * @param field
     * @return
     */
    public static ExtendedStatsBuilder extendedStatsBuilder(String name, String field, Object missingValue) {
        ExtendedStatsBuilder extendedStatsBuilder = AggregationBuilders.extendedStats(name).field(field);
        if (missingValue != null) {
            extendedStatsBuilder.missing(missingValue);
        }

        return extendedStatsBuilder;
    }

    /**
     * 最大值统计
     *
     * @param name
     * @param field
     * @param missingValue
     * @return
     */
    public static MaxBuilder maxBuilder(String name, String field, Object missingValue) {
        MaxBuilder maxBuilder = AggregationBuilders.max(name).field(field);

        if (missingValue != null) {
            maxBuilder.missing(missingValue);
        }

        return maxBuilder;
    }

    /**
     * 最小值统计
     *
     * @param name
     * @param field
     * @param missingValue
     * @return
     */
    public static MinBuilder minBuilder(String name, String field, Object missingValue) {
        MinBuilder minBuilder = AggregationBuilders.min(name).field(field);

        if (missingValue != null) {
            minBuilder.missing(missingValue);
        }

        return minBuilder;
    }

    /**
     * 统计min,sum,max,avg,count
     *
     * @param name
     * @param field
     * @param missingValue
     * @return
     */
    public static StatsBuilder statsBuilder(String name, String field, Object missingValue) {
        StatsBuilder statsBuilder = AggregationBuilders.stats(name).field(field);

        if (missingValue != null) {
            statsBuilder.missing(missingValue);
        }

        return statsBuilder;
    }

    /**
     * 某个字段的和统计
     *
     * @param name
     * @param field
     * @param missingValue
     * @return
     */
    public static SumBuilder sumBuilder(String name, String field, Object missingValue) {
        SumBuilder sumBuilder = AggregationBuilders.sum(name).field(field);

        if (missingValue != null) {
            sumBuilder.missing(missingValue);
        }

        return sumBuilder;
    }

    /**
     * 获取top hits的记录
     *
     * @param name
     * @param from
     * @param size
     * @param sortField
     * @return
     */
    public static TopHitsBuilder topHitsBuilder(String name, Integer from, Integer size, SortField sortField) {
        TopHitsBuilder topHitsBuilder = AggregationBuilders.topHits(name);

        if (from != null) {
            topHitsBuilder.setFrom(from);
        }

        if (size != null) {
            topHitsBuilder.setSize(size);
        }

        if (sortField != null) {
            topHitsBuilder.addSort(sortField.getField(), sortField.getSortOrder());
        }

        return topHitsBuilder;
    }

    /**
     * 统计某个字段不为空的记录个数
     *
     * @param name
     * @param field
     * @param missingValue
     * @return
     */
    public static ValueCountBuilder valueCountBuilder(String name, String field, Object missingValue) {
        ValueCountBuilder valueCountBuilder = AggregationBuilders.count(name).field(field);

        if (missingValue != null) {
            valueCountBuilder.missing(valueCountBuilder);
        }

        return valueCountBuilder;
    }

    /**
     * 范围聚合统计
     *
     * @param name
     * @param field
     * @param rangeList
     * @return
     */
    public static RangeBuilder rangeBuilder(String name, String field, List<Range.NumberRange> rangeList) {
        RangeBuilder rangeBuilder = AggregationBuilders.range(name).field(field);

        for (Range.NumberRange range : rangeList) {
            rangeBuilder.addRange(range.getKey(), range.getFrom(), range.getTo());
        }

        return rangeBuilder;
    }

    /**
     * 字段值不存在统计
     *
     * @param name
     * @param field
     * @return
     */
    public static MissingBuilder missingBuilder(String name, String field) {

        return AggregationBuilders.missing(name).field(field);
    }

    /**
     * 根据时间范围进行聚合
     *
     * @param name
     * @param field
     * @param format
     * @return
     */
    public static DateRangeBuilder dateRangeBuilder(String name, String field, String format, List<Range.StringRange> ranges) {
        DateRangeBuilder dateRangeBuilder = AggregationBuilders.dateRange(name).field(field);
        if (format != null) {
            dateRangeBuilder.format(format);
        }

        for (Range.StringRange range : ranges) {
            dateRangeBuilder.addRange(range.getKey(), range.getFrom(), range.getTo());
        }

        return dateRangeBuilder;
    }

    /**
     * 根据ipv4地址进行聚合
     *
     * @param name
     * @param field
     * @param ranges
     * @return
     */
    public static IPv4RangeBuilder iPv4RangeBuilder(String name, String field, List<Range.StringRange> ranges) {
        IPv4RangeBuilder iPv4RangeBuilder = AggregationBuilders.ipRange(name).field(field);

        for (Range.StringRange range : ranges) {
            iPv4RangeBuilder.addRange(range.getKey(), range.getFrom(), range.getTo());
        }

        return iPv4RangeBuilder;
    }

    public static AggregationBuilder histogramBuilder(String name, String field, int interval) {
        return AggregationBuilders.histogram(name).field(field).interval(interval);
    }

    public static DateHistogramBuilder dateHistogramBuilder(String name, String field, String interval, String format, String timeZone, String offset, String sort, Object missing) {
        DateHistogramBuilder dateHistogramBuilder = AggregationBuilders.dateHistogram(name).field(field).interval(new DateHistogramInterval(interval));

        if (format != null) {
            dateHistogramBuilder.format(format);
        }

        if (timeZone != null) {
            dateHistogramBuilder.timeZone(timeZone);
        }

        if (offset != null) {
            dateHistogramBuilder.offset(offset);
        }

        if (sort != null) {
            if ("desc".equalsIgnoreCase(sort)) {
                dateHistogramBuilder.order(Histogram.Order.KEY_DESC);
            } else {
                dateHistogramBuilder.order(Histogram.Order.KEY_ASC);
            }
        }

        if (missing != null) {
            dateHistogramBuilder.missing(missing);
        }

        return dateHistogramBuilder;
    }
}
