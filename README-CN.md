### elasticsearch-query-builder
#### 简介
`elasticsearch-query-builder` 是一个非常方便构造 [elasticsearch](https://www.elastic.co) 查询语句的工具. 在具有多构造查询条件的情况下,使用`elasticsearch-query-builder`可以不用在代码里面构造复杂的查询语句. 这不将不仅减少代码编写的工作量，同时也会使你的代码更加清晰简洁，便于理解。

[查看详细说明文档](https://github.com/xiaowei1118/elasticsearch-query-builder/blob/master/INTRODUCTION.md)
#### 使用方法
首先,需要构造一个遵循 `elasticsearch-query-builder` 定义规则的配置文件， 如下所示. 它是一个标准的json文件。
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
这是配置文件的[样例](https://github.com/xiaowei1118/elasticsearch-query-builder/blob/master/src/main/resources/portrait_mapping.json).

##### query_type
在 `elasticsearch-query-builder` 里定义了三种query_type, <strong>并且不可以同时使用</strong>。
1. `terms_level_query`.
 >   "terms_level_query" 操作的精确字段是存储在反转索引中的。这些查询通常用于结构化数据, 如数字、日期和枚举, 而不是全文字段,包含`term_query`,`terms_query`,`range_query`,`exists_query` 等类型。

2. `text_level_query`.
  > "text_level_query" 查询通常用于在完整文本字段 (如电子邮件正文) 上运行全文查询。他们了解如何分析被查询的字段, 并在执行之前将每个字段的分析器 (或 search_analyzer) 应用到查询字符串。<br/>
  包含 `match_query`,`multi_match_query`,`query_string`,`simple_query_string` 等类型。
3. `bool_level` .
  >与其他查询的布尔组合匹配的文档匹配的查询。bool 查询映射到 Lucene BooleanQuery。它是使用一个或多个布尔子句生成的, 每个子句都有一个类型化的实例。 <br/>
   布尔查询的查询值包括: must,filter,should,must_not. 想要了解这几个类型的差异，可以查阅elasticsearch的相关文档(这里默认使用本项目的人是有基本的elasticsearch基础的).
   在每个布尔查询的查询类型值中, 可以包含`terms_level_query` 和 `text_level_query`中任意的查询类型，如此便可以构造非常复杂的查询情况。

##### 参数解析
除了解析配置文件外 , `elasticsearch-query-builder` 还可以从外部的json数据源中解析参数，这里面json解析用的是阿里巴巴的 [fastjson](https://github.com/alibaba/fastjson)工具包.我们在配置文件中使用 `${}` 这个字段的value值必须从外部数据源中获取,就像 `${a}`表示从json对象中获取key值为a的value值. 如果你想从json更深的数据层次中获取数据, 我们通过`.`号来表示json中的数据格式，比如 `${a.b}`. <br/>

注意，如果是一个range_query的范围查询, json数据中必须是如 `[a, b]` 格式的字符串，a表示范围的起点，b表示范围的终点, a和b可以同时为空，但是`,`不可缺省。

##### 使用
clone`elasticsearch-query-builder`并且在本地执行 'mvn package',本项目一般以jar包的形式为您的项目提供服务，或者将源码导入项目目录中也可。

## License
 elasticsearch-query-builder is available under the [MIT](https://www.opensource.org/licenses/mit-license.php) license. See the LICENSE file for more info.
