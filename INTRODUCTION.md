
### 前言
在这里，我想向大家推荐一个我自己开发的项目，也就是`elasticsearch-query-builder`,这个项目目前在github上已经开源，有兴趣的朋友可以去fork或者star，你的star就是对我最大的鼓励。同时，本项目长期维护和更新，我也接受并且很高兴有小伙伴向本项目pull request，或者协同开发，有兴趣的同学可以给我发邮件。
 
 `elasticsearch-query-builder`是一个非常方便构造`elasticsearch`(后面简称ES) DSL 查询语句的工具包，在`elasticsearch-query-builder`中，我尝试基于配置化的操作去构建ES的查询语句，并且接受外界传入参数，这极大的减少了在Java代码中构建ES查询语句的工作，并同时减少了代码量，使代码更加直观和清晰。基于使ES中DSL构造语句和Java代码分离的思想，`elasticsearch-query-builder`诞生了。戳右上角Fork!
 
 ### 构建
 `elasticsearch-query-builder`工程一般作为jar包为别的工程提供使用，当然，如果需要基于本项目做二次开发，这都需要将Github上克隆本项目到本地
 ```
 git clone https://github.com/xiaowei1118/elasticsearch-query-builder.git
 ```
 在将本项目克隆到本地后，执行`mvn package` 将本项目打成jar包，或者直接将本项目作为你们自己maven项目的module项目。
 
 ### 使用说明
 `elastcisearch-query-builder`接受配置文件(特定json格式)或者json格式的字符串配置，配置格式如下：
 ``` json
{
  "index": "user_portrait",
  "type": "docs",
  "from": "${from}",
  "size": "10",
  "include_source": ["name","age"],  //需要哪些字段
  "exclude_source": ["sex"],  //排除哪些字段
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
#### 参数说明
##### # index
index表示ES中的索引或者别名。
##### # type
type表示ES索引或者别名下的type。
##### # from
from表示检索文档时的偏移量，相当于关系型数据库里的offset。
##### # include_source
include_source 搜索结果中包含某些字段，格式为json数组，``"include_source": ["name","age"]``。
#### # exclude_source
exclude_source 搜索结果中排除某些字段，格式为json数组，``"exclude_source":["sex"]``。
##### # query_type 
query_type表示查询类型，支持三种类型`terms_level_query`,`text_level_query`,`bool_level_query`,并且这三种类型
不可以一起使用。
+ `terms_level_query` 操作的精确字段是存储在反转索引中的。这些查询通常用于结构化数据, 如数字、日期和枚举, 而不是全文字段,包含term_query,terms_query,range_query,exists_query 等类型。

+ `text_level_query` 查询通常用于在完整文本字段 (如电子邮件正文) 上运行全文查询。他们了解如何分析被查询的字段, 并在执行之前将每个字段的分析器 (或 search_analyzer) 应用到查询字符串。<br/>
包含 match_query,multi_match_query,query_string,simple_query_string 等类型。

+  `bool_query` 与其他查询的布尔组合匹配的文档匹配的查询。bool 查询映射到 Lucene BooleanQuery。它是使用一个或多个布尔子句生成的, 每个子句都有一个类型化的实例。 布尔查询的查询值包括: must,filter,should,must_not。 在每个布尔查询的查询类型值中, 可以包含terms_level_query 和 text_level_query中任意的查询类型，如此便可以构造非常复杂的查询情况。

##### # terms_level_query查询类型
##### terms_level_type
terms_level查询类型，支持`term_query`,`terms_query`,`range_query`,`exists_query`查询。
+ `term_query` 
  + key 表示ES中需要查询的字段
  + value 表示要查询的值
  + boost 占搜索中的权重
    ```
    "term_query": {
          "value": "",
          "key": "",
          "boost": 2
        }
    ```
+ `terms_query`
   + key,value,boost解释同`term_query`。
   + value 可以传入多个，以逗号隔开，如"[1,2]"。
    ```
     "terms_query": {
          "value": "[1,2]", //数组
          "key": ""
        },
    ```
+ `range_query`，给定的查询条件使一个范围
  + key 表示ES中需要查询的字段
  + range 表示要搜索的值范围，格式如"[a,b]",表示范围在a、b之间，a、b可以缺省，a缺省则表示没有下限，
  b缺省则表示没有上限，但ab不可以同时为空。a，b可以为时间或者数值。
  + boost 占搜索中的权重
  + format 如果范围使时间的化，format定义时间格式。
  + include_lower 布尔值，是否包含下限。
  + include_upper 布尔值，是否包含上限。
    ```
    "range_query": {
          "key": "",
          "range": "", //[,]
          "boost": "",
          "format": "",
          "include_lower": true,
          "include_upper": false
    }
    ```

+ `exists_query`,存在查询，查找字段不存在的文档。
   + key ES字段。
    ````
    "exists_query": {
      "key": ""
    }
    ````
   
##### # text_level_query查询类型
##### text_query_type
text_level_query查询类型，支持match_query,multi_match_query,query_string,simple_query_string等。
+ match_query,普通的文本匹配查询。
  + key 供文本匹配的ES字段
  + value 需要搜索的文本关键字，会分词。
  + zero_terms_query 决定是否使用停词。all表示不使用停词，默认使none。
    ````
    "match_query": {
      "key": "",
      "value": "this is a test",
      "zero_terms_query": "none" 
    } 
    ````
+ multi_match_query 在多个字段中进行文本匹配
  + value 需要搜索的文本关键字，会分词。
  + fields ES中的字段，可以多个，用逗号隔开，在字段旁边使用^表示该字段的权重，如"a^3,b"。
  + type 匹配类型，支持best_fields(默认),most_fields,cross_fields,phrase,phrase_prefix。
    ````
     "multi_match_query": {
          "value": "",
          "fields": "a^3,b", 
          "type": "best_fields" //most_fields,cross_fields,phrase,phrase_prefix
        }
    ````

+ query_string 字符串文本匹配。
    + value 需要搜索的文本关键字，会分词。
    + fields ES中的字段，格式为数组，如"[a,b]"
    ````
     "query_string": {
          "value": "",
          "fields": ""//数组
     }
    ````
    
+ simple_query_string 简单字符串匹配
    + value fields 同 query_string
    + default_operate 匹配逻辑，值为`and` 或者 `or`。
    ````
    "simple_query_string": {
          "value": "",
          "fields": "", //数组
          "default_operate": "and"
    }
    ````
    
+ match_all_query 匹配所有文档

##### # bool_query 布尔查询
bool_query是其他查询的布尔组合，一般用于构建复杂的查询，而这正是`elasticsearch-query-builder`最拿手的地方。
##### bool_type 
+ must查询 
  must查询所有的查询条件都会用于做文档匹配（相当于and），并且用于计算相关度score的值。
+ filter查询 
  filter查询所有的查询条件都会用于做文档匹配（相当于and），但是和must不同的是，filter查询里面的查询条件并不用于计算相关度。
+ should查询 
  should查询条件只需要满足其中一个即可（相当于or)。
+ must_not 
  must_not查询表示所有的查询条件同时不满足（相当于not)。
如:
`````
 "query_type": "bool_query",
  "bool_query": [
    {
      "bool_type": "must",
      "items": [
        {
          "value": "",
          "key": "province_code",
          "terms_method": "term" //term,terms,range,match
        },
        {
          "value": "",
          "key": "city_code",
          "terms_method": "term" //term,terms,range,match
        }
      ]
    },
    {
      "bool_type":"filter",
      "items":[
        "value": "",
        "key": "sex",
        "terms_method":"term"
      ]
    }
  ]
