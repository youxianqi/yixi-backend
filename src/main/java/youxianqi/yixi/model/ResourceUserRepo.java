package youxianqi.yixi.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceUserRepo extends JpaRepository<MainResourceUserR, Long> {
    MainResourceUserR findOneByResourceIdAndUserId(int resourceId, int userId);
    void deleteByResourceId(int resourceId);
}