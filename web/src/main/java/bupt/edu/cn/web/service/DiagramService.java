package bupt.edu.cn.web.service;

import bupt.edu.cn.web.pojo.DataSource;
import bupt.edu.cn.web.pojo.Diagram;
import bupt.edu.cn.web.pojo.DiagramSql;
import bupt.edu.cn.web.repository.DataSourceRepository;
import bupt.edu.cn.web.repository.DiagramSQLRepository;
import bupt.edu.cn.web.util.StringUtil;
import bupt.edu.cn.web.util.realtime.CockpitListener;
import bupt.edu.cn.web.util.realtime.RtAction;
import bupt.edu.cn.web.util.realtime.SocketServer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class DiagramService {

    @Autowired
    bupt.edu.cn.web.repository.DiagramRepository diagramRepository;
    @Autowired
    DiagramSQLRepository diagramSQLRepository;
    @Autowired
    RtAction rtAction;
    @Autowired
    DataSourceRepository dataSourceRepository;
    @Autowired
    QueryService queryService;

    /**
     * 根据diagramId和userId来更新Diagram
     *
     * @param diagramId
     * @param diagramName
     * @param chart
     * @param classificaion
     * @param userId
     * @return
     */
    public Diagram updateDiagram(String diagramId, String diagramName, String chart, String classificaion, String userId) {
        //根据id查找Diagram
        //java8 Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。
        Optional<Diagram> diagram;
        diagram = diagramRepository.findById(Long.valueOf(diagramId));
        //判断是否存在，更新/失败返回
        Diagram newDiagram;
        if (diagram.isPresent()) {
            newDiagram = diagram.get();
            //权限
            if (!newDiagram.getUserId().equals(userId))
                return new Diagram();
        } else {
            return new Diagram();
        }
        //赋值
        newDiagram.setName(diagramName);
        newDiagram.setChart(chart);
        newDiagram.setClassification(classificaion);
        newDiagram.setUpdateTime(new Date());
        diagramRepository.saveAndFlush(newDiagram);
        //返回结果
        return newDiagram;
    }

    /**
     * 新增/覆盖更新Diagram
     * <p>
     * 因为存在覆盖更新，所以很危险，若新建Diagram，请确保diagramId<0
     *
     * @param diagramId
     * @param diagramName
     * @param chart
     * @param classificaion
     * @param userId
     * @param dataSourceId
     * @return
     */
    public Diagram createDiagram(String diagramId, String diagramName, String chart, String classificaion, String userId, String dataSourceId) {
        //根据id查找Diagram
        //java8 Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。
        Optional<Diagram> diagram;
        diagram = diagramRepository.findById(Long.valueOf(diagramId));
        //判断是否存在，新增/覆盖更新
        Diagram newDiagram;
        if (diagram.isPresent()) {
            newDiagram = diagram.get();
        } else {
            newDiagram = new Diagram();
        }
        //赋值
        newDiagram.setName(diagramName);
        newDiagram.setChart(chart);
        newDiagram.setClassification(classificaion);
        newDiagram.setUserId(userId);
        newDiagram.setDataSourceId(dataSourceId);
        newDiagram.setUpdateTime(new Date());
        newDiagram.setSaved("false");
        diagramRepository.saveAndFlush(newDiagram);
        //返回结果
        return newDiagram;
    }

    /**
     * 保存Diagram
     * 根据diagramId和userId修改saved的状态
     *
     * @param diagramId
     * @param userId
     * @return
     */
    public Diagram saveDiagram(String diagramId, String userId, String diagramName, String classificaion, String option) {
        //根据id查找Diagram
        //java8 Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。
        Optional<Diagram> diagram;
        diagram = diagramRepository.findById(Long.valueOf(diagramId));
        //判断是否存在，保存/查询失败
        Diagram newDiagram;
        if (diagram.isPresent()) {
            newDiagram = diagram.get();
            if (!newDiagram.getUserId().equals(userId))
                return new Diagram();
        } else {
            return new Diagram();
        }
        //赋值
        newDiagram.setUpdateTime(new Date());
        newDiagram.setSaved("true");
        newDiagram.setClassification(classificaion);
        newDiagram.setChart(option);
        newDiagram.setName(diagramName);
        diagramRepository.saveAndFlush(newDiagram);
        //返回结果
        return newDiagram;
    }

    /**
     * 判断Diagram对象是否是空
     *
     * @param diagram
     * @return
     */
    public boolean isEmptyDiagram(Diagram diagram) {
        if (diagram == null || diagram.getId() == null) {
            return true;
        }
        return false;
    }

    /**
     * 计算联动图谱
     *
     * @param cockpitId
     * @param diagramId
     * @param condition
     * @return
     */
    public boolean calculationAssoiation(Integer cockpitId, Long diagramId, String condition) {
        // 查找图谱
        Optional<Diagram> optionalDiagram = diagramRepository.findById(diagramId);
        Diagram diagram;
        if (optionalDiagram.isPresent())
            diagram = optionalDiagram.get();
        else
            return false;
        if (StringUtil.isEmpty(diagram.getAssociationIds())) {
            return false;
        }
        DiagramSql dSql = diagramSQLRepository.findByDiagramid(diagramId).get(0);
        String dim = dSql.getDims().split(",")[0];

        // 计算联动图谱
        String[] associationIds = diagram.getAssociationIds().split(",");
        System.out.println("**********" + diagram.getAssociationIds());
        for (int i = 0; i < associationIds.length; i++) {
            // 获取 diagramSql
            DiagramSql diagramSql = diagramSQLRepository.findByDiagramid(Long.valueOf(associationIds[i])).get(0);
            // 拼接新的联动sql
            String sql = diagramSql.getSqlinfo();
            System.out.println("联动前的sql: " + sql);
            String[] arr1 = sql.split("group by");
            String[] arr2 = arr1[0].split("where");
            if (arr2.length == 2) {
                sql = arr2[0] + "where `" + dim + "` = \"" + condition + "\" group by" + arr1[1];
            } else {
                sql = arr1[0] + "where `" + dim + "` = \"" + condition + "\" group by" + arr1[1];
            }
            System.out.println("联动后的sql: " + sql);
            // 修改diagramSql
            diagramSql.setSqlinfo(sql);
            // 准备数据源路径
            DataSource dataSource = dataSourceRepository.findById(Integer.valueOf(diagramSql.getDataSourceId())).get();
            String fileUrl = dataSource.getFileUrl();
            String tableName = dataSource.getFileName().split("\\.")[0];
            fileUrl = fileUrl.substring(0, fileUrl.length() - 4);
            // 计算
            List<Map> listJson = queryService.getQueryDataWithDate(fileUrl, tableName, sql);
            // 更新图谱
            Diagram oldDiagram = diagramRepository.findById(diagramSql.getDiagramid()).get();
            rtAction.generateOption(listJson, oldDiagram, diagramSql);
            System.out.println("-----------");
            diagramRepository.saveAndFlush(oldDiagram);
            diagramSQLRepository.saveAndFlush(diagramSql);
        }
        if (cockpitId != null)
            rtAction.updateCockpit(cockpitId);
        return true;
    }


    /**
     * 获取联动的option
     *
     * @param diagramId
     * @return
     */
    public JSONArray getAssoiation(Long diagramId) {
        // 查找图谱
        Diagram diagram = diagramRepository.findById(diagramId).get();
        String[] diagramArr = diagram.getAssociationIds().split(",");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < diagramArr.length; i++) {
            Diagram assDiagram = diagramRepository.findById(Long.valueOf(diagramArr[i])).get();
            JSONObject item = new JSONObject();
            item.put("option", JSON.parseObject(assDiagram.getChart()));
            item.put("diagramId", assDiagram.getId());
            item.put("diagramName", assDiagram.getName());
            item.put("classificaion", assDiagram.getClassification());
            item.put("userId", assDiagram.getUserId());
            item.put("dataSourceId", assDiagram.getDataSourceId());
            jsonArray.add(item);
        }
        return jsonArray;
    }


}
