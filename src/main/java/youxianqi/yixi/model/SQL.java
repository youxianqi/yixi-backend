package youxianqi.yixi.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import youxianqi.yixi.consts.OrderByDirection;
import youxianqi.yixi.consts.OrderByType;
import youxianqi.yixi.reqres.RequestResourceList;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SQL {

    static private Logger logger = LoggerFactory.getLogger(SQL.class);

    private static final String RES_USER = "SELECT \n" +
            "    res.resource_id as resourceId,\n" +
            "    res.ktree_id as ktreeId,\n" +
            "    res.resource_type as resourceType,\n" +
            "    res.resource_status as resourceStatus,\n" +
            "    res.resource_access_type as resourceAccessType,\n" +
            "    res.content_time as contentTime,\n" +
            "    res.content_title as contentTitle,\n" +
            "    res.content_desc as contentDesc,\n" +
            "    res.content_thumbnail as contentThumbnail,\n" +
            "    res.content_media_type as contentMediaType,\n" +
            "    res.content_count as contentCount,\n" +
            "    res.owner_user_id as ownerUserId,\n" +
            "    u.user_name as ownerUserName,\n" +
            "    u.sex_type as ownerUserSexType,\n" +
            "    u.user_img as ownerUserImg,\n" +
            "    res.views_d as views,\n" +
            "    res.likes_d as likes,\n" +
            "    res.favs_d as favs,\n" +
            "    res.tags_json_d as tagsJson,\n" +
            "    res.local_update_time as localUpdateTime\n" +
            " from main_resource res\n" +
            " left join main_user u on res.owner_user_id = u.user_id \n";

    private static final String WHERE =
            " where \n" +
                    "    res.ktree_id in (:p_ktree_ids)\n" +
                    "    and res.resource_type = :p_resource_type\n" +
                    "    and res.resource_status = :p_resource_status\n" +
                    "    and res.resource_access_type = :p_resource_access_type\n";

    public static final String GET_RES_LIST = RES_USER + WHERE +
            " order by %s limit %d,%d";

    public static final String GET_RES_LIST_BY_OWNER = RES_USER + WHERE +
            " and res.owner_user_id = :p_owner_user_id\n" +
            " order by %s limit %d,%d";

    public static final String GET_RES_LIST_BY_FAV = RES_USER +
            " left join main_resource_user_r fav on res.resource_id = fav.resource_id \n" +
            WHERE +
            " and fav.user_id = :p_fav_user_id\n" +
            " and fav.has_faved = 1\n" +
            " order by %s limit %d,%d";

    public static final String GET_RES_LIST_BY_TAGS = RES_USER +
            " inner join\n" +
            "   (select count(tag.resource_id) as tags, tag.resource_id, tag.tag_id \n" +
            "   from main_resource_user_tag_r tag \n" +
            "   where tag_id in (:p_tag_ids) \n" +
            "   group by tag_id, resource_id) \n" +
            " tags\n" +
            " on tags.resource_id = res.resource_id\n" +
            WHERE +
            " order by %s limit %d,%d";

    @Autowired
    EntityManager em;

    public List<CustomResource> queryResourceList(RequestResourceList params) {
        String orderBy = getOrderBy(params.getOrderByType(), params.getOrderByDirection());
        int offset = 0;
        int limit = 10;
        if (params.getLimit() != null) {
            limit = params.getLimit();
        }
        if (params.getOffset() != null) {
            offset = params.getOffset();
        }
        String sqlTemplate = GET_RES_LIST;
        List<String> lst = Arrays.asList(params.getKtreeIds().split(","));
        List<Integer> ktreeIds = lst.stream().map(x -> Integer.valueOf(x)).collect(Collectors.toList());

        if (params.getOwnerUserId() != null) {
            sqlTemplate = GET_RES_LIST_BY_OWNER;
            String sql = String.format(sqlTemplate, orderBy, offset,limit);
            logger.info("sql..." + sql);
            return em.createNativeQuery(sql, CustomResource.class)
                    .setParameter("p_ktree_ids", ktreeIds)
                    .setParameter("p_resource_type", params.getResourceType())
                    .setParameter("p_resource_status", params.getResourceStatus())
                    .setParameter("p_resource_access_type", params.getResourceAccessType())
                    .setParameter("p_owner_user_id", params.getOwnerUserId())
                    .getResultList();
        }
        else if (params.getFavUserId() != null) {
            sqlTemplate = GET_RES_LIST_BY_FAV;
            String sql = String.format(sqlTemplate, orderBy, offset,limit);
            logger.info("sql..." + sql);
            return em.createNativeQuery(sql, CustomResource.class)
                    .setParameter("p_ktree_ids", ktreeIds)
                    .setParameter("p_resource_type", params.getResourceType())
                    .setParameter("p_resource_status", params.getResourceStatus())
                    .setParameter("p_resource_access_type", params.getResourceAccessType())
                    .setParameter("p_fav_user_id", params.getFavUserId())
                    .getResultList();
        }
        else if (params.getTagIds() != null) {
            sqlTemplate = GET_RES_LIST_BY_TAGS;
            String sql = String.format(sqlTemplate, orderBy, offset,limit);
            logger.info("sql..." + sql);
            return em.createNativeQuery(sql, CustomResource.class)
                    .setParameter("p_ktree_ids", ktreeIds)
                    .setParameter("p_resource_type", params.getResourceType())
                    .setParameter("p_resource_status", params.getResourceStatus())
                    .setParameter("p_resource_access_type", params.getResourceAccessType())
                    .setParameter("p_tag_ids", params.getTagIds())
                    .getResultList();
        }
        else {
            sqlTemplate = GET_RES_LIST;
            String sql = String.format(sqlTemplate, orderBy, offset,limit);
            return em.createNativeQuery(sql, CustomResource.class)
                    .setParameter("p_ktree_ids", ktreeIds)
                    .setParameter("p_resource_type", params.getResourceType())
                    .setParameter("p_resource_status", params.getResourceStatus())
                    .setParameter("p_resource_access_type", params.getResourceAccessType())
                    .getResultList();
        }
    }

    private static String getOrderBy(Integer orderByType, Integer orderByDirection) {

        String direction = " desc ";

        if (orderByDirection != null && orderByDirection.equals(OrderByDirection.ASC.getValue())) {
            direction = "";
        }

        String orderBy = "res.content_time";

        if (orderByType == null)
            return orderBy + direction;

        if (orderByType == OrderByType.CONTENT_TIME.getValue()) {
            orderBy = "res.content_time";
        }
        else if (orderByType == OrderByType.UPDATE_TIME.getValue()) {
            orderBy = "res.local_update_time";
        }
        else if (orderByType == OrderByType.VIEWS.getValue()) {
            orderBy = "res.views_d";
        }
        else if (orderByType == OrderByType.LIKES.getValue()) {
            orderBy = "res.likes_d";
        }
        else if (orderByType == OrderByType.FAVS.getValue()) {
            orderBy = "res.favs_d";
        }
        else if (orderByType == OrderByType.TAGS.getValue()) {
            orderBy = "tags.tags";
        }
        else if (orderByType == OrderByType.ACCESS_TYPE.getValue()) {
            orderBy = "res.resource_access_type";
        }
        return orderBy + direction;
    }
}
