package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.pojo.FaltTable;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.FaltTableRepository;
import com.alibaba.fastjson.JSON;
import net.minidev.json.JSONObject;
import bupt.edu.cn.kylin.service.KylinQueryService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class KylinQueryController {

    @Autowired
    KylinQueryService kylinQueryService;
    @Autowired
    DataSourceRepository dataSourceRepository;
    @Autowired
    FaltTableRepository faltTableRepository;


    /**
     * 获取cube
     *
     * @param fileUrl
     * @param tableName
     * @return
     */
    @RequestMapping("/getCubes")
    public ReturnModel getCubes(String fileUrl, String tableName) {

        ReturnModel result = new ReturnModel();
        List<String> returnData = new ArrayList<>();
        List<FaltTable> faltTables = faltTableRepository.findByNameAndTableSql(tableName, fileUrl);
        FaltTable faltTable = new FaltTable();
        if (faltTables.size() == 1) {
            faltTable = faltTables.get(0);
        } else if (faltTables.size() == 0) {
            result.setResult(false);
            result.setReason("数据源表查询不到该记录");
            return result;
        } else {
            result.setResult(false);
            result.setReason("数据源表匹配到多条记录");
            return result;
        }
        String project = faltTable.getProject();
        //登陆
        kylinQueryService.login("ADMIN", "KYLIN");
        String cubes = kylinQueryService.listCubes(0, 50000, "", project);
        System.out.println("-------cubes-------");
        System.out.println(cubes);
        JSONArray cubesArr = new JSONArray(cubes);
        for (int i = 0; i < cubesArr.length(); i++) {
            org.json.JSONObject cube = cubesArr.getJSONObject(i);
            if (cube.getString("status").equals("READY")) {
                returnData.add(cube.getString("name"));
            }
        }
        result.setDatum(returnData);
        return result;
    }

    /**
     * 获取cube的纬度
     *
     * @param cubeName
     * @return
     */
    @RequestMapping("/getCubeDims")
    public ReturnModel getCubeDims(String cubeName) {

        ReturnModel result = new ReturnModel();
        kylinQueryService.login("ADMIN", "KYLIN");
        String newCubeStr = kylinQueryService.getCubeDes(cubeName);
        System.out.println(newCubeStr);
        JSONArray newCubeArr = new JSONArray(newCubeStr);
        org.json.JSONObject dimsCube = newCubeArr.getJSONObject(0);
        //得到该cube的维度和度量
        JSONArray cubeDims = dimsCube.getJSONArray("dimensions");
        JSONArray cubeMeas = dimsCube.getJSONArray("measures");
        List<String> dims = new ArrayList<>();
        List<String> meas = new ArrayList<>();
        List<String> funs = new ArrayList<>();
        for (int z = 0; z < cubeDims.length(); z++) {
            dims.add(cubeDims.getJSONObject(z).getString("table") + "." + cubeDims.getJSONObject(z).getString("name"));
        }
        for (int z = 0; z < cubeMeas.length(); z++) {
            meas.add(cubeMeas.getJSONObject(z).getJSONObject("function").getJSONObject("parameter").getString("value"));
            funs.add(cubeMeas.getJSONObject(z).getJSONObject("function").getString("expression"));
        }
        Map<String, List<String>> resultData = new HashMap<>();
        resultData.put("dims", dims);
        resultData.put("meas", meas);
        resultData.put("funs", funs);
        result.setDatum(resultData);
        return result;
    }

    /**
     * 获取cube涉及到的table 所包含的纬度
     *
     * @param cubeName
     * @param tableName
     * @return
     */
    @RequestMapping("/getCubeTableDims")
    public ReturnModel getCubeTableDims(String cubeName, String tableName) {

        ReturnModel result = new ReturnModel();
        kylinQueryService.login("ADMIN", "KYLIN");
        String newCubeStr = kylinQueryService.getCubeDes(cubeName);
        System.out.println(newCubeStr);
        JSONArray newCubeArr = new JSONArray(newCubeStr);
        org.json.JSONObject dimsCube = newCubeArr.getJSONObject(0);
        //得到该cube的维度和度量
        JSONArray cubeDims = dimsCube.getJSONArray("dimensions");
        JSONArray cubeMeas = dimsCube.getJSONArray("measures");
        Set<String> dims = new TreeSet<>();
        Set<String> meas = new TreeSet<>();
        for (int z = 0; z < cubeDims.length(); z++) {
            if (tableName.equals(cubeDims.getJSONObject(z).getString("table"))) {
                dims.add(cubeDims.getJSONObject(z).getString("name"));
            }
        }
        for (int z = 0; z < cubeMeas.length(); z++) {
            String str = cubeMeas.getJSONObject(z).getJSONObject("function").getJSONObject("parameter").getString("value");
            if (tableName.equals(str.split("\\.")[0])) {
                meas.add(str.split("\\.")[1]);
            }
        }
        Map<String, Set<String>> mea_funs = new HashMap<>();
        for (String mea : meas) {
            Set<String> funs = new TreeSet<>();
            for (int z = 0; z < cubeMeas.length(); z++) {
                String str = cubeMeas.getJSONObject(z).getJSONObject("function").getJSONObject("parameter").getString("value");
                if (str.equals("1")) {
                    funs.add(cubeMeas.getJSONObject(z).getJSONObject("function").getString("expression"));
                    continue;
                }
                if (mea.equals(str.split("\\.")[1])) {
                    funs.add(cubeMeas.getJSONObject(z).getJSONObject("function").getString("expression"));
                }
            }
            mea_funs.put(mea, funs);
        }

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("dims", dims);
        resultData.put("meas", mea_funs);
        result.setDatum(resultData);
        return result;
    }


    /**
     * 获取cube所涉及到的表
     *
     * @param cubeName
     * @return
     */
    @RequestMapping("/getCubeTables")
    public ReturnModel getCubeTables(String cubeName) {

        ReturnModel result = new ReturnModel();
        kylinQueryService.login("ADMIN", "KYLIN");
        String newCubeStr = kylinQueryService.getCubeDes(cubeName);
        System.out.println(newCubeStr);
        JSONArray newCubeArr = new JSONArray(newCubeStr);
        org.json.JSONObject dimsCube = newCubeArr.getJSONObject(0);
        //得到该cube维度和度量对应的table
        JSONArray cubeDims = dimsCube.getJSONArray("dimensions");
        JSONArray cubeMeas = dimsCube.getJSONArray("measures");
        Set<String> tables = new TreeSet<>();
        for (int z = 0; z < cubeDims.length(); z++) {
            tables.add(cubeDims.getJSONObject(z).getString("table"));
        }
        for (int z = 0; z < cubeMeas.length(); z++) {
            String tab = cubeMeas.getJSONObject(z).getJSONObject("function").getJSONObject("parameter").getString("value").split("\\.")[0];
            if (!tab.equals("1"))
                tables.add(tab);
        }
        result.setDatum(tables);
        return result;
    }

    /**
     * kylin sql test
     *
     * @param sql
     * @return
     */
    @RequestMapping("/kylinSQL")
    public ReturnModel findTechnologyChildrens(@RequestParam(value = "sql") String sql) {

        ReturnModel result = new ReturnModel();
        String output;
        //登陆
        kylinQueryService.login("ADMIN", "KYLIN");

        //拼接参数
        JSONObject obj = new JSONObject();
        obj.put("sql", sql);
        obj.put("offset", new Integer(100));
        obj.put("limit", new Double(1000.21));
        obj.put("acceptPartial", new Boolean(true));
        obj.put("project", new Boolean(true));

        String body = "{\"sql\":\"" + sql + "\",\"offset\":0,\"limit\":50000,\"acceptPartial\":false,\"project\":\"learn_kylin\"}";

        //查询
        try {
            output = kylinQueryService.query(body);
        } catch (Exception e) {
            //查询失败
            result.setReason("查询失败");
            result.setResult(false);
            return result;
        }

        //将jsonString 解析为json
        net.minidev.json.parser.JSONParser p = new net.minidev.json.parser.JSONParser(net.minidev.json.parser.JSONParser.MODE_JSON_SIMPLE);
        try {
            result.setDatum((JSONObject) p.parse(output));
            result.setReason("成功");
            result.setResult(true);
        } catch (Exception e) {
            //字符串不能解析
            result.setReason("解析失败");
            result.setResult(false);
            return result;
        }

        return result;
    }

    @RequestMapping("/kylinSQLTest")
    public ReturnModel kylinSQLTest() {
        String sql = "select PART_DT,LEAF_CATEG_ID from  KYLIN_SALES";
        String body = "{\"sql\":\"" + sql + "\",\"offset\":0,\"limit\":50000,\"acceptPartial\":false,\"project\":\"learn_kylin\"}";
        ReturnModel result = new ReturnModel();
        String output = "";
        //查询
        try {
            kylinQueryService.login("ADMIN", "KYLIN");
            output = kylinQueryService.query(body);
        } catch (Exception e) {
            //查询失败
            result.setReason("查询失败");
            result.setResult(false);
            return result;
        }
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        result.setDatum(JSON.parseObject(output));
        return result;
    }

}