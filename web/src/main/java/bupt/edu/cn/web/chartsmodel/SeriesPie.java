package bupt.edu.cn.web.chartsmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeriesPie {
    public String name;
    public String type = "pie";
    public String radius = "55%";
    public String[] center = {"50%","60%"};
    public List<DataPie> data = new ArrayList<>();
}
