package com.aiblog.system.mapper;

import com.aiblog.system.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Select;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

  @Select("""
      select distinct m.*
      from sys_menu m
      join sys_role_menu rm on rm.menu_id = m.id
      join sys_user_role ur on ur.role_id = rm.role_id
      join sys_role r on r.id = rm.role_id
      where ur.user_id = #{userId}
        and m.status = 1
        and m.visible = 1
        and m.deleted = 0
        and r.status = 1
        and r.deleted = 0
      order by m.sort_order asc, m.id asc
      """)
  List<SysMenu> selectVisibleMenusByUserId(Long userId);
}
