package bupt.edu.cn.web.service;

import bupt.edu.cn.web.pojo.Diagram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class DiagramService {

    @Autowired
    bupt.edu.cn.web.repository.DiagramRepository diagramRepository;

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

}
