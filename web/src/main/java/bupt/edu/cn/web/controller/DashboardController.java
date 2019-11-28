package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.pojo.Cockpit;
import bupt.edu.cn.web.repository.CockpitRepository;
import bupt.edu.cn.web.repository.DiagramRepository;
import bupt.edu.cn.web.service.DiagramService;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 图表报告控制类（驾驶舱）
 * graphIds 记录图表的id列表
 *
 * @Author:zk
 */
@RestController
public class DashboardController {

    @Autowired
    CockpitRepository cockpitRepository;
    @Autowired
    DiagramRepository diagramRepository;
    @Autowired
    DiagramService diagramService;

    /**
     * 保存驾驶舱接口
     *
     * @param cockpitId
     * @param diagramIDs
     * @param name
     * @param userId
     * @param info
     * @param layoutConf
     * @param realtime
     * @param tableDashboard
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(value = "/saveCockpit", method = RequestMethod.POST)
    public ReturnModel saveCockpit(String cockpitId, String diagramIDs, String name, String userId, String info, String layoutConf, Boolean realtime, String tableDashboard,
                                   HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------saveCockpit-----------");
        System.out.println("cockpitId = " + cockpitId);
        System.out.println("diagramsIDs = " + diagramIDs);
        System.out.println("name = " + name);
        System.out.println("info = " + info);
        System.out.println("userId = " + userId);
        ReturnModel result = new ReturnModel();
        Cockpit cockpit = new Cockpit();
        if (cockpitId == "" || cockpit.equals("")) {        //传空的时候为0，为增加一个Cockpit
            cockpit.setUserId(userId);
        } else {
            try {
                cockpit = cockpitRepository.findById(Integer.valueOf(cockpitId));
            } catch (Exception e) {
                result.setResult(false);
                result.setReason(e.toString());
                return result;
            }
        }
        cockpit.setName(name);
        cockpit.setDiagramids(diagramIDs);
        cockpit.setInfo(info);
        cockpit.setLayoutconf(layoutConf);
        cockpit.setRealtime(realtime);
        cockpit.setTabledashboard(tableDashboard);
        cockpitRepository.saveAndFlush(cockpit);
        result.setResult(true);
        return result;
    }

    /**
     * 根据用户ID获取驾驶舱接口
     *
     * @param userId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getCockpitByUserId")
    public ReturnModel getCockpitByUserId(String userId, HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------getCockpitByUserId-----------");
        System.out.println("userId = " + userId);
        ReturnModel result = new ReturnModel();
        try {
            result.setDatum(cockpitRepository.findAllByUserId(userId));
            result.setResult(true);
        } catch (Exception e) {
            result.setResult(false);
            result.setReason(e.toString());
        }
        return result;
    }

    /**
     * 根据ID删除驾驶舱接口
     *
     * @param cockpitId
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/deleteCockpitById")
    public ReturnModel deleteCockpitById(int cockpitId, HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        ReturnModel result = new ReturnModel();
        try {
            cockpitRepository.deleteById(cockpitId);
            result.setResult(true);
        } catch (Exception e) {
            result.setResult(false);
            result.setReason(e.toString());
        }
        return result;
    }

    /**
     * 设置驾驶舱属性为实时更新
     *
     * @param cockpitId
     * @param isRealTime
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/setCockpitRealTime")
    public ReturnModel setCockpitRealTime(int cockpitId, boolean isRealTime, HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        ReturnModel result = new ReturnModel();
        try {
            cockpitRepository.updataRealTime(isRealTime, cockpitId);
            result.setResult(true);
        } catch (Exception e) {
            result.setResult(false);
            result.setReason(e.toString());
        }
        return result;
    }

    /**
     * 根据ID获取驾驶舱
     *
     * @param cockpitId
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/getCockpitById")
    public ReturnModel getCockpitById(int cockpitId, HttpServletResponse response, HttpServletRequest request) {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        ReturnModel result = new ReturnModel();
        Cockpit cockpit = new Cockpit();
        try {
            cockpit = cockpitRepository.findById(cockpitId);
            result.setResult(true);
        } catch (Exception e) {
            result.setResult(false);
            result.setReason(e.toString());
        }
        result.setDatum(cockpit);
        return result;
    }

    /**
     * 多图联动接口
     *
     * @param cockpitId
     * @param diagramId
     * @param condition
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/getDiagramAssociation")
    public ReturnModel getDiagramAssociation(Integer cockpitId, Long diagramId, String condition, HttpServletResponse response, HttpServletRequest request) {
        System.out.println("*********getDiagramAssociation方法");
        System.out.println("*********diagramId:" + diagramId);
        System.out.println("*********condition:" + condition);
        ReturnModel returnModel = new ReturnModel();
        try {
            Boolean flag = diagramService.calculationAssoiation(cockpitId, diagramId, condition);
            if (flag) {
                JSONArray jsonArray = diagramService.getAssoiation(diagramId);
                returnModel.setDatum(jsonArray);
            }
            returnModel.setResult(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnModel;
    }

    /**
     * 测试默认参数
     *
     * @param cockpitId
     * @param diagramId
     * @param condition
     * @return
     */
    @RequestMapping("/testDefaultValue")
    public ReturnModel testDefaultValue(@RequestParam(defaultValue = "-100") int cockpitId,
                                        Long diagramId,
                                        String condition) {
        System.out.println(cockpitId);
        System.out.println(diagramId);
        System.out.println(condition);
        return new ReturnModel();
    }

}
