package bupt.edu.cn.web.controller;

import bupt.edu.cn.spark.service.impl.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/*")
public class TestController {

//    @Autowired
//    UserService userService;

    @GetMapping("list")
    public int list() {
        try {
            return 1;
        } catch (Exception e) {
            return 0;
        }

    }
    @Autowired
    TestServiceImpl testServiceImpl;

    @GetMapping("sparkTest")
    public void sparkTest(){
        testServiceImpl.example();
    }


}
