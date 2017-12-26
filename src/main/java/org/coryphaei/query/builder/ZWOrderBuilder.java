package org.coryphaei.query.builder;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by twist on 2017-05-08.
 */
public class ZWOrderBuilder {

    public static FieldSortBuilder fieldSortBuilder(String field, String order, Object missing, String mode, String unmappedType) {
        FieldSortBuilder fieldSortBuilder = new FieldSortBuilder(field);
        if (order != null) {
            fieldSortBuilder.order(SortOrder.valueOf(order.toUpperCase()));
        }

        if (missing != null) {
            fieldSortBuilder.missing(missing);
        }

        if (mode != null) {
            fieldSortBuilder.sortMode(mode);
        }

        if (unmappedType != null) {
            fieldSortBuilder.unmappedType(unmappedType);
        }

        return fieldSortBuilder;
    }

    public static ScoreSortBuilder scoreSortBuilder() {
        return new ScoreSortBuilder();
    }

//    public static GeoDistanceSortBuilder geoDistanceSortBuilder(String field, String geoDistance) {
//        GeoDistanceSortBuilder geoDistanceSortBuilder = new GeoDistanceSortBuilder(field);
//        if (geoDistance != null) {
//           geoDistanceSortBuilder.geoDistance(GeoDistance.ARC)
//        }
//
//        return geoDistanceSortBuilder;
//    }

//    public static ScriptSortBuilder scriptSortBuilder(){
//        ScriptSortBuilder scriptSortBuilder = new ScriptSortBuilder();
//    }
}
