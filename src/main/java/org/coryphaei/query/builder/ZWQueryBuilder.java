package org.coryphaei.query.builder;

import org.coryphaei.query.DocumentField;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;
import java.util.Map;

/**
 * Created by twist on 2017-04-05.
 */
public class ZWQueryBuilder {
    /**
     * SearchSourceBuilder构造
     *
     * @param queryBuilders
     * @param size
     * @param offset
     * @return
     */
    public static SearchSourceBuilder searchSourceBuilder(QueryBuilder queryBuilders, Integer size, Integer offset) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilders);
        searchSourceBuilder.from(offset);
        searchSourceBuilder.size(size);

        return searchSourceBuilder;
    }

    /**
     * range org.coryphaei.query builder
     *
     * @param field
     * @return
     */
    public static RangeQueryBuilder rangeQueryBuilder(String field, Object from, Object to, String format, Float boost, Boolean includeLower, Boolean includeUpper) {
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(field);
        if (from != null) {
            rangeQueryBuilder.from(from);
        }

        if (to != null) {
            rangeQueryBuilder.to(to);
        }

        if (format != null) {
            rangeQueryBuilder.format(format);
        }

        if (boost != null) {
            rangeQueryBuilder.boost(boost);
        }

        if (includeLower != null) {
            rangeQueryBuilder.includeLower(includeLower);
        }

        if (includeUpper != null) {
            rangeQueryBuilder.includeUpper(includeUpper);
        }

        return rangeQueryBuilder;
    }


    /**
     * 获取querybuilder
     *
     * @param query
     * @param fields
     * @return
     */
    public static QueryStringQueryBuilder queryStringQueryBuilder(String query, List<DocumentField> fields) {
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(query);
        for (DocumentField field : fields) {
            queryStringQueryBuilder.field(field.getField(), field.getBoost());
        }

        return queryStringQueryBuilder;
    }

    /**
     * 获取querybuilder
     *
     * @param query
     * @param fields
     * @return
     */
    public static SimpleQueryStringBuilder simpleQueryStringBuilder(String query, List<DocumentField> fields) {
        SimpleQueryStringBuilder queryStringQueryBuilder = QueryBuilders.simpleQueryStringQuery(query);
        for (DocumentField field : fields) {
            queryStringQueryBuilder.field(field.getField(), field.getBoost());
        }

        return queryStringQueryBuilder;
    }

    /**
     * get sortBuilder
     *
     * @param orderBy
     * @param order
     * @return
     */
    public static SortBuilder sortBuilder(String orderBy, String order) {
        SortBuilder sort = new FieldSortBuilder(orderBy);
        if ("desc".equalsIgnoreCase(order)) {
            sort.order(SortOrder.DESC);
        } else {
            sort.order(SortOrder.ASC);
        }

        return sort;
    }


    /**
     * get termQuery
     *
     * @param values
     * @param field
     * @return
     */
    public static TermsQueryBuilder termsQueryBuilder(Object[] values, String field, Float boost) {
        TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder(field, values);
        if (boost != null) {
            termsQueryBuilder.boost(boost);
        }

        return termsQueryBuilder;
    }

    /**
     * term org.coryphaei.query build
     * @param query
     * @param field
     * @param boost
     * @return
     */
    public static TermQueryBuilder termQueryBuilder(String query, String field, Float boost) {
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder(field, query);
        if (boost != null) {
            termQueryBuilder.boost(boost);
        }

        return termQueryBuilder;
    }

    /**
     * @param map
     * @param boolOperate
     * @return
     */
    public static BoolQueryBuilder boolQueryBuilder(Map<String, String> map, String boolOperate) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if ("must".equals(boolOperate)) {
            for (String key : map.keySet()) {
                boolQueryBuilder.must(termQueryBuilder(map.get(key), key, null));
            }
        } else if ("should".equals(boolOperate)) {
            for (String key : map.keySet()) {
                boolQueryBuilder.should(termQueryBuilder(map.get(key), key, null));
            }
        } else if ("must_not".equals(boolOperate)) {
            for (String key : map.keySet()) {
                boolQueryBuilder.mustNot(termQueryBuilder(map.get(key), key, null));
            }
        } else if ("filter".equals(boolOperate)) {
            for (String key : map.keySet()) {
                boolQueryBuilder.filter(termQueryBuilder(map.get(key), key, null));
            }
        }

        return boolQueryBuilder;
    }

    /**
     * get searchsource builder
     *
     * @param size
     * @param offset
     * @return
     */
    public static SearchSourceBuilder searchSourceBuilder(Integer size, Integer offset) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(offset);
        searchSourceBuilder.size(size);

        return searchSourceBuilder;
    }

    /**
     * query_string org.coryphaei.query
     *
     * @param query
     * @param field
     * @return
     */
    public static QueryStringQueryBuilder queryStringQueryBuilder(String query, String field) {
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query).field(field);
        return queryBuilder;
    }

    /**
     * query_string org.coryphaei.query
     *
     * @param query
     * @param field
     * @return
     */
    public static SimpleQueryStringBuilder simpleQueryStringBuilder(String query, String field) {
        SimpleQueryStringBuilder queryBuilder = QueryBuilders.simpleQueryStringQuery(query).field(field);
        return queryBuilder;
    }

    /**
     * match_all org.coryphaei.query
     *
     * @param boost
     * @return
     */
    public static MatchAllQueryBuilder matchAllQueryBuilder(Float boost) {
        MatchAllQueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        if (boost != null) {
            queryBuilder.boost(boost);
        }

        return queryBuilder;
    }

    /**
     * match org.coryphaei.query builder
     *
     * @param query
     * @param field
     * @param boost
     * @param zeroTermsQuery
     * @return
     */
    public static MatchQueryBuilder matchQueryBuilder(String query, String field, Float boost, String zeroTermsQuery) {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery(field, query);

        if (boost != null) {
            queryBuilder.boost(boost);
        }

        if (zeroTermsQuery != null && "all".equals(zeroTermsQuery)) {
            queryBuilder.zeroTermsQuery(MatchQueryBuilder.ZeroTermsQuery.ALL);
        }

        return queryBuilder;
    }

    /**
     * match org.coryphaei.query builder
     *
     * @param query
     * @param documentFieldList
     * @param zeroTermsQuery
     * @return
     */
    public static MultiMatchQueryBuilder multiMatchQueryBuilder(String query, List<DocumentField> documentFieldList, String zeroTermsQuery) {
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query);

        for (DocumentField documentField : documentFieldList) {
            queryBuilder.field(documentField.getField(), documentField.getBoost());
        }

        if (zeroTermsQuery != null && "all".equals(zeroTermsQuery)) {
            queryBuilder.zeroTermsQuery(MatchQueryBuilder.ZeroTermsQuery.ALL);
        }

        return queryBuilder;
    }

    /**
     * exists org.coryphaei.query builder
     *
     * @param field
     * @return
     */
    public static ExistsQueryBuilder existsQueryBuilder(String field) {
        ExistsQueryBuilder queryBuilder = QueryBuilders.existsQuery(field);

        return queryBuilder;
    }

    /**
     * match org.coryphaei.query builder
     *
     * @param field
     * @return
     */
    public static PrefixQueryBuilder prefixQueryBuilder(String field,String prefix) {
        PrefixQueryBuilder queryBuilder = QueryBuilders.prefixQuery(field,prefix);

        return queryBuilder;
    }

    public static IdsQueryBuilder idsQueryBuilder(String[] types,String[] values,Float boost){
        IdsQueryBuilder queryBuilder = QueryBuilders.idsQuery(types);
        queryBuilder.addIds(values);
        queryBuilder.boost(boost);

        return queryBuilder;
    }
}
