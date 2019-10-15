package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.util.realtime.HiveListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program analysis
 * @description: ${TODO}
 * @author: kang
 * @create: 2019/07/07 18:37
 */

@RestController
public class HiveController {

    @Autowired
    HiveListener hiveListener;

    @RequestMapping(value = "/hiveChange")
    public ReturnModel hiveChange(String fileUrl) {
        System.out.println("--------进入方法hiveChange-----");
        System.out.println("--------参数 fileUrl = " + fileUrl);
        ReturnModel returnModel = new ReturnModel();
        try{
            hiveListener.exeTimeInterval(fileUrl);
            returnModel.setResult(true);
        }catch (Exception e){
            returnModel.setResult(false);
            e.printStackTrace();
        }
        return returnModel;
    }
}
