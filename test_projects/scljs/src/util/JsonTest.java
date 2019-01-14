

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonTest {
    public static void main(String[] args) {
        String json = "{'name':'JTZen9','age':'21'}";
        JsonTest jsonTest = new JsonTest();
        jsonTest.tt(json);
    }

    public void tt(String json){
        JSONObject jsonObject  = new JSONObject(json);
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()){
            String key = iterator.next().toString();
            if ("来源表配置".equals(key)){
                jsonObject.getJSONObject(key);
            }else if ("输出表配置".equals(key)){
                jsonObject.getJSONObject(key);
            }else if ("输出表配置".equals(key)){
                jsonObject.getJSONObject(key);
            }
            JSONArray value = jsonObject.getJSONArray(key);
            System.out.println(value);
        }
    }
}
