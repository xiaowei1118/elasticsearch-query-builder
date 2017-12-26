### elasticsearch-query-builder
#### Introduction
`elasticsearch-query-builder` is used for building [elasticsearch](https://www.elastic.co) query DSL. if there are so many query conditions, you won't build so complex elasticsearch DSL link before, and a config file is just enough using `elasticsearch-query-builder`. I belive that it will help you make your code simple and easy-understand.

[中文文档](https://github.com/xiaowei1118/elasticsearch-query-builder/blob/master/README-CN.md)

#### How to use it
At the first of all,create a config file following `elasticsearch-query-builder` rules just like the sample below.It is a standard json file.
```
{
  "index": "user_portrait",
  "type": "docs",
  "from": "${from}",
  "size": "10",
  "query_type": "terms_level_query",
  "terms_level_query": {
    "terms_level_type": "term_query",
    "term_query": {
      "value": "${value}",
      "key": "key",
      "boost": 2
    }
  },
  "aggregations": [
    {
      "aggregation_type": "terms",
      "name": "",
      "field": "field",
      "sub_aggregations": {
        "aggregation_type": "terms",
        "name": "sub",
        "field": "field",
        "size": "${size.value}",
        "sort": "asc",
        "sort_by": "_count"
      }
    }
  ],
  "highlight":{
      "fields": [
            {
              "field": "content",
              "number_of_fragment": 2,
              "no_match_size": 150
            }
       ],
      "pre_tags":["<em>"],
      "post_tags":["</em>"]
  },
  "sort": [
    "_score",
    {
      "field": "age",
      "order": "asc"
    }
  ]
}
```
Here are the [config file example](https://github.com/xiaowei1118/elasticsearch-query-builder/blob/master/src/main/resources/portrait_mapping.json).

##### query_type
There are three query_type defined in `elasticsearch-query-builder`,and <strong>they can't be used together</strong>.
1. `terms_level_query`.
 >   The `terms_level_query` operate on the exact terms that are stored in the inverted index.These queries are usually used for structured data like numbers, dates, and enums, rather than full text fields. Alternatively, they allow you to craft low-level queries, foregoing the analysis process.<br/>
 It contains `term_query`,`terms_query`,`range_query`,`exists_query` and so on.

2. `text_level_query`.
  > The `text_level_query` queries are usually used for running full text queries on full text fields like the body of an email. They understand how the field being queried is analyzed and will apply each field’s analyzer (or search_analyzer) to the query string before executing.<br/>
  It contains `match_query`,`multi_match_query`,`query_string`,`simple_query_string` and so on.
3. `bool_level` .
  > A query that matches documents matching boolean combinations of other queries. The bool query maps to Lucene BooleanQuery. It is built using one or more boolean clauses, each clause with a typed occurrence. <br/>
   The occurrence types are: must,filter,should,must_not.In each types , `terms_level_query` and `text_level_query` is involved.

##### data-parser
In addition to parsing config file , `elasticsearch-query-builder` parser parameters from JSONObject(alibaba [fastjson](https://github.com/alibaba/fastjson) object).We use the form of `${}` to indicate that the field needs to be fetched from an external data source, just as `${a}` indicates that we get a value from a field in Jsonobject. If you need to get data from a deeper level of JSON, just use`.` to represents a hierarchy, such as `${a.b.c}`. <br/>
If it is a range query, the `JSON data` must be a string in `[a, b]` format, a and b can be empty, but `,` can't be.

##### Run and use
clone this project and execute 'mvn package' and just use it as a `jar` file.

## License
elasticsearch-query-builder is available under the [MIT](https://www.opensource.org/licenses/mit-license.php) license. See the LICENSE file for more info.
