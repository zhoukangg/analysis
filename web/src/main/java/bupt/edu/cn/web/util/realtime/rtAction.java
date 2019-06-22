package bupt.edu.cn.web.util.realtime;

import bupt.edu.cn.web.pojo.Cockpit;
import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.DiagramSql;
import bupt.edu.cn.web.repository.CockpitRepository;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.DiagramSQLRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class rtAction {
    @Autowired
    CockpitRepository cockpitRepository;

    @Autowired
    DiagramSQLRepository diagramSQLRepository;

    @Autowired
    DataSourceRepository dataSourceRepository;

    public String[] getAllPath(int cockpitId){
        Cockpit cp = cockpitRepository.findAllById(cockpitId).get(0);
        String[] result = cp.getDiagramids().split(",");
        String[] pathResult = new String[result.length];
        for (int i = 0; i < result.length; i++) {
            DiagramSql dsql = diagramSQLRepository.findByDiagramid(Long.valueOf(result[i])).get(0);
            DataSource dataSource = dataSourceRepository.findById(dsql.getId()).get();
            pathResult[i] = dataSource.getFileUrl();
        }
        return pathResult;
    }

}
