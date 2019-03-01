package bupt.edu.cn.web.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SQLGenerateTest {

    @Test
    public void getbydim() {
        SQLGenerate sqlGenerate = new SQLGenerate();
        String[] dim=new String[]{"dim1","dim2"};
        String[] mea = new String[]{"mea1","mea2"};
        String tablename = "table";
        String sql = sqlGenerate.getbydim(dim,mea,tablename);
        System.out.println(sql);
    }

    @Test
    public void getWithGroup(){
        SQLGenerate sqlGenerate = new SQLGenerate();
        String[] dim=new String[]{"dim1","dim2"};
        List<String> mea = new ArrayList<>();
        mea.add("mea1");
        mea.add("mea2");
        List<String> fun = new ArrayList<>();
        fun.add("sum");
        fun.add("max");
        String tablename = "table";
        String sql = sqlGenerate.getWithGroup(dim,fun,mea,tablename,"CSV","","spark","10");
        System.out.println(sql);
    }
    @Test
    public void getFaltTableSql(){
        SQLGenerate sqlGenerate = new SQLGenerate();
        String projectName = "learn_kylin";
        String sql = "{\"uuid\":\"0928468a-9fab-4185-9a14-6f2e7c74823f\",\"last_modified\":1542695289000,\"version\":\"2.5.1.20500\",\"name\":\"kylin_sales_model\",\"owner\":null,\"is_draft\":false,\"description\":\"\",\"fact_table\":\"DEFAULT.KYLIN_SALES\",\"lookups\":[{\"table\":\"DEFAULT.KYLIN_CAL_DT\",\"kind\":\"LOOKUP\",\"alias\":\"KYLIN_CAL_DT\",\"join\":{\"type\":\"inner\",\"primary_key\":[\"KYLIN_CAL_DT.CAL_DT\"],\"foreign_key\":[\"KYLIN_SALES.PART_DT\"]}},{\"table\":\"DEFAULT.KYLIN_CATEGORY_GROUPINGS\",\"kind\":\"LOOKUP\",\"alias\":\"KYLIN_CATEGORY_GROUPINGS\",\"join\":{\"type\":\"inner\",\"primary_key\":[\"KYLIN_CATEGORY_GROUPINGS.LEAF_CATEG_ID\",\"KYLIN_CATEGORY_GROUPINGS.SITE_ID\"],\"foreign_key\":[\"KYLIN_SALES.LEAF_CATEG_ID\",\"KYLIN_SALES.LSTG_SITE_ID\"]}},{\"table\":\"DEFAULT.KYLIN_ACCOUNT\",\"kind\":\"LOOKUP\",\"alias\":\"BUYER_ACCOUNT\",\"join\":{\"type\":\"inner\",\"primary_key\":[\"BUYER_ACCOUNT.ACCOUNT_ID\"],\"foreign_key\":[\"KYLIN_SALES.BUYER_ID\"]}},{\"table\":\"DEFAULT.KYLIN_ACCOUNT\",\"kind\":\"LOOKUP\",\"alias\":\"SELLER_ACCOUNT\",\"join\":{\"type\":\"inner\",\"primary_key\":[\"SELLER_ACCOUNT.ACCOUNT_ID\"],\"foreign_key\":[\"KYLIN_SALES.SELLER_ID\"]}},{\"table\":\"DEFAULT.KYLIN_COUNTRY\",\"kind\":\"LOOKUP\",\"alias\":\"BUYER_COUNTRY\",\"join\":{\"type\":\"inner\",\"primary_key\":[\"BUYER_COUNTRY.COUNTRY\"],\"foreign_key\":[\"BUYER_ACCOUNT.ACCOUNT_COUNTRY\"]}},{\"table\":\"DEFAULT.KYLIN_COUNTRY\",\"kind\":\"LOOKUP\",\"alias\":\"SELLER_COUNTRY\",\"join\":{\"type\":\"inner\",\"primary_key\":[\"SELLER_COUNTRY.COUNTRY\"],\"foreign_key\":[\"SELLER_ACCOUNT.ACCOUNT_COUNTRY\"]}}],\"dimensions\":[{\"table\":\"KYLIN_SALES\",\"columns\":[\"TRANS_ID\",\"SELLER_ID\",\"BUYER_ID\",\"PART_DT\",\"LEAF_CATEG_ID\",\"LSTG_FORMAT_NAME\",\"LSTG_SITE_ID\",\"OPS_USER_ID\",\"OPS_REGION\"]},{\"table\":\"KYLIN_CAL_DT\",\"columns\":[\"CAL_DT\",\"WEEK_BEG_DT\",\"MONTH_BEG_DT\",\"YEAR_BEG_DT\"]},{\"table\":\"KYLIN_CATEGORY_GROUPINGS\",\"columns\":[\"USER_DEFINED_FIELD1\",\"USER_DEFINED_FIELD3\",\"META_CATEG_NAME\",\"CATEG_LVL2_NAME\",\"CATEG_LVL3_NAME\",\"LEAF_CATEG_ID\",\"SITE_ID\"]},{\"table\":\"BUYER_ACCOUNT\",\"columns\":[\"ACCOUNT_ID\",\"ACCOUNT_BUYER_LEVEL\",\"ACCOUNT_SELLER_LEVEL\",\"ACCOUNT_COUNTRY\",\"ACCOUNT_CONTACT\"]},{\"table\":\"SELLER_ACCOUNT\",\"columns\":[\"ACCOUNT_ID\",\"ACCOUNT_BUYER_LEVEL\",\"ACCOUNT_SELLER_LEVEL\",\"ACCOUNT_COUNTRY\",\"ACCOUNT_CONTACT\"]},{\"table\":\"BUYER_COUNTRY\",\"columns\":[\"COUNTRY\",\"NAME\"]},{\"table\":\"SELLER_COUNTRY\",\"columns\":[\"COUNTRY\",\"NAME\"]}],\"metrics\":[\"KYLIN_SALES.PRICE\",\"KYLIN_SALES.ITEM_COUNT\"],\"filter_condition\":\"\",\"partition_desc\":{\"partition_date_column\":\"KYLIN_SALES.PART_DT\",\"partition_time_column\":null,\"partition_date_start\":1325376000000,\"partition_date_format\":\"yyyy-MM-dd\",\"partition_time_format\":\"HH:mm:ss\",\"partition_type\":\"APPEND\",\"partition_condition_builder\":\"org.apache.kylin.metadata.model.PartitionDesc$DefaultPartitionConditionBuilder\"},\"capacity\":\"MEDIUM\"}";
        System.out.println(sqlGenerate.getFaltTableSql(sql).length);
    }
}