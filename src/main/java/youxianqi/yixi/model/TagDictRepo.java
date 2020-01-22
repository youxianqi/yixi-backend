package youxianqi.yixi.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagDictRepo extends JpaRepository<MainTagDict, Long> {
    MainTagDict findByTagNameAndTagResourceType(String tagName, Byte tagResourceType);
}