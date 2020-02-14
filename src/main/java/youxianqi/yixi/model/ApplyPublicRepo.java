package youxianqi.yixi.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplyPublicRepo extends JpaRepository<MainApplyPublic, Long> {
}