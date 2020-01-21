package youxianqi.yixi.model;

public class SQL {
    public static final String TEST =
            " SELECT t1.user_id as userId, t1.user_name as userName, t2.resource_id as resourceId, t2.title as title FROM main_user t1 \n" +
                    " LEFT JOIN main_resource t2 ON t1.user_id = t2.owner_user_id \n" +
                    " WHERE t1.user_id in (:p_conditions) \n" +
                    " ORDER BY t2.resource_id desc";

    private static final String RES_USER = "SELECT \n" +
            "    res.resource_id as resourceId,\n" +
            "    res.ktree_id as ktreeId,\n" +
            "    res.resource_type as resourceType,\n" +
            "    res.resource_status as resourceStatus,\n" +
            "    res.resource_access_type as resourceAccessType,\n" +
            "    res.data_time as dataTime,\n" +
            "    res.title as title,\n" +
            "    res.description as description,\n" +
            "    res.thumbnail_url as thumbnailUrl,\n" +
            "    res.content as content,\n" +
            "    res.content_type as contentType,\n" +
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
            " order by res.data_time desc";

    public static final String GET_RES_LIST_BY_OWNER = RES_USER + WHERE +
            " and res.owner_user_id = :p_owner_user_id\n" +
            " order by res.data_time desc";

    public static final String GET_RES_LIST_BY_FAV = RES_USER +
            " left join main_resource_user_r fav on res.resource_id = fav.resource_id \n" +
            WHERE +
            " and fav.user_id = :p_fav_user_id\n" +
            " and fav.has_faved = 1\n" +
            " order by res.data_time desc";

    public static final String GET_RES_LIST_BY_TAGS = RES_USER +
            " inner join\n" +
            "   (select count(tag.resource_id) as tags, tag.resource_id, tag.tag_id \n" +
            "   from main_resource_user_tag_r tag \n" +
            "   where tag_id in (:p_tag_ids) \n" +
            "   group by tag_id, resource_id) \n" +
            " tags\n" +
            " on tags.resource_id = res.resource_id\n" +
            WHERE +
            " order by tags.tags desc";
}
