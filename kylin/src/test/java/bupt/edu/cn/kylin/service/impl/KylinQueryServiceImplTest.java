package bupt.edu.cn.kylin.service.impl;

import bupt.edu.cn.kylin.service.KylinQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {KylinQueryService.class,KylinQueryServiceImpl.class})
public class KylinQueryServiceImplTest {

    @Autowired
    KylinQueryService kqs;

    @Test
    public void login() {
        kqs.login("ADMIN","KYLIN");
    }

    @Test
    public void listQueryableTables() {
        kqs.login("ADMIN","KYLIN");
        String str = kqs.listQueryableTables("CREATE_CUBE");
        System.out.println(str);
    }

    @Test
    public void listCubes() {
        kqs.login("ADMIN","KYLIN");
        String str = kqs.listCubes(0,50000,"","learn_kylin");
        System.out.println(str);
    }

    @Test
    public void getCubeDes() {
        kqs.login("ADMIN","KYLIN");
        String str = kqs.getCubeDes("kylin_sales_cube");
        System.out.println(str);
    }

    @Test
    public void getCube() {
        kqs.login("ADMIN","KYLIN");
        String str = kqs.getCube("kylin_sales_cube");
        System.out.println(str);
    }

    @Test
    public void getDataModel() {
        kqs.login("ADMIN","KYLIN");
        String str = kqs.getDataModel("kylin_sales_model");
        System.out.println(str);
    }

    @Test
    public void enableCube() {
    }

    @Test
    public void disableCube() {
    }

    @Test
    public void purgeCube() {
    }

    @Test
    public void resumeJob() {
    }

    @Test
    public void buildCube() {
    }

    @Test
    public void discardJob() {
    }

    @Test
    public void getJobStatus() {
    }

    @Test
    public void getJobStepOutput() {
    }

    @Test
    public void getHiveTable() {
        kqs.login("ADMIN","KYLIN");
        String str = kqs.getHiveTable("jsw","JSW_DATA.GD_PREPREGNANCY_SERVICE");
        System.out.println(str);
    }

    @Test
    public void getHiveTableInfo()
    {
//        //不可用
//        kqs.login("ADMIN","KYLIN");
//        String str = kqs.getHiveTableInfo("JSW_DATA.GD_PREPREGNANCY_SERVICE");
//        System.out.println(str);
    }

    @Test
    public void getHiveTables() {
        kqs.login("ADMIN","KYLIN");
        String str = kqs.getHiveTables("jsw", false);
        System.out.println(str);
    }

    @Test
    public void loadHiveTables() {

    }

    @Test
    public void wipeCache() {
    }

