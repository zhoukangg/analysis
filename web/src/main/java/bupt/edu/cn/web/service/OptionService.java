package bupt.edu.cn.web.service;

import bupt.edu.cn.kylin.service.KylinQueryService;
import bupt.edu.cn.kylin.service.impl.KylinQueryServiceImpl;
import bupt.edu.cn.web.chartsmodel.*;
import bupt.edu.cn.web.chartsmodel.radar.*;
import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.kylinModel.ColumnMeta;
import bupt.edu.cn.web.kylinModel.KylinSelectResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OptionService {

    @Autowired
    KylinQueryService kqss;

    public JSONObject createOptionSpark(String[] dims,List<String> meas, List<Map> listJson){
        System.out.println("--------------------------");
        System.out.println(JSON.toJSONString(listJson));
        //返回结果
        JSONObject result = new JSONObject();
        //当维度和度量都是一维的时候，默认使用面积图option
        if (dims.length == 1 && meas.size() <= 1){
            //定义option的结构
            ChartModelOne chartModelOne = new ChartModelOne();
//        JSONObject option = new JSONObject();
//        YAxis yAxis = new YAxis();
//        XAxis xAxis = new XAxis();
//        Legend legend = new Legend();
            Serial serial = new Serial();
//        List<Serial> serials = new ArrayList<>();
//        ToolTips toolTips = new ToolTips();
//        JSONObject title = new JSONObject();
//        JSONObject grid = new JSONObject();
            //设置yAxis 和 xAxis
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            }else {
                chartModelOne.yAxis.name = meas.get(0);
            }
            chartModelOne.yAxis.type = "value";
            chartModelOne.xAxis.name = dims[0];
            chartModelOne.xAxis.type = "category";
            for (int i = 0;i<listJson.size();i++){
                String dim = "";
                for (int j = 0;j<dims.length;j++){
                    dim = dim + listJson.get(i).get(dims[0]).toString();
                }
                chartModelOne.xAxis.data.add(listJson.get(i).get(dims[0]).toString());
            }
            chartModelOne.xAxis.boundaryGap=false;
            //设置legend
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            }else {
                String le =  meas.get(0);
                chartModelOne.legend.data.add(le);
            }
            //设置serials
            serial.type = "line";
            serial.stack = "stack";
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            }else {
                serial.name =  meas.get(0);
                for (int i = 0;i<listJson.size();i++){
                    serial.data.add(listJson.get(i).get(meas.get(0)).toString());
                }
                chartModelOne.series.add(serial);
            }
            //设置toolTips

            //设置title
            if (meas.size() == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
            }else {
                chartModelOne.title.put("text",(meas.get(0)));
            }
            chartModelOne.title.put("left","center");
            //设置grid
            chartModelOne.grid.put("y","8");

            //返回结果
//        option.put("yAxis",yAxis);
//        option.put("xAxis",xAxis);
//        option.put("legend",legend);
//        option.put("series",serials);
//        option.put("tooltip",toolTips);
//        option.put("title",title);
//        option.put("grid",grid);
            result = (JSONObject)JSON.toJSON(chartModelOne);
        }else if (dims.length == 1 && meas.size() > 1){ //雷达图作为默认option
            ChartModelRadar chartModelRadar = new ChartModelRadar();
//            chartModelRadar.title.text = optionName;
            chartModelRadar.legend.data = meas;

            for (int i = 0;i<listJson.size();i++){
                double max_d = -1000000;
                for (int j = 0;j<meas.size();j++){
                    double dou = Double.valueOf(listJson.get(i).get(meas.get(j)).toString());
                    if (max_d < dou){
                        max_d = dou;
                    }
                }
                chartModelRadar.radar.indicator.add(new Indicator(listJson.get(i).get(dims[0]).toString(),Double.toString(max_d *1.2)));
            }
            String seriesName = meas.get(0);
            for (int j = 1;j<meas.size();j++){
                seriesName = seriesName+"VS"+meas.get(j);
            }
            SeriesRadar seriesRadar = new SeriesRadar(seriesName,"radar");

            for (int i = 0;i<meas.size();i++){
                List<String> dataMeas = new ArrayList<>();
                for (int j= 0;j<listJson.size();j++){
                    dataMeas.add(listJson.get(j).get(meas.get(i)).toString());
                    System.out.println(listJson.get(j).get(meas.get(i)).toString());
                }
                seriesRadar.data.add(new DataRadar(dataMeas,meas.get(i)));
            }
            chartModelRadar.series.add(seriesRadar);
            result = (JSONObject)JSON.toJSON(chartModelRadar);
        }
        return result;
    }

