package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.Diagram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagramRepository extends JpaRepository<Diagram,Long> {

    @Override
    List<Diagram> findAllById(Iterable<Long> iterable);

    List<Diagram> findByUserId(String userId);
    List<Diagram> findByUserIdAndSaved(String userId,String saved);
    Diagram findByIdAndUserId(Long id,String userId);

}
