package bupt.edu.cn.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DrillController {

    @RequestMapping("/drillDimSet")
    public String drilloptionset(String tablename, String drilldims){
        System.out.println("开始设置一个上卷下钻的维度值");


        return "";
    }
}
