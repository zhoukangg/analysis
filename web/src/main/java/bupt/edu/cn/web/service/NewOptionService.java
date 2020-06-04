package bupt.edu.cn.web.service;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NewOptionService {


    /**
     * 根据数据 创建默认图表
     *
     * @param dims     维度
     * @param meas     度量
     * @param listJson 是series.data
     * @return
     */
    public JSONObject newCreateOption(String[] dims, List<String> meas, List<Map> listJson) {
        System.out.println("------------创建默认图表");
        System.out.println(JSON.toJSONString(dims));
        System.out.println(JSON.toJSONString(meas));
        System.out.println(JSON.toJSONString(listJson));
        JSONObject option = new JSONObject();
        String str_option = "";
        //当维度和度量都是一维的时候，默认使用面积图option
        if (dims.length == 1 && meas.size() <= 1) {
            //直接使用初始化的option，联系前端再加需要的属性即可
            str_option = "{'tooltip':{'trigger':'axis'},title:{text:'title',left:'center'},'legend':{'data':[],orient:'vertical','left':'left'},'xAxis':{'type':'category','axisLabel':{'rotate':'0','show':true},'boundaryGap':false,'data':['周一','周二','周三','周四','周五','周六','周日']},'yAxis':{'type':'value'},'color':[],'series':[{'name':'','stack':'stack','type':'line','smooth':true,'itemStyle':{'normal':{'areaStyle':{'type':'default'}}},'data':[]}]};";
            option = new JSONObject(str_option);
            //设置yAxis 和 xAxis
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            } else {
                option.getJSONObject("yAxis").put("name", meas.get(0));
            }
            List<String> xAxisData = new ArrayList<String>();
            String str = dims[0];
            if (listJson.get(0).containsKey(str)) {  //非上卷下钻的情况
                option.getJSONObject("xAxis").put("name", dims[0]);
                for (int i = 0; i < listJson.size(); i++)
                    xAxisData.add(listJson.get(i).get(dims[0]).toString());
            } else {        //上卷下钻时会出现listJson中没有dim的情况
                String columnName = "year";
                if (listJson.get(0).containsKey("month"))
                    columnName = "month";
                else if (listJson.get(0).containsKey("day"))
                    columnName = "day";
                else if (listJson.get(0).containsKey("season"))
                    columnName = "season";
                option.getJSONObject("xAxis").put("name", dims[0] + "_" + columnName);
                for (int i = 0; i < listJson.size(); i++)
                    xAxisData.add(listJson.get(i).get(columnName).toString());
            }

            option.getJSONObject("xAxis").put("data", xAxisData);
            //设置legend
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            } else {
                String le = meas.get(0);
                option.getJSONObject("legend").put("data", meas.get(0));
            }
            //设置serials
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            } else {
                option.getJSONArray("series").getJSONObject(0).put("name", meas.get(0));
                List<String> seriesData = new ArrayList<String>();
                for (int i = 0; i < listJson.size(); i++) {
                    seriesData.add(listJson.get(i).get(meas.get(0)).toString());
                }
                option.getJSONArray("series").getJSONObject(0).put("data", seriesData);
            }

            //设置title
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            } else {
                option.getJSONObject("title").put("text", meas.get(0));
            }
        } else if (dims.length == 1 && meas.size() > 1) { //雷达图作为默认option
            str_option = "{'title':{'text':'','left':'center'},'tooltip':{},'legend':{'data':[],'left':'left',orient:'vertical'},'color':[],'radar':{'name':{'textStyle':{'color':'#fff','backgroundColor':'#999','borderRadius':3,'padding':[3,5]}},'indicator':[]},'series':[{'name':'','type':'radar','data':[]}]}";
            option = new JSONObject(str_option);
            option.getJSONObject("legend").put("data", meas);
            for (int i = 0; i < listJson.size(); i++) {
                double max_d = -1000000;
                for (int j = 0; j < meas.size(); j++) {
                    double dou = Double.valueOf(listJson.get(i).get(meas.get(j)).toString());
                    if (max_d < dou) {
                        max_d = dou;
                    }
                }
                JSONObject tempjo = new JSONObject();
                tempjo.put("name", listJson.get(i).get(dims[0]).toString());
                tempjo.put("max", max_d * 1.2);
                option.getJSONObject("radar").getJSONArray("indicator").put(tempjo);
            }
            String seriesName = meas.get(0);
            for (int j = 1; j < meas.size(); j++) {
                seriesName = seriesName + "VS" + meas.get(j);
            }

            for (int i = 0; i < meas.size(); i++) {
                List<String> dataMeas = new ArrayList<>();
                for (int j = 0; j < listJson.size(); j++) {
                    dataMeas.add(listJson.get(j).get(meas.get(i)).toString());
                    System.out.println(listJson.get(j).get(meas.get(i)).toString());
                }
                JSONObject tempjo = new JSONObject();
                tempjo.put("value", dataMeas);
                tempjo.put("name", meas.get(i));
                option.getJSONArray("series").getJSONObject(0).getJSONArray("data").put(tempjo);
            }
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            } else {
                option.getJSONObject("title").put("text", meas.get(0));
            }
        } else if (dims.length == 0 && meas.size() == 1) {    //指标卡的特殊数据样式
            System.out.println("开始搭建指标卡的Option");
            System.out.println(listJson.get(0).get(meas.get(0)));
            double dbvalue = Double.valueOf(listJson.get(0).get(meas.get(0)).toString());
            double value = new BigDecimal(dbvalue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            option.put("value", value);
            option.put("name", meas.get(0));
        } else if (dims.length == 0 && meas.size() == 0) {
            option = new JSONObject();
        }
        return option;
    }
}
