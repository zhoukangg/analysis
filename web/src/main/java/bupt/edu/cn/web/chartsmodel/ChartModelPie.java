package bupt.edu.cn.web.chartsmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * 饼图
 */
public class ChartModelPie {

    public Title title = new Title();
    public ToolTips tooltip = new ToolTips();
    public LegendPie legend = new LegendPie();
    public List<SeriesPie> series = new ArrayList<>();
    public ChartModelPie(){
        this.tooltip.trigger="item";
        this.tooltip.formatter="{a} <br/>{b} : {c} ({d}%)";
    }

}
