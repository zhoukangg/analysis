package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.FaltTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaltTableRepository extends JpaRepository<FaltTable,Long> {

    List<FaltTable> findByModelAndProject(String model,String project);
    List<FaltTable> findByNameAndTableSql(String name, String sql);
    List<FaltTable> findByName(String name);

}
