package youxianqi.yixi.generated.mapper;

import youxianqi.yixi.generated.model.UserEntity;

public interface UserEntityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table main_user
     *
     * @mbg.generated Mon Jan 20 16:26:49 CST 2020
     */
    int deleteByPrimaryKey(Integer userid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table main_user
     *
     * @mbg.generated Mon Jan 20 16:26:49 CST 2020
     */
    int insert(UserEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table main_user
     *
     * @mbg.generated Mon Jan 20 16:26:49 CST 2020
     */
    int insertSelective(UserEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table main_user
     *
     * @mbg.generated Mon Jan 20 16:26:49 CST 2020
     */
    UserEntity selectByPrimaryKey(Integer userid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table main_user
     *
     * @mbg.generated Mon Jan 20 16:26:49 CST 2020
     */
    int updateByPrimaryKeySelective(UserEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table main_user
     *
     * @mbg.generated Mon Jan 20 16:26:49 CST 2020
     */
    int updateByPrimaryKey(UserEntity record);
}