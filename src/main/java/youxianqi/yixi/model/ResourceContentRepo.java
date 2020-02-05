package youxianqi.yixi.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ResourceContentRepo extends JpaRepository<MainResourceContent, Integer> {
    List<MainResourceContent> findByResourceId(int resourceId);
    void deleteByResourceId(int resourceId);
}