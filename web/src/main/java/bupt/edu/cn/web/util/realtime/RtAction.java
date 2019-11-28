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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class RtAction {
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
                System.out.println("开始任务" + i);
                cpl.diagrams.add(diagramRepository.findByIdEquals(Long.valueOf(result[i])));
                DiagramSql dsql = diagramSQLRepository.findByDiagramid(Long.valueOf(result[i])).get(0);
                cpl.diagramSqls.add(dsql);
                System.out.println("----------------" + dsql.getDataSourceId());
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
                System.out.println("开始文件改变任务" + i);
                String fileUrl = cockpitListener.dataSources.get(i).getFileUrl();
                String tableName = cockpitListener.dataSources.get(i).getFileName().split("\\.")[0];
                if (file.getAbsolutePath().equals(fileUrl)){
                    String sql = cockpitListener.diagramSqls.get(i).getSqlinfo();
                    fileUrl = fileUrl.substring(0, fileUrl.length()-4);
                    List<Map> listJson = queryService.getQueryDataWithDate(fileUrl, tableName, sql);
                    Diagram oldDiagram = cockpitListener.diagrams.get(i);
                    generateOption(listJson, oldDiagram, cockpitListener.diagramSqls.get(i));
                    System.out.println("-----------");
                    System.out.println(cockpitListener.diagrams.get(i).getChart());
                    diagramRepository.saveAndFlush(cockpitListener.diagrams.get(i));
                }
            }
            updateCockpit(cockpitListener.cockpitId);
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
        if (clas.equals("-3")){
            String[] rowArr = rows.split(",");
            com.alibaba.fastjson.JSONArray cowJson = new GenerateTable().generateCowJSON(dimArr, rowArr, listJson);
            com.alibaba.fastjson.JSONArray rowJson = new GenerateTable().generateRowJSON(dimArr, meaArr, funArr,rowArr,listJson);
            JSONObject op = new JSONObject();
            op.put("cows",cowJson);
            op.put("rows",rowJson);
            op.put("row", rows);
            oldDiagram.setChart(op.toString());
        }else {
            int int_typeBefore = new chartsBase().getOptionType(new JSONObject(oldOption));     //需要转成的option类型
            JSONObject jo = newoptionService.newCreateOption(dimArr,mea_fun,listJson);
            System.out.println("+++++++++");
            System.out.println(jo);
            System.out.println(clas + " " + int_typeBefore);
            if (clas.equals("-2")){
                System.out.println("+++++++++");
                oldDiagram.setChart(jo.toString());
            }else if (clas.equals("4")){     //从雷达图转为数据库中存的图
                String str_newDiagram = new chartsBase().transDiagram(4,int_typeBefore,jo.toString());
                System.out.println("------");
                jo = new JSONObject(str_newDiagram);
//                diagramService.updateDiagram(oldDiagram.getId() + "", oldDiagram.getName(), str_newDiagram, "5", oldDiagram.getUserId() + "");
            }else if (clas.equals("2")){     //从面积图转为数据库中存的图
                String str_newDiagram = new chartsBase().transDiagram(2,int_typeBefore,jo.toString());
//                diagramService.updateDiagram(oldDiagram.getId() + "", oldDiagram.getName(), str_newDiagram, "5", oldDiagram.getUserId() + "");
                System.out.println("=-=-=-=-=-=");
                System.out.println(jo);
                jo = new JSONObject(str_newDiagram);
            }
            oldDiagram.setChart(jo.toString());
        }
        oldDiagram.setUpdateTime(new Date());
    }

    public void updateCockpit(int cockpitId){
        Cockpit cockpit = cockpitRepository.findById(cockpitId);
        JSONArray layoutConf = new JSONArray(cockpit.getLayoutconf());
        JSONArray tableDashboard = new JSONArray(cockpit.getTabledashboard());
        String[] diagramsIDs = cockpit.getDiagramids().split(",");
        int m = 0;      // layoutConf索引
        int n = 0;      // tableDashboard索引
        JSONArray layoutArr = new JSONArray();
        JSONArray tableArr = new JSONArray();
        for (int i = 0; i < diagramsIDs.length; i++){
            Diagram temp = diagramRepository.findByIdEquals(Long.valueOf(diagramsIDs[i]));
            if (temp.getClassification().equals("-3")){     //遍历tableDashboard
                System.out.println("TABLE");
                JSONObject jot = tableDashboard.getJSONObject(n);
                jot.put("tabOption", new JSONObject(temp.getChart()));
                tableArr.put(jot);
                n++;
            }else {
                System.out.println("CHART");
                JSONObject jot2 = layoutConf.getJSONObject(m);
                jot2.put("choseOption", new JSONObject(temp.getChart()));
                layoutArr.put(jot2);
                m++;
            }
        }
        cockpit.setTabledashboard(tableArr.toString());
        cockpit.setLayoutconf(layoutArr.toString());
        cockpitRepository.saveAndFlush(cockpit);
    }
}
