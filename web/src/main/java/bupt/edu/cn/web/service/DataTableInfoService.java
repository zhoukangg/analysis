package bupt.edu.cn.web.service;

import bupt.edu.cn.kylin.service.KylinQueryService;
import bupt.edu.cn.kylin.service.impl.KylinQueryServiceImpl;
import com.opencsv.CSVReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class DataTableInfoService {

    @Autowired
    KylinQueryService kqs;

    //    判断整数（int）
    private boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    //     判断浮点数（double和float）
    private boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }


    /**
     * 获取宽表的维度和度量
     *
     * @param project
     * @param tables
     * @return
     */
    public Map<String, String[]> getFaltTableDims(String project, String tables) {
//        System.out.println(project);
//        System.out.println(tables);

        Map result = new HashMap();
        List<String> dims = new ArrayList<>();
        List<String> meas = new ArrayList<>();
        String[] tableArr = tables.split(",");
        kqs.login("ADMIN", "KYLIN");
        for (int i = 0; i < tableArr.length; i++) {
//            System.out.println(tableArr[i]);
            String tableStr = kqs.getHiveTable(project, tableArr[i]);
//            System.out.println(tableStr);
            JSONObject tableJson = new JSONObject(tableStr);
            JSONArray columns = tableJson.getJSONArray("columns");
//            System.out.println(columns.length());
            for (int j = 0; j < columns.length(); j++) {
                JSONObject column = columns.getJSONObject(j);
                String datatype = column.getString("datatype");
//                System.out.println(datatype);
                if (datatype.indexOf("data") != -1 || datatype.indexOf("varchar") != -1) {
                    dims.add(tableJson.getString("name") + "." + column.getString("name"));
                } else {
                    meas.add(tableJson.getString("name") + "." + column.getString("name"));
                }
            }
        }
        result.put("dims", dims);
        result.put("meas", meas);

        return result;
    }

    /**
     * 获取宽表包含的hive表
     *
     * @param project
     * @param tables
     * @return
     */
    public List<String> getFaltTableTables(String project, String tables) {
        String[] tableArr = tables.split(",");
//        List<String> result = new ArrayList<>();
//        for (int i = 0;i<tableArr.length;i++){
//            result.add(tableArr[i].split("\\.")[1]);
//        }
        return Arrays.asList(tableArr);
    }

    /**
     * 获取宽表包含的hive表的维度和度量
     *
     * @param project
     * @param tableName
     * @return
     */
    public Map<String, List<String>> getHiveTableDimsByKylin(String project, String tableName) {
        kqs.login("ADMIN", "KYLIN");
        String tableStr = kqs.getHiveTable(project, tableName);
        JSONObject tableJson = new JSONObject(tableStr);
        JSONArray columns = tableJson.getJSONArray("columns");
        List<String> tableDims = new ArrayList<>();
        List<String> tableMeas = new ArrayList<>();
        for (int j = 0; j < columns.length(); j++) {
            JSONObject column = columns.getJSONObject(j);
            String datatype = column.getString("datatype");
            if (datatype.indexOf("data") != -1 || datatype.indexOf("varchar") != -1) {
                tableDims.add(column.getString("name"));
            } else {
                tableMeas.add(column.getString("name"));
            }
        }
        Map<String, List<String>> result = new HashMap();
        result.put("dims", tableDims);
        result.put("meas", tableMeas);
        return result;
    }


    /**
     * 获取宽表的维度和度量(按照hive表组织)
     *
     * @param project
     * @param tables
     * @return
     */
    public Map<String, Map<String, List<String>>> getFaltTableDimsGroupByTables(String project, String tables) {
        Map result = new HashMap();
        Map<String, List<String>> dims = new HashMap<>();
        Map<String, List<String>> meas = new HashMap<>();
        String[] tableArr = tables.split(",");
        kqs.login("ADMIN", "KYLIN");
        for (int i = 0; i < tableArr.length; i++) {
//            System.out.println(tableArr[i]);
            String tableStr = kqs.getHiveTable(project, tableArr[i]);
//            System.out.println(tableStr);
            JSONObject tableJson = new JSONObject(tableStr);
            JSONArray columns = tableJson.getJSONArray("columns");
//            System.out.println(columns.length());
            List<String> tableDims = new ArrayList<>();
            List<String> tableMeas = new ArrayList<>();
            for (int j = 0; j < columns.length(); j++) {
                JSONObject column = columns.getJSONObject(j);
                String datatype = column.getString("datatype");
//                System.out.println(datatype);
                if (datatype.indexOf("data") != -1 || datatype.indexOf("varchar") != -1) {
                    tableDims.add(column.getString("name"));
                } else {
                    tableMeas.add(column.getString("name"));
                }
            }
            //如果没有值就不放该表
//            if (tableDims.size() != 0){
//                dims.put(tableJson.getString("name"),tableDims);
//            }
//            if (tableMeas.size() != 0){
//                meas.put(tableJson.getString("name"),tableMeas);
//            }
            //没有值也放该表
            dims.put(tableJson.getString("name"), tableDims);
            meas.put(tableJson.getString("name"), tableMeas);
        }
        result.put("dims", dims);
        result.put("meas", meas);

        return result;
    }


    /**
     * 获取csv文件的维度和度量
     *
     * @param fileUrl
     * @return
     */
    public Map<String, String[]> getCsvDim(String fileUrl) {
        Map result = new HashMap();
        List<String> dims = new ArrayList<>();
        List<String> meas = new ArrayList<>();
        String[] nextLine = {};
        String[] demoLine = {};
        try {
            // 创建CSV读对象
            CSVReader reader = null;
            reader = new CSVReader(new FileReader(fileUrl));
            // 读表头
            nextLine = reader.readNext();
            //读第二行
            demoLine = reader.readNext();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //判断String和数值型，区分dims和meas
        for (int i = 0; i < demoLine.length; i++) {
            if (isInteger(demoLine[i]) || isDouble(demoLine[i])) {
                meas.add(nextLine[i]);
            } else {
                dims.add(nextLine[i]);
            }
        }
        result.put("dims", dims.toArray());
        result.put("meas", meas.toArray());
        return result;
    }


}
