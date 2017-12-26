package org.coryphaei.query;

import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by twist on 2017-06-20.
 */
public class SortField {
    private String field;
    private SortOrder sortOrder;

    public SortField(String field, SortOrder sortOrder) {
        this.field = field;
        this.sortOrder = sortOrder;
    }

    public String getField() {
        return field;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }
}
