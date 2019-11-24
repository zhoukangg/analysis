package bupt.edu.cn.web.service;

import bupt.edu.cn.web.common.ReturnModel;
import bupt.edu.cn.web.pojo.Dashboard;
import bupt.edu.cn.web.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    DashboardRepository dashboardRepository;

    public Boolean saveDashboard(String dashboardId, String graphIds, String userId, String name) {
        //根据id查找Dashboard
        //java8 Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。
        Optional<Dashboard> dashboard;
        try {
            dashboard = dashboardRepository.findById(Long.valueOf(dashboardId));
        } catch (Exception e) {
            return false;
        }
        //判断是否存在，新增/更新
        Dashboard newDashboard;
        if (dashboard.isPresent()) {
            newDashboard = dashboard.get();
        } else {
            newDashboard = new Dashboard();
        }
        //赋值
        newDashboard.setId(Long.valueOf(dashboardId));
        newDashboard.setGraphIds(graphIds);
        newDashboard.setUserId(userId);
        newDashboard.setName(name);
        dashboardRepository.saveAndFlush(newDashboard);
        //返回结果
        return true;
    }

    public ReturnModel getALLDashboard() {
        //返回所有Dashboard
        ReturnModel result = new ReturnModel();
        result.setResult(true);
        result.setDatum(dashboardRepository.findAll());
        return result;
    }

    public ReturnModel getDashboardById(String id) {
        //返回该Dashboard
        ReturnModel result = new ReturnModel();
        result.setResult(true);
        result.setDatum(dashboardRepository.findById(Long.valueOf(id)).get());
        return result;
    }

    public ReturnModel getDashboardByUserId(String userId) {
        //返回该用户的所有Dashboard
        ReturnModel result = new ReturnModel();
        result.setResult(true);
        result.setDatum(dashboardRepository.findByUserId(userId));
        return result;
    }

}
