package bupt.edu.cn.web.util.realtime;

import bupt.edu.cn.web.pojo.Cockpit;
import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.DiagramSql;
import bupt.edu.cn.web.repository.CockpitRepository;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.DiagramRepository;
import bupt.edu.cn.web.repository.DiagramSQLRepository;
import bupt.edu.cn.web.util.realtime.CockpitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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
}
