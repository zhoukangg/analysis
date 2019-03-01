package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DataSourceRepository extends JpaRepository<DataSource,Integer> {
    List<DataSource> findByFileNameEquals(String name);
    List<DataSource> findByFileUrl(String fileUrl);
    List<DataSource> findByFileType(String filetype);

    @Transactional
    @Modifying
    @Query("delete from DataSource  where fileUrl=?1 and fileType=?2")
    public int deleteByFileUrlAndFileType(String fileUrl,String fileType);

    @Transactional
    @Modifying
    @Query("delete from DataSource  where fileType=?1")
    public int deleteByFileType(String fileType);

}
