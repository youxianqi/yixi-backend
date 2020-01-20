package youxianqi.yixi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import youxianqi.yixi.generated.CustomEntity;
import youxianqi.yixi.generated.MainUser;

import java.util.List;

public interface QueryRepo extends JpaRepository<MainUser, Integer> {
    @Query(nativeQuery = true, value="SELECT t1.UserId, t1.UserName, t2.ResourceId, t2.Title FROM main_user t1 \n" +
                    "LEFT JOIN main_resource t2 ON t1.UserId = t2.OwnerUserId \n" +
                    "WHERE t1.UserId=:userid \n" +
                    "ORDER BY t1.UserId desc")
    List<CustomEntity> findList1(@Param("userid") Integer userId);
}