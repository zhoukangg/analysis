package bupt.edu.cn.kylin.common;

import bupt.edu.cn.kylin.service.KylinQueryService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class jsonStringTo {

    @Autowired
    KylinQueryService kqs;

    public List<String> getHiveTableDims(String projectName, String factTableName){

        List<String> result = new ArrayList<>();
        String table = kqs.getHiveTable(projectName,factTableName);
        JSONArray jsonTable = new JSONArray(table);


        return result;
    }
}
