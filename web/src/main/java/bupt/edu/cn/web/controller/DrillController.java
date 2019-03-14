package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.pojo.DrillDim;
import bupt.edu.cn.web.util.SQLGenerate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DrillController {

    @Autowired
    bupt.edu.cn.web.service.DrillService drillService;

    @Autowired
    bupt.edu.cn.web.service.DataTableInfoService dataTableInfoService;

    @Autowired
    bupt.edu.cn.spark.service.impl.SparkSqlServiceImpl sparkSqlService;

    @RequestMapping("/drillDimSet")
    public String drilloptionset(String tablename, String drilldims, String fileUrl){
        System.out.println("开始设置一个上卷下钻的维度值");
        JSONObject result = new JSONObject();
        DrillDim drillDim = drillService.createDrillDim(tablename,drilldims);
        Map newmap = dataTableInfoService.getCsvDim(fileUrl);

//        获取该表的所有维度，并对其进行建模计算
        String[] fileMeas = (String[]) newmap.get("meas");
        String[] drilldimArray = drilldims.split(",");

        String sql = "";
        SQLGenerate sqlGenerate = new SQLGenerate();
        sql = sqlGenerate.buildWithDrillDims(tablename,drilldimArray,fileMeas);

        System.out.println("开始生成模型");
        sparkSqlService.DrillFileOutput(fileUrl, tablename, sql, drillDim.getId());
        System.out.println("模型创建完成");

        result.put("tablename",tablename);
        result.put("meas",fileMeas);
        result.put("drillID",drillDim.getId());

        return "";
    }
}
