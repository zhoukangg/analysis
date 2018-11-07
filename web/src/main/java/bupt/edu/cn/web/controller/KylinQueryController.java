package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.common.ReturnModel;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import bupt.edu.cn.kylin.service.KylinQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KylinQueryController {

    @Autowired
    KylinQueryService kqs;

    @RequestMapping("/kylinSQL")
    public ReturnModel findTechnologyChildrens(@RequestParam(value = "sql") String sql) {
        ReturnModel result = new ReturnModel();
        String output ;
        kqs.login("ADMIN","KYLIN");
        String body = "{\"sql\":\""+ sql +"\",\"offset\":0,\"limit\":50000,\"acceptPartial\":false,\"project\":\"learn_kylin\"}";
        output = kqs.query(body);
        //将jsonString 解析为json
        JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            result.setDatum((JSONObject)p.parse(output));
        } catch (Exception e) {
            //字符串不能解析
        }

        return result;
    }
}