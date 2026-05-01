package com.aiblog.system.mapper;

import com.aiblog.system.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Select;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {

  @Select("""
      select distinct p.code
      from sys_permission p
      join sys_role_permission rp on rp.permission_id = p.id
      join sys_user_role ur on ur.role_id = rp.role_id
      join sys_role r on r.id = rp.role_id
      where ur.user_id = #{userId}
        and p.status = 1
        and r.status = 1
        and p.deleted = 0
        and r.deleted = 0
      """)
  List<String> selectPermissionCodesByUserId(Long userId);
}
