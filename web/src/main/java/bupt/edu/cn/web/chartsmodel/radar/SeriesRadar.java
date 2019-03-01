package bupt.edu.cn.web.chartsmodel.radar;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述Series序列中的一个元素
 */
public class SeriesRadar {
    public String name;
    public String type = "radar";
    public List<DataRadar> data = new ArrayList<>();
    public SeriesRadar(){}
    public SeriesRadar(String name,String type){
        this.name = name;
        this.type = type;
    }
}
