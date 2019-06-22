package bupt.edu.cn.web.util.realtime;

import bupt.edu.cn.web.pojo.Cockpit;
import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.pojo.DiagramSql;
import bupt.edu.cn.web.repository.CockpitRepository;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.DiagramRepository;
import bupt.edu.cn.web.repository.DiagramSQLRepository;
import bupt.edu.cn.web.service.DiagramService;
import bupt.edu.cn.web.service.NewOptionService;
import bupt.edu.cn.web.service.QueryService;
import bupt.edu.cn.web.util.GenerateTable;
import bupt.edu.cn.web.util.StringUtil;
import bupt.edu.cn.web.util.chartsBase;
import bupt.edu.cn.web.util.realtime.SocketServer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    NewOptionService newoptionService;

    @Autowired
    DiagramService diagramService;


    public boolean getAllPath(CockpitListener cpl){
        try {
            Cockpit cp = cockpitRepository.findById(Integer.valueOf(cpl.cockpitId));
            String[] result = cp.getDiagramids().split(",");
            String[] pathResult = new String[result.length];
            for (int i = 0; i < result.length; i++) {
                cpl.diagrams.add(diagramRepository.findByIdEquals(Long.valueOf(result[i])));
                DiagramSql dsql = diagramSQLRepository.findByDiagramid(Long.valueOf(result[i])).get(0);
                cpl.diagramSqls.add(dsql);
                DataSource dataSource = dataSourceRepository.findById(Integer.valueOf(dsql.getDataSourceId())).get();
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
                String tableName = cockpitListener.dataSources.get(i).getFileName().split("\\.")[0];
                if (file.getAbsolutePath().equals(fileUrl)){
                    String sql = cockpitListener.diagramSqls.get(i).getSqlinfo();
                    fileUrl = fileUrl.substring(0, fileUrl.length()-4);
                    List<Map> listJson = queryService.getQueryDataWithDate(fileUrl, tableName, sql);
                    Diagram oldDiagram = cockpitListener.diagrams.get(i);
                    generateOption(listJson, oldDiagram, cockpitListener.diagramSqls.get(i));
                    diagramRepository.saveAndFlush(cockpitListener.diagrams.get(i));
                }
            }
        });
    }

    public void generateOption(List<Map> listJson, Diagram oldDiagram, DiagramSql diagramSql){
        String oldOption = oldDiagram.getChart();
        String clas = oldDiagram.getClassification();
        String rows = diagramSql.getRows();
        String dims = diagramSql.getDims();
        String meas = diagramSql.getMeas();
        List<String> meaArr = new ArrayList<>();
        List<String> funArr = new ArrayList<>();
        String[] funAndMeaArr;
        String[] dimArr = {};
        List<String> mea_fun = new ArrayList<>();

        if(meas != null && !meas.equals("") && !meas.equals(" ")){
            meas = StringUtil.custom_trim(meas,',');
            funAndMeaArr = meas.split(",");
            for (String item : funAndMeaArr) {
                item = StringUtil.custom_trim(item,'.'); //去除首尾'.'
                String[] itemSplit = item.split("\\.");
                funArr.add(itemSplit[0]);
                if (itemSplit.length == 4){         //操作名.数据库名.表名.维度
                    meaArr.add(itemSplit[2]+"."+itemSplit[3]);
                }else if (itemSplit.length == 2){   //操作名.维度
                    meaArr.add(itemSplit[1]);
                }else if (itemSplit.length == 3){   //操作名.表名.维度
                    meaArr.add(itemSplit[1]+"."+itemSplit[2]);
                }
            }
        }
        if(dims != null && !dims.equals("") && !dims.equals(" ")){
            dims = StringUtil.custom_trim(dims,',');
            System.out.println("--------去,后：dims = " + dims);
            dimArr = dims.split(",");
        }
        for (int i = 0;i<meaArr.size();i++){
            mea_fun.add(meaArr.get(i)+"_"+funArr.get(i));
        }
        int int_typeBefore = new chartsBase().getOptionType(new JSONObject(oldOption));     //需要转成的option类型
        if (clas == "-3"){
            String[] rowArr = rows.split(",");
            com.alibaba.fastjson.JSONArray cowJson = new GenerateTable().generateCowJSON(dimArr, rowArr, listJson);
            com.alibaba.fastjson.JSONArray rowJson = new GenerateTable().generateRowJSON(dimArr, meaArr, funArr,rowArr,listJson);
            JSONObject op = new JSONObject();
            op.put("cows",cowJson);
            op.put("rows",rowJson);
            oldDiagram.setChart(op.toString());
        }else {
            JSONObject jo = newoptionService.newcreateOptionSpark(dimArr,mea_fun,listJson);
            int nowType = 0;
            if (clas == "-2"){
                oldDiagram.setChart(jo.toString());
            }else if (clas == "4"){     //从雷达图转为数据库中存的图
                String str_newDiagram = new chartsBase().transDiagram(4,int_typeBefore,jo.toString());
                diagramService.updateDiagram(oldDiagram.getId() + "", oldDiagram.getName(), str_newDiagram, "5", oldDiagram.getUserId() + "");
            }else if (clas == "2"){     //从面积图转为数据库中存的图
                String str_newDiagram = new chartsBase().transDiagram(2,int_typeBefore,jo.toString());
                diagramService.updateDiagram(oldDiagram.getId() + "", oldDiagram.getName(), str_newDiagram, "5", oldDiagram.getUserId() + "");
            }
        }
        oldDiagram.setUpdateTime(new Date());
    }
}
