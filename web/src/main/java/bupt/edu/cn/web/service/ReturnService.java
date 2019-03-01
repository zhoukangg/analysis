package bupt.edu.cn.web.service;

import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.pojo.Diagram;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReturnService {

    @Autowired
    DiagramService diagramService;

    /**
     * 用于DiagramController  /updateDiagram
     * @param result
     * @param diagramId
     * @param diagramName
     * @param option
     * @param diagramType
     * @param userId
     * @return
     */
    public ReturnModel returnDiagram(ReturnModel result, String diagramId, String diagramName,String option ,String diagramType, String userId){
        //保存
        Diagram di = diagramService.updateDiagram(diagramId, diagramName, option, diagramType,userId);
        //构建返回值
        com.alibaba.fastjson.JSONObject re = new com.alibaba.fastjson.JSONObject();
        re.put("option",JSON.parseObject(option));
        re.put("diagramId",di.getId());
        re.put("diagramName",di.getName());
        re.put("classificaion",di.getClassification());
        re.put("userId",di.getUserId());
        re.put("dataSourceId",di.getDataSourceId());
        result.setDatum(re);
        return result;
    }
}
