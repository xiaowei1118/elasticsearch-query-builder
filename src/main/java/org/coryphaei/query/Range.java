package org.coryphaei.query;

/**
 * Created by twist on 2017-06-20.
 */

public class Range {
    public class NumberRange {
        private String key;
        private Double from;
        private Double to;

        public NumberRange(String key, Double from, Double to) {
            this.key = key;
            this.from = from;
            this.to = to;
        }

        public String getKey() {
            return key;
        }

        public Double getFrom() {
            return from;
        }

        public Double getTo() {
            return to;
        }
    }

    public class StringRange {
        private String key;
        private String from;
        private String to;

        public StringRange(String key, String from, String to) {
            this.key = key;
            this.from = from;
            this.to = to;
        }

        public String getKey() {
            return key;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }
}
