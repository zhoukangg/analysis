package bupt.edu.cn.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GenerateTable {
    public com.alibaba.fastjson.JSONArray generateCowJSON(String[] dimArr, String[] rowArr, List<Map> listJson) {
        List<String> cowList = new ArrayList<>(Arrays.asList(dimArr));
        List<String> rowList = new ArrayList<>(Arrays.asList(rowArr));
        cowList.removeAll(rowList);
        com.alibaba.fastjson.JSONArray cowJson = new com.alibaba.fastjson.JSONArray();
        for (int i = 0; i < listJson.size(); i++) {
            com.alibaba.fastjson.JSONArray now = cowJson;
            for (int j = 0; j < cowList.size(); j++) {
                String value;
                if (listJson.get(i).containsKey(cowList.get(j)))
                    value = listJson.get(i).get(cowList.get(j)).toString();
                else {
                    value = listJson.get(i).get(listJson.get(i).keySet().iterator().next().toString()).toString();
                }
                System.out.println(value);
                int flag = isExistInJSONArray(now, "name", value);
                if (flag == -1) {
                    com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
                    obj.put("name", value);
                    if (j != cowList.size() - 1) {
                        com.alibaba.fastjson.JSONArray last = new com.alibaba.fastjson.JSONArray();
                        obj.put("last", last);
                        now.add(obj);
                        now = last;
                    } else {
                        now.add(obj);
                    }
                } else {
                    now = now.getJSONObject(flag).getJSONArray("last");
                }
            }
        }
        return cowJson;
    }

    public com.alibaba.fastjson.JSONArray generateRowJSON(String[] dimArr, List<String> meaArr, List<String> funArr, String[] rowArr, List<Map> listJson) {
        //构造行结构和并填充数据
        List<String> cowList = new ArrayList<>(Arrays.asList(dimArr));
        List<String> rowList = new ArrayList<>(Arrays.asList(rowArr));
        List<String> mea_fun = new ArrayList<>();
        for (int i = 0; i < meaArr.size(); i++) {
            mea_fun.add(meaArr.get(i) + "_" + funArr.get(i));
        }
        com.alibaba.fastjson.JSONArray rowJson = new com.alibaba.fastjson.JSONArray();
        for (int i = 0; i < listJson.size(); i++) {
            com.alibaba.fastjson.JSONArray now = rowJson;
            //构造返回数据的key值
            String cowName;
            if (listJson.get(i).containsKey(cowList.get(0)))
                cowName = listJson.get(i).get(cowList.get(0)).toString();
            else {
                cowName = listJson.get(i).get(listJson.get(i).keySet().iterator().next().toString()).toString();
            }

            for (int j = 1; j < cowList.size(); j++) {
                cowName = cowName + "_" + listJson.get(i).get(cowList.get(j));
            }

            System.out.println("---------listJson.get(i).toString()------------");
            System.out.println(listJson.get(i).toString());
            for (int j = 0; j < rowArr.length; j++) {
                System.out.println(rowArr[j]);
                String value = listJson.get(i).get(rowArr[j]).toString();
                int flag = isExistInJSONArray(now, "name", value);
                if (flag == -1) {
                    com.alibaba.fastjson.JSONObject obj = new com.alibaba.fastjson.JSONObject();
                    obj.put("name", value);
                    if (j != rowArr.length - 1) {
                        com.alibaba.fastjson.JSONArray last = new com.alibaba.fastjson.JSONArray();
                        obj.put("last", last);
                        now.add(obj);
                        now = last;
                    } else {
                        com.alibaba.fastjson.JSONObject last = new com.alibaba.fastjson.JSONObject();
                        if (mea_fun.size() < 1) {
                            last.put(cowName, "");
                        } else {
                            last.put(cowName, listJson.get(i).get(mea_fun.get(0))); //目前只考虑一个度量
                        }
                        obj.put("value", last);
                        now.add(obj);
                    }
                } else {
                    if (j != rowArr.length - 1) {
                        now = now.getJSONObject(flag).getJSONArray("last");
                    } else {
                        com.alibaba.fastjson.JSONObject data = now.getJSONObject(flag).getJSONObject("value");
                        if (mea_fun.size() < 1) {
                            data.put(cowName, "");
                        } else {
                            data.put(cowName, listJson.get(i).get(mea_fun.get(0))); //目前只考虑一个度量
                        }
                    }
                }
            }
        }
        return rowJson;
    }

    private int isExistInJSONArray(com.alibaba.fastjson.JSONArray jsonArray,String name,String value){
        for(int i = 0;i<jsonArray.size();i++){
            if (jsonArray.getJSONObject(i).getString(name).equals(value))
                return i;
        }
        return -1;
    }

}