    @Test
    public void query() {
        kqs.login("ADMIN","KYLIN");
        String sql8 = "select sum(KYLIN_SALES.PRICE) from DEFAULT.KYLIN_SALES";
        String sql7 = "select sum(LIVE_BIRTH_NUM) from JSW_DATA.GD_GENERAL_INFO_W";
        String sql6 = "select max(KYLIN_SALES.PRICE) from DEFAULT.KYLIN_SALES inner join DEFAULT.KYLIN_CAL_DT KYLIN_CAL_DT on KYLIN_CAL_DT.CAL_DT=KYLIN_SALES.PART_DT inner join DEFAULT.KYLIN_CATEGORY_GROUPINGS KYLIN_CATEGORY_GROUPINGS on KYLIN_CATEGORY_GROUPINGS.LEAF_CATEG_ID=KYLIN_SALES.LEAF_CATEG_ID and KYLIN_CATEGORY_GROUPINGS.SITE_ID=KYLIN_SALES.LSTG_SITE_ID inner join DEFAULT.KYLIN_ACCOUNT BUYER_ACCOUNT on BUYER_ACCOUNT.ACCOUNT_ID=KYLIN_SALES.BUYER_ID inner join DEFAULT.KYLIN_ACCOUNT SELLER_ACCOUNT on SELLER_ACCOUNT.ACCOUNT_ID=KYLIN_SALES.SELLER_ID inner join DEFAULT.KYLIN_COUNTRY BUYER_COUNTRY on BUYER_COUNTRY.COUNTRY=BUYER_ACCOUNT.ACCOUNT_COUNTRY inner join DEFAULT.KYLIN_COUNTRY SELLER_COUNTRY on SELLER_COUNTRY.COUNTRY=SELLER_ACCOUNT.ACCOUNT_COUNTRY";
        String sql5 = "select sum(KYLIN_SALES.seller_id) as `kylin_sales.seller_id_sum` from DEFAULT.KYLIN_SALES left join DEFAULT.KYLIN_CAL_DT KYLIN_CAL_DT on KYLIN_CAL_DT.CAL_DT=KYLIN_SALES.PART_DT left join DEFAULT.KYLIN_CATEGORY_GROUPINGS KYLIN_CATEGORY_GROUPINGS on KYLIN_CATEGORY_GROUPINGS.LEAF_CATEG_ID=KYLIN_SALES.LEAF_CATEG_ID and KYLIN_CATEGORY_GROUPINGS.SITE_ID=KYLIN_SALES.LSTG_SITE_ID left join DEFAULT.KYLIN_ACCOUNT BUYER_ACCOUNT on BUYER_ACCOUNT.ACCOUNT_ID=KYLIN_SALES.BUYER_ID left join DEFAULT.KYLIN_ACCOUNT SELLER_ACCOUNT on SELLER_ACCOUNT.ACCOUNT_ID=KYLIN_SALES.SELLER_ID left join DEFAULT.KYLIN_COUNTRY BUYER_COUNTRY on BUYER_COUNTRY.COUNTRY=BUYER_ACCOUNT.ACCOUNT_COUNTRY left join DEFAULT.KYLIN_COUNTRY SELLER_COUNTRY on SELLER_COUNTRY.COUNTRY=SELLER_ACCOUNT.ACCOUNT_COUNTRY";
        String sql4 = "select count(DISTINCT GD_GENERAL_INFO_W.id) from JSW_DATA.GD_GENERAL_INFO_W left join JSW_DATA.GD_PREPREGNANCY_SERVICE GD_PREPREGNANCY_SERVICE on GD_PREPREGNANCY_SERVICE.ID=GD_GENERAL_INFO_W.ID left join JSW_DATA.GD_BASIC_INFO_DETAIL GD_BASIC_INFO_DETAIL on GD_BASIC_INFO_DETAIL.ID=GD_GENERAL_INFO_W.ID limit 10";
        String sql3 = "select count(DISTINCT id) from gd_prepregnancy_service;";
        String sql2 = "select sum(GD_GENERAL_INFO_W.LIVE_BIRTH_NUM) as LIVE_BIRTH_NUM_sum,GD_GENERAL_INFO_W.EXAMINE_DATE_YEAR from JSW_DATA.GD_GENERAL_INFO_W left join JSW_DATA.GD_PREPREGNANCY_SERVICE GD_PREPREGNANCY_SERVICE on GD_PREPREGNANCY_SERVICE.ID=GD_GENERAL_INFO_W.ID left join JSW_DATA.GD_BASIC_INFO_DETAIL GD_BASIC_INFO_DETAIL on GD_BASIC_INFO_DETAIL.ID=GD_GENERAL_INFO_W.ID group by GD_GENERAL_INFO_W.EXAMINE_DATE_YEAR limit 10";
        String sql = "select sum(LIVE_BIRTH_NUM) from JSW_DATA.GD_GENERAL_INFO_W left join JSW_DATA.GD_PREPREGNANCY_SERVICE GD_PREPREGNANCY_SERVICE on GD_PREPREGNANCY_SERVICE.ID=GD_GENERAL_INFO_W.ID left join JSW_DATA.GD_BASIC_INFO_DETAIL GD_BASIC_INFO_DETAIL on GD_BASIC_INFO_DETAIL.ID=GD_GENERAL_INFO_W.ID";
        String project = "jsw";
        String project2 = "kylin_learn";
        String body = "{\"sql\":\""+ sql8 +"\",\"offset\":0,\"limit\":50000,\"acceptPartial\":false,\"project\":\"kylin_learn\"}";
        String str = kqs.query(body);
        System.out.println(str);
    }
}