`````

##### # aggregation 聚合
ES的聚合操作通常用于聚合查询结果数据，通常用于数据的分类和统计工作。同时ES本身支持多种聚合操作，为我们的数据分析和统计提供了便利，相应的，本项目也支持聚合操作的配置化和参数绑定。
##### Avg Aggregation  计算平均数
   + name 聚合名称。
   + aggregation_type 聚合类型。
   + field  查询结果中用于聚合的字段。
   + missing_value 如果文档中该字段为空时，设置的默认值。
````
    "aggregations": [
        {
          "aggregation_type": "avg", // 聚合类型
          "name": "",  //聚合的名称
          "field": "",
          "missing_value": 10
        }
    ]
````

##### Terms Aggregation  根据字段的值进行聚合
 + name 聚合名称。
 + aggregation_type 聚合类型。
 + field 查询结果中用于聚合的字段。
 + size 默认为10，设置为0即统计所有的字段值分类的文档个数。
 + sort asc || desc,确定是升序还是降序，default: asc。
 + min_doc_count  最小的匹配文档数，count低于该值的字段值不显示。
 + sort_by 设置聚合结果的排序，默认是根据聚合字段的值排序，可以设置成以聚合分类下的个数排序即_count。
 + sub_aggregations  子聚合，对聚合结果进行再聚合，子聚合可以是别的任意聚合类型。
```json
 {
      "aggregation_type": "terms",
      "name": "",
      "field": "",
      "size": "${size.value}",
      "sort": "asc",
      "sort_by": "_count",
      "sub_aggregations":{     //子聚合
          "aggregation_type": "terms",
          "name": "",
          "field": "",
          "size": "${size.value}",
          "sort": "asc",
          "sort_by": "_count"
      }
    }
