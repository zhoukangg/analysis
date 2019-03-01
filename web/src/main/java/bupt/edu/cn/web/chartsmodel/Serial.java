package bupt.edu.cn.web.chartsmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2017/4/23.
 */
public class Serial {
    public String type;
    public String name;
    public String stack;
    public List<String> color = new ArrayList<>();
    public List<String> data = new ArrayList<>();
    public AreaStyle areaStyle = new AreaStyle();
    public int yAxisIndex = 0;
    public int xAxisIndex = 0;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }



}
