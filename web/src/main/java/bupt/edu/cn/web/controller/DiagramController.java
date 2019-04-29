package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.chartsmodel.*;
import bupt.edu.cn.web.chartsmodel.radar.ChartModelRadar;
import bupt.edu.cn.web.chartsmodel.radar.DataRadar;
import bupt.edu.cn.web.chartsmodel.radar.Indicator;
import bupt.edu.cn.web.chartsmodel.radar.SeriesRadar;
import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.repository.DiagramRepository;
import bupt.edu.cn.web.service.DiagramService;
import bupt.edu.cn.web.service.OptionService;
import bupt.edu.cn.web.service.ReturnService;
import bupt.edu.cn.web.util.OptionSort;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cn.bupt.cad.bigdataroles.annotation.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class DiagramController {

    @Autowired
    DiagramRepository diagramRepository;
    @Autowired
    DiagramService diagramService;
    @Autowired
    OptionService optionService;
    @Autowired
    ReturnService returnService;

    /**
     * 更新Diagram
     * @param diagramId
     * @param diagramName
     * @param diagramType 等同classificaion 0：柱状图，2:条形图
     * @param userId
     * @param response
     * @param request
     * @return
     */
    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping("/updateDiagram")
    public ReturnModel updateDiagram(String diagramId, String diagramName, String diagramType, String userId, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        ReturnModel result = new ReturnModel();
        System.out.println("----------------updateDiagram-----------");
        System.out.println("diagramId = "+diagramId);
        System.out.println("diagramName = "+diagramName);
        System.out.println("diagramType = "+diagramType);
        System.out.println("userId = "+userId);

        //根据id查找Diagram
        //java8 Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。
        Optional<Diagram> diagram;
        diagram = diagramRepository.findById(Long.valueOf(diagramId));
        //判断是否存在，新增/更新
        Diagram newDiagram;
        if (diagram.isPresent()){
            newDiagram = diagram.get();
        }else {
            result.setReason("找不到该option");
            return result;
        }
        //获取option
        String ch = newDiagram.getChart();
        //获取转换前option的类型
        System.out.println("ch: "+ch);
        String serieBefore = JSON.parseObject(ch).getJSONArray("series").get(0).toString();
        String typeBefore = JSON.parseObject(serieBefore).getString("type");
        System.out.println("typeBefore: "+typeBefore);

        //转换 情况1、option 类型保持不变 ；
        if ((typeBefore.equals("line") || typeBefore.equals("bar")) && (diagramType.equals("0") || diagramType.equals("2"))){
            //更新
            ChartModelOne chartModelOne = JSON.parseObject(ch,new TypeReference<ChartModelOne>() {});
            System.out.println(JSON.toJSONString(chartModelOne));
            if (diagramType.equals("0")){
                chartModelOne.series.get(0).setType("bar");
                chartModelOne.xAxis.boundaryGap = true;
            } else if (diagramType.equals("2")){
                chartModelOne.series.get(0).setType("line");
                chartModelOne.xAxis.boundaryGap = false;
            }

            result = returnService.returnDiagram(result, diagramId, diagramName, JSON.toJSONString(chartModelOne), diagramType, userId);
            return result;
        }else if ((typeBefore.equals("pie") && diagramType.equals("3"))){//暂定3
            ChartModelPie chartModelPie = JSON.parseObject(ch,new TypeReference<ChartModelPie>() {});
            System.out.println(JSON.toJSONString(chartModelPie));
            //待修改更新

            result = returnService.returnDiagram(result, diagramId, diagramName, JSON.toJSONString(chartModelPie), diagramType, userId);
            return result;

        }else if ((typeBefore.equals("radar") && diagramType.equals("4"))){//暂定4
            ChartModelRadar chartModelRadar = JSON.parseObject(ch,new TypeReference<ChartModelRadar>() {});
            System.out.println(JSON.toJSONString(chartModelRadar));
            //待修改更新

            result = returnService.returnDiagram(result, diagramId, diagramName, JSON.toJSONString(chartModelRadar), diagramType, userId);
            return result;
        }

        //情况2、option 类型变换
        List<List> dataALL = new ArrayList<>();
        List<String> dataMeas = new ArrayList<>();
        List<String> dataDims = new ArrayList<>();
        String measName = "";
        String dimsName = "";
        String optionName = "";
        //解析option中的数值
        if (typeBefore.equals("line") || typeBefore.equals("bar"))
        {
            ChartModelOne chartModelOne = JSON.parseObject(ch,new TypeReference<ChartModelOne>() {});
            dataMeas = chartModelOne.series.get(0).data;
            dataDims = chartModelOne.xAxis.data;
            measName = chartModelOne.series.get(0).name;
            dimsName = chartModelOne.xAxis.name;
            optionName = measName+"/"+dimsName;

        }else if (typeBefore.equals("pie")){
            ChartModelPie chartModelPie = JSON.parseObject(ch,new TypeReference<ChartModelPie>() {});
            for (int i = 0;i< chartModelPie.series.get(0).data.size();i++){
                dataMeas.add(chartModelPie.series.get(0).data.get(i).value);
                dataDims.add(chartModelPie.series.get(0).data.get(i).name);
            }
            measName = "数值";
            dimsName = chartModelPie.series.get(0).name;
            optionName = chartModelPie.title.text;
        }else if (typeBefore.equals("radar")){
            ChartModelRadar chartModelRadar = JSON.parseObject(ch,new TypeReference<ChartModelRadar>() {});
            for (int i = 0;i< chartModelRadar.radar.indicator.size();i++){
                dataDims.add(chartModelRadar.radar.indicator.get(i).name);
            }
            dataMeas = chartModelRadar.series.get(0).data.get(0).value;
            optionName = chartModelRadar.title.text;
            //
            dimsName = "unset";
            //只取第一个度量（丢失其它度量）
            measName = chartModelRadar.series.get(0).data.get(0).name;

        }
        //生成目标option
        if (diagramType.equals("0") || diagramType.equals("2")){
            ChartModelOne chartModelOne = new ChartModelOne();
            Serial serial = new Serial();
            serial.name = measName;
            serial.data = dataMeas;
            if (diagramType.equals("0")){
                serial.type ="bar";
            }else {
                serial.type ="line";
            }
            chartModelOne.series.add(serial);
            chartModelOne.xAxis.boundaryGap = false;
            chartModelOne.xAxis.data = dataDims;
            chartModelOne.xAxis.name = dimsName;
//            chartModelOne.se

            result = returnService.returnDiagram(result, diagramId, diagramName, JSON.toJSONString(chartModelOne), diagramType, userId);
            return result;
        }else if (diagramType.equals("3")){//暂定3
            ChartModelPie chartModelPie = new ChartModelPie();
            SeriesPie seriesPie = new SeriesPie();
            for (int i = 0; i< dataDims.size();i++){
                seriesPie.data.add(new DataPie(dataMeas.get(i), dataDims.get(i)));
            }
            seriesPie.name = dimsName;
            seriesPie.type="pie";
            chartModelPie.series.add(seriesPie);
            chartModelPie.legend.data = dataDims;
            chartModelPie.title.text = optionName;

            result = returnService.returnDiagram(result, diagramId, diagramName, JSON.toJSONString(chartModelPie), diagramType, userId);
            return result;

        }else if (diagramType.equals("4")){//暂定4
            ChartModelRadar chartModelRadar = new ChartModelRadar();
            chartModelRadar.title.text = optionName;
            chartModelRadar.legend.data.add(measName);
            for (int i = 0;i<dataDims.size();i++){
                chartModelRadar.radar.indicator.add(new Indicator(dataDims.get(i),Double.toString((Double.valueOf(dataMeas.get(i))) *1.2)));
            }
            SeriesRadar seriesRadar = new SeriesRadar(measName,"radar");
            seriesRadar.data.add(new DataRadar(dataMeas,measName));
            chartModelRadar.series.add(seriesRadar);
            result = returnService.returnDiagram(result, diagramId, diagramName, JSON.toJSONString(chartModelRadar), diagramType, userId);
            return result;
        }
        System.out.println(diagramType);
        System.out.println("3".equals(diagramType) ||"3"== diagramType);

        result.setResult(false);
        result.setDatum("尚未定义该类型的模版");
        return result;
    }

    /**
     * 根据diagramId修改saved状态
     * @param diagramId
     * @param diagramName
     * @param classificaion
     * @param userId
     * @param dataSourceId
     * @param response
     * @param request
     * @return
     */

    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping("/saveDiagram")
    public ReturnModel saveDiagram(String diagramId, String diagramName,String option, String classificaion, String userId, String dataSourceId, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------saveDiagram-----------");
        System.out.println("diagramId = "+diagramId);
        System.out.println("diagramName = "+diagramName);
        System.out.println("option = "+option);
        System.out.println("classificaion = "+classificaion);
        System.out.println("userId = "+userId);
        System.out.println("dataSourceId = "+dataSourceId);

        ReturnModel result = new ReturnModel();
        //修改saved字段为true
        Diagram newDiagram = diagramService.saveDiagram(diagramId,userId,diagramName,option);
        //判断是否修改成功
        if (!diagramService.isEmptyDiagram(newDiagram))
            result.setResult(true);
        else
            result.setResult(false);
        return result;
    }


    /**
     * 根据userId查找saved状态为true的Diagram
     * @param userId
     * @param response
     * @param request
     * @return
     */
    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping("/getDiagramByUserId")
    public ReturnModel getDiagramByUserId(String userId, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------------getDiagramByUserId-----------");
        System.out.println("userId = "+userId);
        //返回该用户的所有Diagram
        ReturnModel result = new ReturnModel();
        result.setResult(true);
        List<Diagram> diagramList = diagramRepository.findByUserIdAndSaved(userId,"true");
        //将option由JSONString改为JSON
        JSONArray jsonArray = new JSONArray();
        for (int i=0;i<diagramList.size();i++){
            JSONObject item =new JSONObject();
            item.put("option",JSON.parseObject(diagramList.get(i).getChart()));
            item.put("diagramId",diagramList.get(i).getId());
            item.put("diagramName",diagramList.get(i).getName());
            item.put("classificaion",diagramList.get(i).getClassification());
            item.put("userId",diagramList.get(i).getUserId());
            item.put("dataSourceId",diagramList.get(i).getDataSourceId());
            jsonArray.add(item);
        }
        result.setDatum(jsonArray);
        return result;
    }

    // option排序
    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping(value = {"/optionSort", "sort"}, method = RequestMethod.GET)
    public JSONObject optionSort(String userId, HttpServletResponse response, HttpServletRequest request){
        String key1 = request.getParameter("key1");
        String key2 = request.getParameter("key2");
        String sort_method = request.getParameter("sort_method");
        String str = request.getParameter("option");
        // 若未传入参数，则设定为空字符串
        if(key2 == null){
            key2 = "";
        }
        if(sort_method == null){
            sort_method = "";
        }
        // 读入json，为option的内容
        JSONObject option = JSONObject.parseObject(str);
        return OptionSort.optionSort(key1, key2, sort_method, option);
    }

}
