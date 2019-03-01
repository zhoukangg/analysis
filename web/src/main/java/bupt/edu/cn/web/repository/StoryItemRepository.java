package bupt.edu.cn.web.repository;

import bupt.edu.cn.web.pojo.StoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StoryItemRepository extends JpaRepository<StoryItem,Long> {
    List<StoryItem> findByStoryId(String storyId);
    List<StoryItem> findByDiagramIdAndDescription(String diagramId,String description);
    @Transactional
    @Modifying
    @Query("delete from StoryItem  where storyId=?1")
    public int deleteByStoryId(String userId);

}
