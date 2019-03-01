package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard,Long> {
    @Override
    List<Dashboard> findAllById(Iterable<Long> iterable);

    List<Dashboard> findByUserId(String userId);
}
