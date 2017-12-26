package org.coryphaei.query;

/**
 * Created by twist on 2017-04-13.
 */
public class DocumentField {
    private String field;
    private Float boost;

    public DocumentField(String field, Float boost) {
        this.field = field;
        this.boost = boost;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Float getBoost() {
        return boost;
    }

    public void setBoost(Float boost) {
        this.boost = boost;
    }
}
