package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.Cockpit;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface CockpitRepository extends JpaRepository<Cockpit, Long>{

    public Cockpit findById(int CockId);
    List<Cockpit> findAllByUserId(String userId);
    List<Cockpit> findAllById(Long cockpitId);

    @Transactional
    @Modifying
    @Query(value = "update cockpit set name=?1, diagramIDs=?2, info=?3 where id=?4", nativeQuery = true)
    public void updateCockpit(String name, String diagramIDs, String desc, int id);

    @Transactional
    @Modifying
    @Query(value = "update cockpit set realtime=?1 where id=?2", nativeQuery = true)
    public void updataRealTime(boolean isRealTime, int id);

    @Transactional
    @Query(value = "select * from cockpit where id=?1", nativeQuery = true)
    public List<Cockpit> getCockpitById(int id);

    @Transactional
    void deleteById(Integer CockId);
}
