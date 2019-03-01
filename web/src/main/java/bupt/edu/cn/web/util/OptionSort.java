package bupt.edu.cn.web.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


// 对option进行排序
public class OptionSort {
    // 二维排序，基于冒泡排序思想
    // 数组data第一行为主要排序数据，第二行为次要排序数据，第三行为标记
    private static double[][] two_dimension_sort(double[][] data, String sort_method){
        // 第一次排序，基于第一行数据
        for(int i = 0; i < data[0].length - 1; i++){
            boolean flag = false;
            for(int j = 0; j < data[0].length - 1 - i; j++){
                if(data[0][j] > data[0][j+1]){
                    flag = true;
                    // 同时交换三行对应数据
                    for(int k = 0; k < 3; k++){
                        double temp = data[k][j];
                        data[k][j] = data[k][j+1];
                        data[k][j+1] = temp;
                    }
                }
            }
            // 一趟比较未发生交换时，排序结束
            if(!flag){
                break;
            }
        }
        // 第二次排序，基于第二行数据（只在第一行对应位置相等时发生交换）
        for(int i = 0; i < data[0].length-1;){
            // 比较第一行中的元素是否相等，[left, right)为第一行元素相等的区间
            int left = i;
            int right = i + 1;
            // 循环找出区间
            while(data[0][left] == data[0][right]){
                right++;
            }
            // 对区间[left, right)进行基于第二行数据的排序，小冒泡
            if(left < right - 1){
                for(int p = left; p < right - 1; p++){
                    boolean flag = false;
                    for(int j = left; j < right - 1 - (p - left); j++){
                        if(data[1][j] > data[1][j+1]){
                            flag = true;
                            // 同时交换第二行和第三行对应数据
                            for(int k = 1; k < 3; k++){
                                double temp = data[k][j];
                                data[k][j] = data[k][j+1];
                                data[k][j+1] = temp;
                            }
                        }
                    }
                    // 一趟比较未发生交换时，排序结束
                    if(!flag){
                        break;
                    }
                }
            }
            i = right;
        }
        // 若sort_method为Descending，对数组进行反序处理
        if(sort_method.equals("Descending")){
            for(int i = 0; i < 3; i++){
                reverse(data[i]);
            }
        }
        return data;
    }

    // 反序函数，用于Desceding时数组反序
    private static void reverse(double[] array){
        for(int i = 0; i < array.length/2; i++){
            double temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    // 对option（JSONObject）进行解析并排序
    // 参数key1和json不能为空，key2和sort_method可以为空，默认：key2="",sort_method="Ascending"
    public static JSONObject optionSort(String key1,String key2, String sort_method, JSONObject option){
        // 提取series下的JSONArray
        JSONArray jsay_series = option.getJSONArray("series");
        int key1_num = 0;                                   // key1、key2所在JSONObject位于JSONArray中的下标
        int key2_num = 0;
        JSONObject json_key1 = new JSONObject();            // key1、key2所在的JSONObject
        JSONObject json_key2 = new JSONObject();
        JSONArray json_data_key1 = new JSONArray();         // key1、key2所对应的data数组，格式为JSONArray
        JSONArray json_data_key2 = new JSONArray();
        // 遍历该JSONArray，找到key1、key2所在的JSONObject，提取数据和下标
        for(int i = 0; i < jsay_series.size(); i++){
            JSONObject json_temp = jsay_series.getJSONObject(i);
            if(json_temp.get("name").equals(key1)){
                json_data_key1 = json_temp.getJSONArray("data");
                json_key1 = json_temp;
                key1_num = i;
            }
            else if(!key2.isEmpty()){
                if(json_temp.get("name").equals(key2)){
                    json_data_key2 = json_temp.getJSONArray("data");
                    json_key2 = json_temp;
                    key2_num = i;
                }
            }
        }
        // 数组data有三行，第一行为第一维待排序数据，第二行为第二维待排序数据，第三行为顺序标记（方便后面对String数组进行排序）
        // 数组data使用double型保持精度，若有需要再转回int型
        double[][] data = new double[3][json_data_key1.size()];
        // 存入数据
        for(int i = 0; i < json_data_key1.size(); i++){
            data[0][i] = Double.parseDouble(json_data_key1.get(i).toString());
            if(!key2.isEmpty()){
                data[1][i] = Double.parseDouble(json_data_key2.get(i).toString());
            }
            data[2][i] = i;
        }
        // cate_flag标志cates是在x轴还是y轴，默认x轴，如在y轴则进行转换
        String cates_flag = "xAxis";
        JSONObject json_xAxis = new JSONObject();
        JSONObject json_yAxis = new JSONObject();
        JSONArray jsay_cates = new JSONArray();
        if(option.getJSONObject("yAxis").get("type").equals("category")){
            cates_flag = "yAxis";
            json_yAxis = option.getJSONObject("yAxis");
            jsay_cates = json_yAxis.getJSONArray("data");
        }
        else{
            json_xAxis = option.getJSONObject("xAxis");
            jsay_cates = json_xAxis.getJSONArray("data");
        }
        String[] cates = new String[jsay_cates.size()];
        for(int i = 0; i < jsay_cates.size(); i++){
            cates[i] = jsay_cates.get(i).toString();
        }
        // 调用two_dimension_sort()方法排序
        double[][] data_sorted = two_dimension_sort(data, sort_method);
        String[] cates_sorted = new String[jsay_cates.size()];
        // 把cates中的string按照data[2]中记录的顺序放入cates_sorted中
        for(int i = 0; i < data_sorted[2].length; i++){
            cates_sorted[i] = cates[(int) data[2][i]];
        }
        // 将更改后的json逐级放回
        // 若原数据不带小数点，则需转回不带小数点的形式
        if(!(""+json_data_key1.get(0)).contains(".")){
            int[] data1_int = new int[data_sorted[0].length];
            for(int i = 0; i < data_sorted[0].length; i++){
                data1_int[i] = (int)data_sorted[0][i];
            }
            json_key1.put("data", data1_int);
        }
        else{
            json_key1.put("data", data_sorted[0]);
        }
        if(!key2.isEmpty()){
            if(!(""+json_data_key2.get(0)).contains(".")){
                int[] data2_int = new int[data_sorted[1].length];
                for(int i = 0; i < data_sorted[1].length; i++){
                    data2_int[i] = (int)data_sorted[1][i];
                }
                json_key2.put("data", data2_int);
            }
            else{
                json_key2.put("data", data_sorted[1]);
            }
            jsay_series.remove(key2_num);
        }
        jsay_series.remove(key1_num);
        jsay_series.add(json_key1);
        if(!key2.isEmpty()){
            jsay_series.add(json_key2);
        }
        option.put("series", jsay_series);
        if(cates_flag.equals("yAxis")){
            json_yAxis.put("data", cates_sorted);
            option.put("yAxis", json_yAxis);
        }
        else{
            json_xAxis.put("data", cates_sorted);
            option.put("xAxis", json_xAxis);
        }
        return option;
    }
}
