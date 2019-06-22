package bupt.edu.cn.web.util.realtime;


import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.pojo.DiagramSql;
import org.apache.commons.io.monitor.FileAlterationMonitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @program analysis
 * @description: ${TODO}
 * @author: kang
 * @create: 2019/06/22 13:14
 */
public class CockpitListener {

    public int cockpitId;

    public Client client;

    public List<Diagram> diagrams;

    public List<DiagramSql> diagramSqls;

    public List<DataSource> dataSources;


    public CockpitListener(){
        this.cockpitId = -1;
        this.diagrams = new ArrayList<>();
        this.dataSources = new ArrayList<>();
        this.diagramSqls = new ArrayList<>();
    }

    public CockpitListener(Client client){
        this.cockpitId = client.getCockpitId();
        this.client = client;
        this.diagrams = new ArrayList<>();
        this.dataSources = new ArrayList<>();
        this.diagramSqls = new ArrayList<>();
    }

    public CockpitListener(int cockpitId,Client client){
        this.cockpitId = cockpitId;
        this.client = client;
        this.diagrams = new ArrayList<>();
        this.dataSources = new ArrayList<>();
        this.diagramSqls = new ArrayList<>();
    }

}
