package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.DrillDim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrillDimRepository extends JpaRepository<DrillDim,Integer> {
    DrillDim findByDimsEqualsAndTablenameEquals(String dims,String tablename);
    DrillDim findByIdEquals(Long id);
    List<DrillDim> findByTablenameEquals(String tabName);

}
