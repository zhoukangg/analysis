package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.Cockpit;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CockpitRepository extends JpaRepository<Cockpit, Long>{
    @Override
    List<Cockpit> findAllById(Iterable<Long> iterable);
    Cockpit findById(int CockId);
    List<Cockpit> findAllByUserId(String userId);

    @Transactional
    @Modifying
    @Query(value = "update cockpit set name=?1, diagramIDs=?2, info=?3 where id=?4", nativeQuery = true)
    public void updateCockpit(String name, String diagramIDs, String desc, int id);

    @Transactional
    void deleteById(Integer CockId);
}