```

因为aggregation聚合的类型比较多，另外还有，`min, max, cardinality,extended_stats, stats,sum,top_hits,value_count,range,missing,date_range,ipv4_range,date_histogram`等，这里就不再赘述，需要查看聚合类型怎么用的，可以查看[配置文件样例](https://github.com/xiaowei1118/elasticsearch-query-builder/blob/master/src/main/resources/portrait_mapping.json)

##### # highlight 文档高亮
ES可以设置对查询结果中包含搜索关键字的字段部分进行高亮。
 + fields 设置需要高亮的字段
     + field 字段名
     + fragment_size 字段高亮显示的片段的字符长度大小，default: 100
     + number_of_fragment 最多返回片段数，default: 5
 + pre_tags 匹配出来的文档的标签前缀, default: `<em>`
 + post_tags 匹配出来的文档的标签后缀, default: `</em>`
 + tags_schema 
 + order 高亮片段的排序方式
```
"highlight":{
    "fields": [
      {
        "field": "content",
        "number_of_fragment": 2,
        "fragment_size": 150
      }
    ],
    "pre_tags":[],
    "post_tags":[],
    "tags_schema":"", //styled
    "order":"score"
  }
```

#### 参数绑定
##### # 单值参数绑定
单值绑定，这里我们以sex字段为例，我们需要查询出index中性别为女性的记录，我们可以用terms查询，如：
```
"term_query": {
  "value": "female",
  "key": "sex",
}
```
通过以上查询就可以查出性别为女性的文档。那如果我们的value值需要从外面传进来呢，比如我们的参数在一个json字符串中（非常适合application/json的传值:)),如:`{ "sex": "female",type: 1}`,我们的配置文件应该怎么写？

在`elasticsearch-query-builder`中，我们约定需要外界绑定的参数用`${}`将字段包括进来，如：`${sex}`这里的`sex`同json数据里面的key一致。那么在配置文件中就转换成了:
```
"term_query": {
  "value": "${sex}",
  "key": "sex",
}
```
如果json中的字段不在第一层呢？比方说:`{ "a.sex": "female",type: 1}`, 那么我们用`.`号来表示层级结构，`${a.sex}`, 不管层级多深都没有问题。

##### # 范围参数绑定
对于范围类型的参数绑定，比如：`type 从 1->6` 或者 `date 从 2017-06-10 -> 2017-12-12`, 我们应该怎么从外界进行参数绑定呢？在range查询中，我们已经定义了range的传参方式，如下:
```
"range_query":{
   "range": "[2017-06-10, 2017-12-12]"
   "key": "date"
}
```
其实，其实range的参数绑定和单值的参数绑定是一致的，虽然有范围，其实取的还是单值`${date}`, 只是我们对外界的json数据结构表示范围的字段有限制，我们规定json中表示范围的字段必须是`[a,b]`的形式，a和b可以单一缺省，表示无上界或者下界，但是a和b不可以同时缺省（同时缺省，这个范围查询是没有意义的）。
如json数据：`{ "date": "[2017-06-10, 2017-12-12]" }`即符合规范。

#### 使用示例
这个例子也是`elasticsearch-query-builder`种的example。
我们先定义配置文件`test.json`:
```
{
  "index":"event",
  "query_type":"bool_query",
  "size":"${pageSize}",
  "from":"${offset}",
  "include_source": ["age"],
  "sort":[
    {
      "field":"date_time",
      "order":"desc"
    }
  ],
  "bool_query":[
    {
      "bool_type":"filter",
      "items":[
        {
          "terms_method":"term",
          "value":"${is_success}",
          "key":"is_success"
        },
        {
          "terms_method":"range",
          "range":"${date_time}",
          "key":"date_time"
        },
        {
          "terms_method":"term",
          "value":"${id}}",
          "key":"id"
        }
      ]
    },
    {
      "bool_type":"must",
      "items":[
        {
          "terms_method":"match",
          "value":"${name}",
          "key":"name"
        }
      ]
    }
  ],
  "type":"docs"
}
```
使用`elasticsearch-query-builder`生成ES的查询语句。
```
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.coryphaei.query.FileUtil;
import org.coryphaei.query.parser.ZWSearchParser;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * Created by twist on 2017-08-05.
 */
