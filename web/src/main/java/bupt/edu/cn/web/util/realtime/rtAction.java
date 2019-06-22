package bupt.edu.cn.web.util.realtime;

import bupt.edu.cn.web.pojo.Cockpit;
import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.pojo.DiagramSql;
import bupt.edu.cn.web.repository.CockpitRepository;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.DiagramRepository;
import bupt.edu.cn.web.repository.DiagramSQLRepository;
import bupt.edu.cn.web.service.QueryService;
import bupt.edu.cn.web.util.realtime.SocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class rtAction {
    @Autowired
    CockpitRepository cockpitRepository;

    @Autowired
    DiagramSQLRepository diagramSQLRepository;

    @Autowired
    DataSourceRepository dataSourceRepository;

    @Autowired
    DiagramRepository diagramRepository;

    @Autowired
    QueryService queryService;

    public boolean getAllPath(CockpitListener cpl){
        try {
            Cockpit cp = cockpitRepository.findById(Integer.valueOf(cpl.cockpitId));
            String[] result = cp.getDiagramids().split(",");
            String[] pathResult = new String[result.length];
            for (int i = 0; i < result.length; i++) {
                cpl.diagrams.add(diagramRepository.findByIdEquals(Long.valueOf(result[i])));
                DiagramSql dsql = diagramSQLRepository.findByDiagramid(Long.valueOf(result[i])).get(0);
                cpl.diagramSqls.add(dsql);
                DataSource dataSource = dataSourceRepository.findById(dsql.getId()).get();
                cpl.dataSources.add(dataSource);
                pathResult[i] = dataSource.getFileUrl();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void ChangeOption(File file){
        CopyOnWriteArraySet<CockpitListener> socketServers = SocketServer.getSocketServers();
        socketServers.forEach(cockpitListener ->{
            for (int i = 0; i < cockpitListener.diagrams.size(); i++) {
                String fileUrl = cockpitListener.dataSources.get(i).getFileUrl();
                String tableName = cockpitListener.dataSources.get(i).getFileName();
                if (file.getAbsolutePath().equals(fileUrl)){
                    String sql = cockpitListener.diagramSqls.get(i).getSqlinfo();
                    List<Map> listJson = queryService.getQueryDataWithDate(fileUrl, tableName, sql);
                    Diagram oldDiagram = cockpitListener.diagrams.get(i);
                    diagramRepository.saveAndFlush(cockpitListener.diagrams.get(i));
                }
            }
        });
    }

    public String generateOption(List<Map> listJson, Diagram oldDiagram){
        String oldOption = oldDiagram.getChart();
        String clas = oldDiagram.getClassification();
        String option = "";
        if (clas == "-2"){

        }else if (clas == "4"){

        }else if (clas == "2"){

        }else if (clas == "-3"){

        }

        oldDiagram.setChart(option);
        return option;
    }
}
