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
        String dataStr = "{\n" +
                "\t\"pageSize\":10,\n" +
                "\t\"from\": 10,\n" +
                "\t\"id\": \"1\",\n" +
                "\t\"date_time\":\"[2017-06-01,]\",\n" +
                "\t\"is_success\":1,\n" +
                "\t\"name\":\"Bob\"\n" +
                "}";

        JSONObject data = JSON.parseObject(dataStr);
        String config = FileUtil.readResourceRemoveComments(ElasticsearchParser.class, "test.json");
        SearchSourceBuilder searchSourceBuilder = ZWSearchParser.searchSourceBuilder(JSON.parseObject(config), data);
        System.out.println(searchSourceBuilder.toString());
    }
}
