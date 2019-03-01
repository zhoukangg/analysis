package bupt.edu.cn.web.service;

import bupt.edu.cn.kylin.service.KylinQueryService;
import bupt.edu.cn.kylin.service.impl.KylinQueryServiceImpl;
import bupt.edu.cn.spark.common.SpSession;
import bupt.edu.cn.spark.service.impl.SparkSqlServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DataTableInfoService.class,KylinQueryService.class,KylinQueryServiceImpl.class})
public class DataTableInfoServiceTest {

    @Autowired
    DataTableInfoService dataTableInfoService;

    @Test
    public void getFaltTableDims(){

        dataTableInfoService.getFaltTableDims("jsw","JSW_DATA.GD_GENERAL_INFO_W");
    }


    @Test
    public void getCsvDim() {

        String fileUrl = "/Users/kang/Desktop/BreadBasket_DMS.csv";
        Map<String,String[]> re = dataTableInfoService.getCsvDim(fileUrl);
        System.out.println(re.get("dims"));
        System.out.println(re.get("meas"));
    }
}