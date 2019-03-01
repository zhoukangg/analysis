package bupt.edu.cn.web.chartsmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2017/4/23.
 */
public class XAxis {

    public String name;
    public String type;
    public List<String> data = new ArrayList<>();
    public boolean boundaryGap = false;
    public AxisLabel axisLabel = new AxisLabel();
    public XAxis(){
        this.boundaryGap = false;
    }

}
