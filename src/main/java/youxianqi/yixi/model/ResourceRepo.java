package youxianqi.yixi.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ResourceRepo extends JpaRepository<MainResource, Integer> {

    @Query(nativeQuery = true, value= SQL.TEST )
    List<CustomUser> getCustomUserList(@Param("p_conditions") String userid);

    @Query(nativeQuery = true, value= SQL.GET_RES_LIST )
    List<CustomResource> getResourceList(
            @Param("p_ktree_ids") String ktreeIds,
            @Param("p_resource_type") int resourceType,
            @Param("p_resource_status") int resourceStatus,
            @Param("p_resource_access_type") int resourceAccessType);


    @Query(nativeQuery = true, value= SQL.GET_RES_LIST_BY_OWNER)
    List<CustomResource> getResourceListByOwner(
            @Param("p_ktree_ids") String ktreeIds,
            @Param("p_resource_type") int resourceType,
            @Param("p_resource_status") int resourceStatus,
            @Param("p_resource_access_type") int resourceAccessType,
            @Param("p_owner_user_id") int ownerUserId);


    @Query(nativeQuery = true, value= SQL.GET_RES_LIST_BY_FAV)
    List<CustomResource> getResourceListByFav(
            @Param("p_ktree_ids") String ktreeIds,
            @Param("p_resource_type") int resourceType,
            @Param("p_resource_status") int resourceStatus,
            @Param("p_resource_access_type") int resourceAccessType,
            @Param("p_fav_user_id") int favUserId);

    @Query(nativeQuery = true, value= SQL.GET_RES_LIST_BY_TAGS)
    List<CustomResource> getResourceListByTags(
            @Param("p_ktree_ids") String ktreeIds,
            @Param("p_resource_type") int resourceType,
            @Param("p_resource_status") int resourceStatus,
            @Param("p_resource_access_type") int resourceAccessType,
            @Param("p_tag_ids") String tagIds);
}