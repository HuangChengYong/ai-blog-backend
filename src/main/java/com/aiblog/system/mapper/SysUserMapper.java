package com.aiblog.system.mapper;

import com.aiblog.system.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface SysUserMapper extends BaseMapper<SysUser> {

  @Select("""
      select r.role_name
      from sys_role r
      join sys_user_role ur on ur.role_id = r.id
      where ur.user_id = #{userId}
        and r.deleted = 0
        and r.status = 1
      order by r.sort_order asc, r.id asc
      limit 1
      """)
  String selectPrimaryRoleNameByUserId(Long userId);

  @Select("""
      select r.id
      from sys_role r
      join sys_user_role ur on ur.role_id = r.id
      where ur.user_id = #{userId}
        and r.deleted = 0
        and r.status = 1
      order by r.sort_order asc, r.id asc
      limit 1
      """)
  Long selectPrimaryRoleIdByUserId(Long userId);
}
