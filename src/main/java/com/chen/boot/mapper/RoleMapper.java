package com.chen.boot.mapper;

import com.chen.boot.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chen
 * @description 针对表【role】的数据库操作Mapper
 * @createDate 2022-06-14 22:09:32
 * @Entity com.chen.boot.entity.Role
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select auth_id from inner_role_auth where role_id = #{roleId}")
    List<Integer> queryAssignedAuthIdByRoleId(Integer roleId);

    /*
    分配权限之前，删除之前所分配的所有权限
    【根据role_id删除所有的对应的auth_id】
     */
    @Delete("delete from inner_role_auth where role_id = #{roleId}")
    void deleteAssignedAuthByRoleId(Integer roleId);

    /*
    保存分配
    【根据 role_id 和 auth_id 的关系保存到 inner_role_auth 表中】
     */
    @Insert("insert into inner_role_auth values (null,#{roleId},#{authId})")
    void saveRoleAuthRelationship(Integer roleId, Integer authId);

}




