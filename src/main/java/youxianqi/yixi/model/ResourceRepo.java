package youxianqi.yixi.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ResourceRepo extends JpaRepository<MainResource, Integer> {
//    @Query(nativeQuery = true, value= SQL.GET_RES_LIST )
//    List<CustomResource> getResourceList(
//            @Param("p_ktree_ids") String ktreeIds,
//            @Param("p_resource_type") int resourceType,
//            @Param("p_resource_status") int resourceStatus,
//            @Param("p_resource_access_type") int resourceAccessType);
}