package com.chen.boot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import com.chen.boot.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author chen
 * @description 针对表【admin】的数据库操作Mapper
 * @createDate 2022-06-08 17:09:50
 * @Entity com.chen.boot.entity.User
 */
@Repository
public interface AdminMapper extends BaseMapper<Admin> {
    List<Admin> getByUserName(@Param("userName") String userName);

    @Select("select role_id from inner_admin_role where admin_id=#{adminId}")
    List<Integer> queryAssignedRoleIdByAdminId(Integer adminId);

    /*
    分配角色之前，删除之前所分配的所有角色
    【根据admin_id删除所有的对应的role_id】
     */
    @Delete("delete from inner_admin_role where admin_id = #{adminId}")
    void deleteAssignedRoleByAdminId(Integer adminId);

    /*
    保存分配
    【根据 admin_id 和 role_id 的关系保存到 inner_admin_role 表中】
     */
    @Insert("insert into inner_admin_role values(null,#{adminId},#{roleId})")
    void saveAssignedRoleByAdminId(Integer adminId, Integer roleId);
}