//    public JSONObject createOptionSpark(String[] dims, String[] meas, List<Map> listJson){
//
//        //定义option的结构
//        JSONObject option = new JSONObject();
//        YAxis yAxis = new YAxis();
//        XAxis xAxis = new XAxis();
//        Legend legend = new Legend();
//        Serial serial = new Serial();
//        List<Serial> serials = new ArrayList<>();
//        ToolTips toolTips = new ToolTips();
//        JSONObject title = new JSONObject();
//        JSONObject grid = new JSONObject();
//        //设置yAxis 和 xAxis
//        if (meas.length == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
//        }else {
//            yAxis.name = meas[0];
//        }
//        yAxis.type = "value";
//        xAxis.name = dims[0];
//        xAxis.type = "category";
//        for (int i = 0;i<listJson.size();i++){
//            String dim = "";
//            for (int j = 0;j<dims.length;j++){
//                dim = dim + listJson.get(i).get(dims[0]).toString();
//            }
//            xAxis.data.add(listJson.get(i).get(dims[0]).toString());
//        }
//        xAxis.boundaryGap=true;
//        //设置legend
//        if (meas.length == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
//        }else {
//            String le =  meas[0];
//            legend.data.add(le);
//        }
//        //设置serials
//        serial.type = "line";
//        serial.stack = "stack";
//        if (meas.length == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
//        }else {
//            serial.name =  meas[0];
//            for (int i = 0;i<listJson.size();i++){
//                serial.data.add(listJson.get(i).get(meas[0]).toString());
//            }
//            serials.add(serial);
//        }
//        //设置toolTips
//
//        //设置title
//        if (meas.length == 0 || meas.toString().equals("") || meas.toString().equals(" ")) {
//        }else {
//            title.put("text",(meas[0]));
//        }
//        title.put("left","center");
//        //设置grid
//        grid.put("y",8);
//
//        //返回结果
//        option.put("yAxis",yAxis);
//        option.put("xAxis",xAxis);
//        option.put("legend",legend);
//        option.put("series",serials);
//        option.put("tooltip",toolTips);
//        option.put("title",title);
//        option.put("grid",grid);
//        return option;
//    }

    public ReturnModel createOption(String[] dims, String[] meas){

        String sql = "select PART_DT,LEAF_CATEG_ID from  KYLIN_SALES";
        String body = "{\"sql\":\""+ sql +"\",\"offset\":0,\"limit\":50000,\"acceptPartial\":false,\"project\":\"learn_kylin\"}";
        ReturnModel result = new ReturnModel();
        String output="";
        kqss.login("ADMIN","KYLIN");
        output = kqss.query(body);
//        //查询
//        try{
//
//            System.out.println(output);
//        }catch (Exception e){
////            查询失败
//            result.setReason("查询失败");
//            result.setResult(false);
//            return result;
//        }

        KylinSelectResult kylinSelectResult =  JSON.parseObject(output,new TypeReference<KylinSelectResult>(){});
        List<ColumnMeta> columnMetaList = kylinSelectResult.getColumnMetas();
        List<List<String>> resultList = kylinSelectResult.getResults();
        //定义option的结构
        JSONObject option = new JSONObject();
        YAxis yAxis = new YAxis();
        XAxis xAxis = new XAxis();
        Legend legend = new Legend();
        Serial serial = new Serial();
        List<Serial> serials = new ArrayList<>();
        ToolTips toolTips = new ToolTips();
        JSONObject title = new JSONObject();
        JSONObject grid = new JSONObject();
        //设置yAxis 和 xAxis
        yAxis.name = columnMetaList.get(0).getName();
        yAxis.type = "value";
        xAxis.name = columnMetaList.get(1).getName();
        xAxis.type = "category";
        System.out.println(resultList.size());
        System.out.println(resultList.get(0).get(0));
        for (int i = 0;i<resultList.size();i++){
            xAxis.data.add(resultList.get(i).get(1));
        }
        xAxis.boundaryGap=true;
        //设置legend
        String le = columnMetaList.get(0).getName();
        legend.data.add(le);
        //设置serials
        serial.type = "line";
        serial.name = columnMetaList.get(0).getName();
        serial.stack = "stack";
        for (int i = 0;i<resultList.size();i++){
            serial.data.add(resultList.get(i).get(0));
        }
        serials.add(serial);
        //设置toolTips

        //设置title
        title.put("text",columnMetaList.get(0).getName());
        title.put("left","center");
        //设置grid
        grid.put("y",8);

        //返回结果
        option.put("yAxis",yAxis);
        option.put("xAxis",xAxis);
        option.put("legend",legend);
        option.put("series",serials);
        option.put("tooltip",toolTips);
        option.put("title",title);
        option.put("grid",grid);
        result.setDatum(option);
        return result;
    }
}
