package bupt.edu.cn.web.controller;

import bupt.edu.cn.kylin.service.KylinQueryService;
import bupt.edu.cn.kylin.service.impl.KylinQueryServiceImpl;
import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.FaltTable;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.FaltTableRepository;
import bupt.edu.cn.web.util.SQLGenerate;
import com.peaceful.auth.sdk.spring.AUTH;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KylinController {

    @Autowired
    KylinQueryService kqs;
    @Autowired
    FaltTableRepository ftr;
    @Autowired
    DataSourceRepository dsr;


    /**
     * 创建宽表，并向数据源注册
     * @return
     */
    @AUTH.RequireLogin
    @AUTH.Role({"数据分析师","超级管理员"})
    @RequestMapping("/createFaltTable")
    public ReturnModel createFaltTable(){
        System.out.println("----------------createFaltTable--------------");

        ReturnModel result = new ReturnModel();
        //获取cubes
        kqs.login("ADMIN","KYLIN");
        String cubeList = kqs.listCubes(0,50000,"","");
        System.out.println(cubeList);

        //将jsonString 解析为json
        JSONArray cubeJsonArray;
        try {
            cubeJsonArray = new JSONArray(cubeList);//先将对象转成json数组
            result.setReason("成功");
            result.setResult(true);
        } catch (Exception e) {
            //字符串不能解析
            result.setReason("解析失败,宽表创建失败");
            result.setResult(false);
            return result;
        }
        for(int i = 0; i < cubeJsonArray.length();i++){
            JSONObject job = cubeJsonArray.getJSONObject(i);
            if (job.getString("status").equals("READY")){
                //取出 model 名称和 project 名称
                String model = job.getString("model");
                String project = job.getString("project");

                //将name复制为 model 名称
                String name = model;

                //获取model信息
                String dataModel = kqs.getDataModel(job.getString("model"));
                System.out.println(dataModel);

                //得到model/宽表的对应sql 和 tables
                SQLGenerate sg = new SQLGenerate();
                String[] sqlANDtab = sg.getFaltTableSql(dataModel);
                String sql = sqlANDtab[0];
                String tables = sqlANDtab[1];


                //更新数据库(falt_table)
                List<FaltTable> faltTables = ftr.findByModelAndProject(model,project);
                if (faltTables.size() == 0){
                    FaltTable faltTable = new FaltTable();
                    faltTable.setModel(model);
                    faltTable.setProject(project);
                    faltTable.setTableSql(sql);
                    faltTable.setName(name);
                    faltTable.setTables(tables);
                    ftr.saveAndFlush(faltTable);
                }else if (faltTables.size() == 1){
                    FaltTable faltTable = faltTables.get(0);
                    faltTable.setModel(model);
                    faltTable.setProject(project);
                    faltTable.setTableSql(sql);
                    faltTable.setName(name);
                    faltTable.setTables(tables);
                    ftr.saveAndFlush(faltTable);
                }else {
                    result.setReason("数据库中已多个存在重复model,宽表创建失败，请检查数据库");
                    result.setResult(false);
                    return result;
                }

                //向数据源注册/更新宽表（dataSource）
                List<DataSource> dataSources = dsr.findByFileNameEquals(name);
                if (dataSources.size() == 0){
                    DataSource dataSource = new DataSource();
                    dataSource.setFileName(name);
                    dataSource.setFileUrl(sql);
                    dataSource.setFileType("FALT");
                    dsr.saveAndFlush(dataSource);
                }else if (dataSources.size() == 1){
                    DataSource dataSource = dataSources.get(0);
                    dataSource.setFileUrl(sql);
                    dsr.saveAndFlush(dataSource);
                }else {
                    result.setReason("数据库中存在重复name的数据源,宽表注册失败，请检查数据库");
                    result.setResult(false);
                    return result;
                }

            }
        }

        return result;
    }

}
