package org.coryphaei.query.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import io.searchbox.params.SearchType;
import org.coryphaei.query.builder.ZWQueryBuilder;
import org.coryphaei.query.parser.ZWSearchParser;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;

public class ElasticSearch {
    private JestClient client;

    public void setJestClient(JestClient client) {
        this.client = client;
    }

    /**
     * 简单字符串搜索结果
     *
     * @param column
     * @param queryString
     * @param index
     * @param size
     * @param offset
     * @return
     */
    public JSONArray commonSearchResult(String column, String queryString, String index, String type, Integer size, Integer offset) {

        SearchSourceBuilder searchSourceBuilder = getSearchSourceBuilder(ZWQueryBuilder.queryStringQueryBuilder(queryString, column), size, offset);

        return SearchResult(searchSourceBuilder, index, type, null);
    }

    /**
     * SearchSourceBuilder构造
     *
     * @param queryBuilders
     * @param size
     * @param offset
     * @return
     */
    public SearchSourceBuilder getSearchSourceBuilder(QueryBuilder queryBuilders, Integer size, Integer offset) {
        return ZWQueryBuilder.searchSourceBuilder(queryBuilders, size, offset);
    }

    /**
     * 通用通过json访问es
     *
     * @param searchSourceBuilder
     * @param index
     * @param type
     * @param searchType
     * @return
     */
    public JSONArray SearchResult(SearchSourceBuilder searchSourceBuilder, String index, String type, SearchType searchType) {
        return getSearchDocumentResult(searchSourceBuilder, index, type, searchType);
    }

    /**
     * 获取es搜索结果
     *
     * @param searchSourceBuilder
     * @param index
     * @param type
     * @param searchType
     * @return
     */
    public JestResult getJestResult(SearchSourceBuilder searchSourceBuilder, String index, String type, SearchType searchType) {
        if (searchType == null) {
            searchType = SearchType.QUERY_THEN_FETCH;
        }

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type).setSearchType(searchType).build();

