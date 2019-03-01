package bupt.edu.cn.web.chartsmodel;

import java.util.List;

/**
 * Created by PC on 2017/5/15.
 */
public class NameValues {
    public  NameValues(List<Object> value, String name ){
        this.name = name;
        this.value = value;
    }

    public List<Object> value;
    public String name;

}
