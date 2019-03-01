package bupt.edu.cn.web.util;

import bupt.edu.cn.kylin.service.KylinQueryService;
import bupt.edu.cn.kylin.service.impl.KylinQueryServiceImpl;
import bupt.edu.cn.web.WebApplication;
import bupt.edu.cn.web.repository.FaltTableRepository;
import bupt.edu.cn.web.service.DataTableInfoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {FaltTableRepository.class,QueryRoute.class,DataTableInfoService.class,KylinQueryService.class,KylinQueryServiceImpl.class})
//@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
@WebAppConfiguration
public class QueryRouteTest {

    @Test
    public void route() {
        QueryRoute queryRoute = new QueryRoute();
        List<String> dims = new ArrayList<>(Arrays.asList("aa","bb"));
        List<String> meas = new ArrayList<>(Arrays.asList("sum.mm","sum.tt"));
        String fileName = "second_pregnancy2";
        String fileUrl = "select GD_GENERAL_INFO_W.* , GD_PREPREGNANCY_SERVICE.* , GD_BASIC_INFO_DETAIL.* from JSW_DATA.GD_GENERAL_INFO_W left join JSW_DATA.GD_PREPREGNANCY_SERVICE GD_PREPREGNANCY_SERVICE on GD_PREPREGNANCY_SERVICE.ID=GD_GENERAL_INFO_W.ID inner join JSW_DATA.GD_BASIC_INFO_DETAIL GD_BASIC_INFO_DETAIL on GD_BASIC_INFO_DETAIL.ID=GD_GENERAL_INFO_W.ID";

        //接口测试通过，怀疑问题：解决在测试方法中调用数据库
//        queryRoute.route( dims, meas, fileName,fileUrl);
        System.out.println("QueryRouteTest");
    }
}