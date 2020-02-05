package youxianqi.yixi.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceUserTagRepo extends JpaRepository<MainResourceUserTagR, Long> {
    MainResourceUserTagR findOneByResourceIdAndUserIdAndTagId(int resourceId, int userId, int tagId);
    void deleteByResourceId(int resourceId);
}