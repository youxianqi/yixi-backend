package youxianqi.yixi.model;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import youxianqi.yixi.model.MainUser;

@Repository
public interface UserRepo extends JpaRepository<MainUser, Long> {
    MainUser findByUserName(String userName);
}