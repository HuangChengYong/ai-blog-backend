package com.aiblog.security;

import com.aiblog.system.entity.SysUser;
import com.aiblog.system.mapper.SysPermissionMapper;
import com.aiblog.system.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

  private final SysUserMapper userMapper;
  private final SysPermissionMapper permissionMapper;

  public SecurityUserDetailsService(SysUserMapper userMapper, SysPermissionMapper permissionMapper) {
    this.userMapper = userMapper;
    this.permissionMapper = permissionMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return new SecurityUser(user, permissionMapper.selectPermissionCodesByUserId(user.getId()));
  }
}
