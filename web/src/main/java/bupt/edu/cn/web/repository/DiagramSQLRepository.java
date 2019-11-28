package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.DiagramSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagramSQLRepository extends JpaRepository<DiagramSql, Long> {

    List<DiagramSql> findByDiagramid(Long id);
}
