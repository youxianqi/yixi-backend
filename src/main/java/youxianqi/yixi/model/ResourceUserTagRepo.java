package youxianqi.yixi.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceUserTagRepo extends JpaRepository<MainResourceUserTagR, Long> {
    MainResourceUserTagR findOneByResourceIdAndUserIdAndTagId(int resourceId, int userId, int tagId);
    List<MainResourceUserTagR> findByResourceIdAndUserId(int resourceId, int userId);
    void deleteByResourceIdAndUserId(int resourceId, int userId);
    void deleteByResourceId(int resourceId);
}