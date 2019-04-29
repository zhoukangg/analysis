package bupt.edu.cn.web.controller;

import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.pojo.Dashboard;
import bupt.edu.cn.web.repository.DashboardRepository;
import com.cn.bupt.cad.bigdataroles.annotation.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * graphIds 当作graphs用吧 ， 懒得改了
 */
@RestController
public class DashboardController {

    @Autowired
    DashboardRepository dashboardRepository;

    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping("/saveDashboard")
    public ReturnModel saveDashboard(String dashboardId,String graphs, String userId, String name, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------saveDashboard-----------");
        System.out.println("dashboardId = "+dashboardId);
        System.out.println("graphs = "+graphs);
        System.out.println("userId = "+userId);
        System.out.println("name = "+name);
        ReturnModel result = new ReturnModel();
        //根据id查找Dashboard
        //java8 Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。
        Optional<Dashboard> dashboard;
        try{
            dashboard = dashboardRepository.findById(Long.valueOf(dashboardId));
        }catch (Exception e){
            result.setResult(false);
            result.setReason("saveDashboard 出错！");
            return result;
        }
        //判断是否存在，新增/更新
        Dashboard newDashboard;
        if (dashboard.isPresent()){
            newDashboard = dashboard.get();
        }else {
            newDashboard = new Dashboard();
        }
        //赋值
        newDashboard.setId(Long.valueOf(dashboardId));
        newDashboard.setGraphIds(graphs);
        newDashboard.setUserId(userId);
        newDashboard.setName(name);
        dashboardRepository.saveAndFlush(newDashboard);
        //返回结果
        result.setResult(true);
        return result;
    }

    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping("/getALLDashboard")
    public ReturnModel getALLDashboard(HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        //返回所有Dashboard
        ReturnModel result = new ReturnModel();
        result.setResult(true);
        result.setDatum(dashboardRepository.findAll());
        return result;
    }

    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping("/getDashboardById")
    public ReturnModel getDashboardById(String id, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        //返回该Dashboard
        ReturnModel result = new ReturnModel();
        try
        {
            result.setDatum(dashboardRepository.findById(Long.valueOf(id)).get());
            result.setResult(true);
        }catch (Exception e){
            System.out.println("错误："+e.toString());
            result.setResult(false);
            result.setReason("getDashboardById 出错！");
        }
        return result;
    }

    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping("/getDashboardByUserId")
    public ReturnModel getDashboardByUserId(String userId, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("----------getDashboardByUserId-----------");
        System.out.println("userId = "+userId);
        //返回该用户的所有Dashboard
        ReturnModel result = new ReturnModel();
        try
        {
            result.setDatum(dashboardRepository.findByUserId(userId));
            result.setResult(true);
        }catch (Exception e){
            System.out.println("错误："+e.toString());
            result.setResult(false);
            result.setReason("getDashboardByUserId 出错！");
        }
        return result;
    }

    @Auth(roles={"数据分析师","超级管理员"})
    @RequestMapping("/deleteDashboardById")
    public ReturnModel deleteDashboardById(String id, HttpServletResponse response, HttpServletRequest request) {
        // 解决Ajax跨域请求问题
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        //返回该Dashboard
        ReturnModel result = new ReturnModel();
        try {
            dashboardRepository.deleteById(Long.valueOf(id));
            result.setResult(true);
        }catch (Exception e){
            System.out.println("错误："+e.toString());
            result.setResult(false);
            result.setReason("Dashboard 删除出错！");
        }
        return result;
    }

}
