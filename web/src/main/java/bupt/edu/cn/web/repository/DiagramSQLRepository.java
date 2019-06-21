package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.DiagramSql;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DiagramSQLRepository extends JpaRepository<DiagramSql, Long> {

}
