package com.aiblog.system.mapper;

import com.aiblog.system.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Select;

public interface SysRoleMapper extends BaseMapper<SysRole> {

  @Select("""
      select count(*)
      from sys_user_role ur
      join sys_user u on u.id = ur.user_id
      where ur.role_id = #{roleId}
        and u.deleted = 0
      """)
  long countUsersByRoleId(Long roleId);

  @Select("""
      select p.name
      from sys_permission p
      join sys_role_permission rp on rp.permission_id = p.id
      where rp.role_id = #{roleId}
        and p.deleted = 0
        and p.status = 1
      order by p.id asc
      """)
  List<String> selectPermissionNamesByRoleId(Long roleId);
}
