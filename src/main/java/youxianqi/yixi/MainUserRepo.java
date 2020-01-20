package youxianqi.yixi;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import youxianqi.yixi.generated.MainUser;

@Repository
public interface MainUserRepo extends JpaRepository<MainUser, Long> {
}