        try {
            return client.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取文档搜索列表
     *
     * @param searchSourceBuilder
     * @param index
     * @param type
     * @param searchType
     * @return
     */
    public JSONArray getSearchDocumentResult(SearchSourceBuilder searchSourceBuilder, String index, String type, SearchType searchType) {
        return parseDocumentResult(getJestResult(searchSourceBuilder, index, type, searchType));
    }

    /**
     * 获取聚合结果列表
     *
     * @return
     */
    public JSONObject getSearchAggregationResult(SearchSourceBuilder searchSourceBuilder, String index, String type, SearchType searchType) {
        JSONObject jsonObject = new JSONObject();
        JestResult jestResult = getJestResult(searchSourceBuilder, index, type, searchType);
        if (jestResult.isSucceeded()) {
            jsonObject = JSON.parseObject(jestResult.getJsonObject().getAsJsonObject("aggregations").toString());
        }

        return jsonObject;
    }

    /**
     * 获取某一字段的聚合结果
     *
     * @param aggregationResult
     * @param key
     * @return
     */
    public JSONArray getSearchAggregationResultByKey(JSONObject aggregationResult, String key) {
        return aggregationResult.getJSONObject(key).getJSONArray("buckets");
    }

    public JSONArray getDocumentsResultList(String index, String type, JSONObject params, JSONObject config) throws Exception {
        SearchSourceBuilder searchSourceBuilder = ZWSearchParser.searchSourceBuilder(config, params);
        JestResult jestResult = getJestResult(searchSourceBuilder, index, type, null);
        return parseDocumentResult(jestResult);
    }

    /**
     * 获取聚合结果
     *
     * @param index  索引
     * @param type   类型
     * @param params 参数
     * @param config 配置
     * @return
     * @throws Exception
     */
    public JSONObject getAggregationsResult(String index, String type, JSONObject params, JSONObject config) throws Exception {
        SearchSourceBuilder searchSourceBuilder = ZWSearchParser.searchSourceBuilder(config, params);
        JSONObject searchAggregationResult = getSearchAggregationResult(searchSourceBuilder, index, type, null);
        JSONArray aggsJSON = config.getJSONArray("aggregations");

        return parseTermsAggregationsResult(searchAggregationResult, aggsJSON);
    }

    /**
     * parseTermsAggregationsResult
     *
     * @param searchAggregationResult
     * @param aggsJSON
     * @return
     */
    public JSONObject parseTermsAggregationsResult(JSONObject searchAggregationResult, JSONArray aggsJSON) {
        for (int i = 0; i < aggsJSON.size(); i++) {
            JSONObject item = aggsJSON.getJSONObject(i);
            JSONArray arr = getSearchAggregationResultByKey(searchAggregationResult, item.getString("name"));
            searchAggregationResult.put(item.getString("name"), arr);

            if (item.getJSONArray("sub_aggregations") != null) {
                for (int j = 0; j < arr.size(); j++) {
                    JSONObject subItem = arr.getJSONObject(j);
                    parseTermsAggregationsResult(subItem, item.getJSONArray("sub_aggregations"));
                }
            }
        }

        return searchAggregationResult;
    }

    public JSONObject getAggregations(JestResult jestResult) {
        JSONObject result = new JSONObject();
        JsonObject aggregations = jestResult.getJsonObject().getAsJsonObject("aggregations");
        if (aggregations != null) {
            result = JSON.parseObject(result.toString());
        }

        return result;
    }

    public JSONObject getSearchResult(String data, String config) {
        JSONObject dataJson = JSON.parseObject(data);
        JSONObject configJSON = JSON.parseObject(config);
        return getSearchResult(dataJson, configJSON);
    }

    public JSONObject getSearchResult(JSONObject dataJson, JSONObject configJSON) {
        return getSearchResult(configJSON.getString("index"), configJSON.getString("type"), dataJson, configJSON);
    }

    public JSONObject getSearchResult(String index, String type, JSONObject dataJson, JSONObject configJSON) {
        JSONObject result = new JSONObject();
        try {
            SearchSourceBuilder searchSourceBuilder = ZWSearchParser.searchSourceBuilder(configJSON, dataJson);

            JestResult jestResult = getJestResult(searchSourceBuilder, index, type, null);
            if (jestResult.isSucceeded()) {
                if (configJSON.getJSONArray("aggregations") != null) {
                    result.put("aggs", parseTermsAggregationsResult(getAggregations(jestResult), configJSON.getJSONArray("aggregations")));
                }

                result.put("item", parseDocumentResult(jestResult));
                result.put("totalNumber", jestResult.getJsonObject().get("hits").getAsJsonObject().get("total").getAsInt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * get searchsource builder
     *
     * @param size
     * @param offset
     * @return
     */
    public SearchSourceBuilder getSearchSourceBuilder(Integer size, Integer offset) {
        return ZWQueryBuilder.searchSourceBuilder(size, offset);
    }

    public JSONArray parseDocumentResult(JestResult jestResult) {
        JSONArray resultArray = new JSONArray();

        if (jestResult.isSucceeded() && jestResult.getJsonObject().get("hits") != null) {
            JSONArray json = JSON.parseArray(jestResult.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits").toString());
            for (int i = 0; i < json.size(); i++) {
                resultArray.add(json.getJSONObject(i).getJSONObject("_source"));
            }
        }

        return resultArray;
    }

    public JSONObject getById(String index, String type, String id) {
        JSONObject result = new JSONObject();
        Get get = new Get.Builder(index, id).type(type).build();

        try {
            JestResult jestResult = client.execute(get);
            if (jestResult.isSucceeded()) {
                return JSON.parseObject(jestResult.getJsonObject().getAsJsonObject("_source").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //log.error(e.getStackTrace());
        }

        return result;
    }

    /**
     * 向Es里面写数据
     *
     * @param index
     * @param type
     * @param message
     * @return
     */
    public boolean writeDataToES(String index, String type, String message) {
        try {
            Bulk.Builder bulkIndexBuilder = new Bulk.Builder();

            JSONObject document = JSON.parseObject(message);
            bulkIndexBuilder.addAction(new Index.Builder(document).index(index).type(type).id(document.getString("id")).build());

            JestResult jestResult = client.execute(bulkIndexBuilder.build());
            if (jestResult.isSucceeded()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    /**
     * 删除es中的指定id的数据
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    public boolean deleteESDataById(String index, String type, String id) {
        try {
            JestResult jestResult = client.execute(new Delete.Builder(id).index(index).type(type).build());
            if (jestResult.isSucceeded()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }


    /**
     * 更新es中指定id的数据
     *
     * @param index
     * @param type
     * @param id
     * @param document
     * @return
     */
    public boolean updateEsDataByIdByScript(String index, String type, String id, String document) {
        try {
            //更新内容
            String script = "{" +
                    "\"script\" : \"ctx._source.content = tag\"," +
                    "\"params\" : {" +
                    "\"tag\" : \"" + document + "\"" +
                    "}}";

            JestResult jestResult = client.execute(new Update.Builder(script).index(index).type(type).id(id).build());
            if (!jestResult.isSucceeded()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 更新es中指定id的数据
     *
     * @param index
     * @param type
     * @param id
     * @param document
     * @return
     */
    public boolean updateEsDataById(String index, String type, String id, String document) {
        try {
            String doc = "{ \"doc\":" + document + "}";
            JestResult jestResult = client.execute(new Update.Builder(doc).index(index).type(type).id(id).build());
            if (!jestResult.isSucceeded()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean writeDataToESWithBulk(List<JSONObject> list) {
        try {
            Bulk.Builder bulkIndexBuilder = new Bulk.Builder();

            for (JSONObject item : list) {
                bulkIndexBuilder.addAction(new Index.Builder(JSON.toJSONString(item.getJSONObject("document")))
                        .index(item.getString("_index"))
                        .type(item.getString("_type"))
                        .id(item.getString("_id"))
                        .build());
            }

            try {
                if (list.size() > 0) {
                    JestResult jestResult = client.execute(bulkIndexBuilder.build());
                    if (jestResult.isSucceeded()) {
                        System.out.println("写入es成功" + list.size());
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 获取结果列表
     *
     * @param data
     * @param config
     * @return
     */
    public String getDocumentsResultList(String data, String config) {
        return getDocumentsResultListObj(data, config).toString();
    }

    /**
     * 获取结果列表
     *
     * @param data
     * @param config
     * @return
     */
    public JSONArray getDocumentsResultListObj(String data, String config) {
        JSONObject tagsJSON = JSON.parseObject(data);
        JSONObject configJSON = JSON.parseObject(config);
        JSONArray result = new JSONArray();

        try {
            result = getDocumentsResultList(configJSON.getString("index"), configJSON.getString("type"), tagsJSON, configJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取聚合结果
     *
     * @param data
     * @param config
     * @return
     */
    public String getAggregationResult(String data, String config) {
        return getAggregationResultObj(data, config).toString();
    }

    public JSONObject getAggregationResultObj(String data, String config) {
        JSONObject dataJson = JSON.parseObject(data);
        JSONObject configJSON = JSON.parseObject(config);
        JSONObject result = new JSONObject();

        try {
            result = getAggregationsResult(configJSON.getString("index"), configJSON.getString("type"), dataJson, configJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getSearchResultWithAggr(String data, String config) {
        return getSearchResultWithAggrObj(data, config).toString();
    }

    public JSONObject getSearchResultWithAggrObj(String data, String config) {
        JSONObject dataJson = JSON.parseObject(data);
        JSONObject configJSON = JSON.parseObject(config);
        JSONObject result = new JSONObject();

        try {
            result = getSearchResult(configJSON.getString("index"), configJSON.getString("type"), dataJson, configJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public JsonObject getSearchResultJsonObject(SearchSourceBuilder searchSourceBuilder, String index, String type) {
        JestResult jestResult = getJestResult(searchSourceBuilder, index, type, null);
        if (!jestResult.isSucceeded()) {
            throw new RuntimeException(jestResult.getErrorMessage());
        }

        return jestResult.getJsonObject();
    }
}