package com.aiblog.security;

import com.aiblog.system.entity.SysUser;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUser implements UserDetails {

  private final SysUser user;
  private final List<GrantedAuthority> authorities;

  public SecurityUser(SysUser user, List<String> permissionCodes) {
    this.user = user;
    this.authorities = permissionCodes.stream()
        .map(SimpleGrantedAuthority::new)
        .map(GrantedAuthority.class::cast)
        .toList();
  }

  public Long id() {
    return user.getId();
  }

  public String nickname() {
    return user.getNickname();
  }

  public String avatarUrl() {
    return user.getAvatarUrl();
  }

  public String dataScope() {
    return user.getDataScope();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return user.getPasswordHash();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public boolean isEnabled() {
    return Integer.valueOf(1).equals(user.getStatus());
  }
}
