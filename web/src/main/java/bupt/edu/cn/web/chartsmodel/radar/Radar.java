package bupt.edu.cn.web.chartsmodel.radar;

import bupt.edu.cn.web.chartsmodel.radar.Indicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2017/5/15.
 */
public class Radar {
    public List<Indicator> indicator = new ArrayList<>();
    public String shape = "radar";

    public Radar(String shape){
        this.shape = shape;
    }
    public Radar(){}
    
}
