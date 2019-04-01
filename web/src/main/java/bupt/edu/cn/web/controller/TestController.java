package bupt.edu.cn.web.controller;

import bupt.edu.cn.kylin.service.KylinQueryService;
import bupt.edu.cn.spark.service.impl.TestServiceImpl;
import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.common.WebConstant;
import bupt.edu.cn.web.service.OptionService;
import bupt.edu.cn.web.service.QueryService;
import bupt.edu.cn.web.util.QueryRoute;
import bupt.edu.cn.web.util.SQLGenerate;
import bupt.edu.cn.web.util.StringUtil;
import bupt.edu.cn.web.util.chartsBase;
//import com.alibaba.fastjson.JSONObject;
import com.peaceful.auth.sdk.Impl.AuthServiceImpl;
import com.peaceful.auth.sdk.api.AuthService;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.peaceful.auth.sdk.Impl.AuthServiceImpl;

@RestController
@RequestMapping("/user/*")
public class TestController {

    @Autowired
    TestServiceImpl testServiceImpl;
    @Autowired
    QueryRoute queryRoute;
    @Autowired
    OptionService optionService;
    @Autowired
    KylinQueryService kylinQueryService;
    @Autowired
    QueryService queryService;

    @GetMapping("getQueryDataTest")
    public ReturnModel getQueryDataTest(String userId, String dataSourceId, String dims, String meas, String fileUrl, String tableName,String fileType,String routeStr){
        System.out.println("-----------进入方法 /getQueryDataTest----------");
        System.out.println("-----------参数1：dims = " + dims);
        System.out.println("-----------参数2：meas = " + meas);
        System.out.println("-----------参数3：fileUrl = " + fileUrl);
        System.out.println("-----------参数4：tableName = " + tableName);
        String[] dimArr = {};
        String[] funAndMeaArr = {};
        List<String> meaArr = new ArrayList<>();
        List<String> funArr = new ArrayList<>();

        ReturnModel returnModel = new ReturnModel();
        if (dims == "" && meas == "")       //两个都是空的时候直接返回空值
            return returnModel;

        JSONObject result = new JSONObject();
        if(dims != null && !dims.equals("") && !dims.equals(" ")){
            dimArr = dims.split(",");
        }
        if(meas != null && !meas.equals("") && !meas.equals(" ")){
            meas = StringUtil.custom_trim(meas,',');
            System.out.println("----------去,后：meas = " + meas);
            funAndMeaArr = meas.split(",");
            for (int i = 0; i <funAndMeaArr.length; i++){
                System.out.println(funAndMeaArr[i]);
                funArr.add(funAndMeaArr[i].split("\\.")[0]);
                meaArr.add(funAndMeaArr[i].split("\\.")[1]);
            }
        }

        SQLGenerate sqlGenerate = new SQLGenerate();
        //获取SQL
        String sql = "";
        if (meaArr.size() == 1 && dimArr.length == 0)     //兼容指标卡的特殊Option
            sql =sqlGenerate.getWithOnemeas(funArr,meaArr,tableName,fileType,fileUrl, routeStr);
        else
            sql = sqlGenerate.getWithGroup(dimArr, funArr, meaArr,tableName,fileType,fileUrl, routeStr,"10");
        System.out.println("The SQL is: " + sql);
        returnModel.setDatum(queryService.getQueryData(Arrays.asList(dimArr), funArr, meaArr, fileUrl, tableName, sql,routeStr));
        return returnModel;
    }

    @GetMapping("optionTest")
    public ReturnModel optionTest(){
        String[] dims={""};
        String[] meas={""};
//        OptionService optionService =new OptionService();
        return optionService.createOption(dims,meas);
    }

    @GetMapping("test")
    public void test() {
        String[] dims ={"KYLIN_CAL_DT.CAL_DT","KYLIN_SALES.PART_DT"};
        String[] meas = {"KYLIN_CAL_DT.CAL_DT"};
        String fileName = "kylin_sales_model";
        String fileUrl = "select KYLIN_SALES.* , KYLIN_CAL_DT.* , KYLIN_CATEGORY_GROUPINGS.* , BUYER_ACCOUNT.* , SELLER_ACCOUNT.* , BUYER_COUNTRY.* , SELLER_COUNTRY.* from DEFAULT.KYLIN_SALES inner join DEFAULT.KYLIN_CAL_DT KYLIN_CAL_DT on KYLIN_CAL_DT.CAL_DT=KYLIN_SALES.PART_DT inner join DEFAULT.KYLIN_CATEGORY_GROUPINGS KYLIN_CATEGORY_GROUPINGS on KYLIN_CATEGORY_GROUPINGS.LEAF_CATEG_ID=KYLIN_SALES.LEAF_CATEG_ID and KYLIN_CATEGORY_GROUPINGS.SITE_ID=KYLIN_SALES.LSTG_SITE_ID inner join DEFAULT.KYLIN_ACCOUNT BUYER_ACCOUNT on BUYER_ACCOUNT.ACCOUNT_ID=KYLIN_SALES.BUYER_ID inner join DEFAULT.KYLIN_ACCOUNT SELLER_ACCOUNT on SELLER_ACCOUNT.ACCOUNT_ID=KYLIN_SALES.SELLER_ID inner join DEFAULT.KYLIN_COUNTRY BUYER_COUNTRY on BUYER_COUNTRY.COUNTRY=BUYER_ACCOUNT.ACCOUNT_COUNTRY inner join DEFAULT.KYLIN_COUNTRY SELLER_COUNTRY on SELLER_COUNTRY.COUNTRY=SELLER_ACCOUNT.ACCOUNT_COUNTRY";
        String fileName2 = "second_pregnancy2";
        String fileUrl2 = "select GD_GENERAL_INFO_W.* , GD_PREPREGNANCY_SERVICE.* , GD_BASIC_INFO_DETAIL.* from JSW_DATA.GD_GENERAL_INFO_W left join JSW_DATA.GD_PREPREGNANCY_SERVICE GD_PREPREGNANCY_SERVICE on GD_PREPREGNANCY_SERVICE.ID=GD_GENERAL_INFO_W.ID left join JSW_DATA.GD_BASIC_INFO_DETAIL GD_BASIC_INFO_DETAIL on GD_BASIC_INFO_DETAIL.ID=GD_GENERAL_INFO_W.ID";
//        queryRoute.route(Arrays.asList(dims),Arrays.asList(meas),fileName,fileUrl);
    }

    @GetMapping("sparkTest")
    public void sparkTest(){
        testServiceImpl.example();
    }
}
