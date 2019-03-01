package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryRepository  extends JpaRepository<Story,Long> {
    List<Story> findByUserId(String userId);
}
