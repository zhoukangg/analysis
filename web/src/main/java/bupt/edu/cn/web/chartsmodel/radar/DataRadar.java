package bupt.edu.cn.web.chartsmodel.radar;

import java.util.ArrayList;
import java.util.List;

public class DataRadar {
    public List<String> value;
    public String name;
    public DataRadar(){}
    public DataRadar(List<String> value, String name){
        this.value = value;
        this.name = name;
    }
}
