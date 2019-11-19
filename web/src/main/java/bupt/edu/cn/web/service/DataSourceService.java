package bupt.edu.cn.web.service;

import bupt.edu.cn.kylin.service.KylinQueryService;
import bupt.edu.cn.web.conf.hiveConf;
import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.FaltTable;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.FaltTableRepository;
import bupt.edu.cn.web.util.SQLGenerate;
import com.opencsv.CSVReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class DataSourceService {

    @Autowired
    DataSourceRepository dataSourceRepository;
    @Autowired
    DataTableInfoService dataTableInfoService;
    @Autowired
    FaltTableRepository faltTableRepository;
    @Autowired
    HiveService hiveService;
    @Autowired
    KylinQueryService kqs;


    /**
     * 预览数据
     * @param fileName
     * @param fileUrl
     * @param fileType
     * @return
     */
    public List<Map> preview(String fileName, String fileUrl, String fileType){
        List<Map> result = new ArrayList<>();

        if (fileType.equals("HIVE")){

            try {
                result = hiveService.selectData(fileUrl.split("/")[0], "select * from "+ fileName+" limit 10");
            }catch (Exception e){
                System.out.println("hive select error:"+e.toString());
            }finally {
            }
        }else if (fileType.equals("CSV")){
            String[] nextLine={};
            String[] nameLine={};
            try {
                // 创建CSV读对象
                CSVReader reader = null;
                reader = new CSVReader(new FileReader(fileUrl));
                Iterator<String[]> iterator = reader.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    i++;
                    if (i == 1) {
                        nameLine = iterator.next();
                        for (int j = 0; j < nameLine.length; j++) {
                            if (nameLine[j] == null) {
                                nameLine[j] = "null";
                            }
                        }
                    }else if (i > 50) {
                        break;
                    }
                    nextLine = iterator.next();
                    Map<String, String> row = new HashMap<>();
                    for (int j = 0; j < nextLine.length; j++) {
                        if (nextLine[j] == null) {
                            nextLine[j] = "";
                        }
                        row.put(nameLine[j], nextLine[j]);
                    }
                    result.add(row);
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (fileType.equals("FALT")){
            try {
                String sqlSelect = fileUrl + " limit 10";
                result = hiveService.selectData(hiveConf.DATABASEURL, hiveConf.DEFAULTNAME, sqlSelect);
            }catch (Exception e){
                System.out.println("hive select error:"+e.toString());
            }

        }else {
            System.out.println("还为支持该种文件类型");
        }

//        转格式，
        JSONArray jsonArray = new JSONArray();
        for (int i = 0;i<result.size();i++){
            Iterator<String> keys = result.get(0).keySet().iterator();
            JSONObject jsonObject = new JSONObject();
            while (keys.hasNext()){
                String kerStr = keys.next();
//                System.out.println(kerStr);
//                System.out.println(result.get(i).get(kerStr));
                jsonObject.put(kerStr, result.get(i).get(kerStr));
            }
            jsonArray.put(jsonObject);
        }
        return result;
//        return jsonArray;
    }

    /**
     * 更新mysql中记录的hive、falt数据源
     * @return
     */
    public boolean updateDataSource(){

        /**
         *更新mysql中hive数据源
         */

        try {
            List<String> databaseNameList = hiveService.showDatabases();//hive 数据库名称
            String[] removeDatabases = {"sys","information_schema"};
            databaseNameList.removeAll(Arrays.asList(removeDatabases));
            //查出hive表
            List<String> tableNameList = new ArrayList<>();
            List<String> tableUrl = new ArrayList<>();//hive表url
            for (int j  = 0;j<databaseNameList.size();j++){
                String databaseNamej = databaseNameList.get(j);
                List<String> tableNamejList = hiveService.showTables(databaseNamej);
                tableNameList.addAll(tableNamejList);
                for (int i = 0; i<tableNamejList.size(); i++){
                    tableUrl.add(databaseNamej+"/"+tableNamejList.get(i));
                }
            }

            Set<String> setHiveTableUrl = new HashSet<>(tableUrl); //list转set
            //查出mysql hive表记录
            List<DataSource>  dataSources = dataSourceRepository.findByFileType("HIVE");
            Set<String> setMysqlTableUrl = new HashSet<>(); //list转set
            for (int i = 0;i<dataSources.size();i++){
                setMysqlTableUrl.add(dataSources.get(i).getFileUrl());
            }

            Set<String> setResult = new HashSet<String>();
            //要删除的记录（差集）
            setResult.clear();
            setResult.addAll(setMysqlTableUrl);
            setResult.removeAll(setHiveTableUrl);
            System.out.println("删除差集:"+setResult);
            //删除datasource表中的"HIVE"数据
            for (String url : setResult){
                dataSourceRepository.deleteByFileUrlAndFileType(url,"HIVE");
            }
            System.out.println("已删除HIVE表");
            //要增加的记录（差集）
            setResult.clear();
            setResult.addAll(setHiveTableUrl);
            setResult.removeAll(setMysqlTableUrl);
            System.out.println("增加差集:"+setResult);
            //新增"HIVE"数据
            for (String url : setResult){
                DataSource dataSource = new DataSource();
                dataSource.setFileType("HIVE");
                dataSource.setFileUrl(url);
                dataSource.setFileName(url.split("/")[1]);
                dataSourceRepository.saveAndFlush(dataSource);
            }
        }catch (Exception e){
            System.out.println(e.toString());
            return false;
        }


        /**
         *更新mysql中Falt源
         */

        //获取cubes
        kqs.login("ADMIN","KYLIN");
        String cubeList = kqs.listCubes(0,50000,"","");
        System.out.println("--------------cubeList： "+cubeList);

        //将jsonString 解析为json
        JSONArray cubeJsonArray;
        try {
            cubeJsonArray = new JSONArray(cubeList);//先将对象转成json数组
        } catch (Exception e) {
            //字符串不能解析
            System.out.println("解析失败,宽表创建失败");
            return false;
        }
        for(int i = 0; i < cubeJsonArray.length();i++){
            JSONObject job = cubeJsonArray.getJSONObject(i);
            if (job.getString("status").equals("READY")){
                //取出 model 名称和 project 名称
                String model = job.getString("model");
                String project = job.get("project").toString();

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
                List<FaltTable> faltTables = faltTableRepository.findByModelAndProject(model,project);
                if (faltTables.size() == 0){
                    FaltTable faltTable = new FaltTable();
                    faltTable.setModel(model);
                    faltTable.setProject(project);
                    faltTable.setTableSql(sql);
                    faltTable.setName(name);
                    faltTable.setTables(tables);
                    faltTableRepository.saveAndFlush(faltTable);
                }else if (faltTables.size() == 1){
                    FaltTable faltTable = faltTables.get(0);
                    faltTable.setModel(model);
                    faltTable.setProject(project);
                    faltTable.setTableSql(sql);
                    faltTable.setName(name);
                    faltTable.setTables(tables);
                    faltTableRepository.saveAndFlush(faltTable);
                }else {
                    System.out.println("数据库中已多个存在重复model,宽表创建失败，请检查数据库");
                    return false;
                }

                //向数据源注册/更新宽表（dataSource）
                List<DataSource> dataSources = dataSourceRepository.findByFileNameEquals(name);
                if (dataSources.size() == 0){
                    DataSource dataSource = new DataSource();
                    dataSource.setFileName(name);
                    dataSource.setFileUrl(sql);
                    dataSource.setFileType("FALT");
                    dataSourceRepository.saveAndFlush(dataSource);
                }else if (dataSources.size() == 1){
                    DataSource dataSource = dataSources.get(0);
                    dataSource.setFileUrl(sql);
                    dataSourceRepository.saveAndFlush(dataSource);
                }else {
                    System.out.println("数据库中存在重复name的数据源,宽表注册失败，请检查数据库");
                    return false;
                }

            }
        }
        return true;
    }
}
