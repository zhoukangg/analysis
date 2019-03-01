package bupt.edu.cn.web.chartsmodel;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用柱状图、面积图
 */
public class ChartModelOne {
    public YAxis yAxis = new YAxis();
    public XAxis xAxis = new XAxis();
    public Legend legend = new Legend();
    public List<Serial> series = new ArrayList<>();
    public ToolTips tooltip = new ToolTips();
    public Map<String,String> title = new HashMap() ;
    public Map<String,String> grid = new HashMap() ;
}