public class ElasticsearchParser {
    public static void main(String[] args) throws Exception {
        String dataStr = "{\n" +               //需要绑定的json格式参数
                "\t\"pageSize\":10,\n" +
                "\t\"from\": 10,\n" +
                "\t\"id\": \"1\",\n" +
                "\t\"date_time\":\"[2017-06-01,]\",\n" +
                "\t\"is_success\":1,\n" +
                "\t\"name\":\"Bob\"\n" +
                "}";

        JSONObject data = JSON.parseObject(dataStr);
        String config = FileUtil.readResourceRemoveComments(ElasticsearchParser.class, "test.json");  //加载配置文件
        SearchSourceBuilder searchSourceBuilder = ZWSearchParser.searchSourceBuilder(JSON.parseObject(config), data);  //生成DSL查询语句
        System.out.println(searchSourceBuilder.toString());
    }
}

```
生成的ES的DSL查询语句：
```json
{
  "size" : 10,
  "query" : {
    "bool" : {
      "must" : {
        "match" : {
          "name" : {
            "query" : "Bob",
            "type" : "boolean"
          }
        }
      },
      "filter" : [ {
        "term" : {
          "is_success" : "1"
        }
      }, {
        "range" : {
          "date_time" : {
            "from" : "2017-06-01",
            "to" : null,
            "include_lower" : true,
            "include_upper" : true
          }
        }
      } ]
    }
  },
  "_source" : {
    "includes" : [ "age" ],
    "excludes" : [ ]
  },
  "sort" : [ {
    "date_time" : {
      "order" : "desc"
    }
  } ]
}
```

#### 不足和待改进
本项目并没有涵盖ES的所有查询功能，同时，也没有包含ES的最新版本的功能，这些都是我后续需要逐渐完善的地方，我希望可以通过自己的努力，使本项目越来越完善。

#### 致谢
本项目使用了阿里的`fastjson`jar包，elasticSearch公司的`elasticsearch`jar包，以及`io.searchbox`的`jest`jar包，这里表示由衷的感谢。
  

 