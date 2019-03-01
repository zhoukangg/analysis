package bupt.edu.cn.web.chartsmodel.radar;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷达图option模版
 */
public class ChartModelRadar {
    public TitleRadar title = new TitleRadar();
    public TooltipRadar tooltip = new TooltipRadar();
    public LegendRadar legend = new LegendRadar();
    public Radar radar = new Radar();
    public List<SeriesRadar> series = new ArrayList<>();
}
