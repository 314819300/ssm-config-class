package com.db.sys.service.realm;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.db.common.utils.ShiroUtils;
import com.db.sys.dao.SysMenuDao;
import com.db.sys.dao.SysRoleMenuDao;
import com.db.sys.dao.SysUserDao;
import com.db.sys.dao.SysUserRoleDao;
import com.db.sys.entity.SysUser;
/**
 * 负责用户认证信息和授权信息的获取以及封装
 * @author ta
 */
@Service
@Scope("singleton")
public class ShiroUserRealm extends AuthorizingRealm {
   
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysUserRoleDao sysUserRoleDao;
	@Autowired
	private SysRoleMenuDao sysRoleMenuDao;
	@Autowired
	private SysMenuDao sysMenuDao;
	/**
	 * 设置凭证(密码)匹配器
	 */
	@Override
	public void setCredentialsMatcher(
	    CredentialsMatcher credentialsMatcher) {
		HashedCredentialsMatcher cMatcher=
		new HashedCredentialsMatcher();
		cMatcher.setHashAlgorithmName("MD5");
		super.setCredentialsMatcher(cMatcher);
	}
	/**
     * 负责认证信息的获取和封装
     */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token)
		    throws AuthenticationException {
		//1.获取登录用户信息
		UsernamePasswordToken uToken=
		(UsernamePasswordToken) token;
		//2.基于用户名从数据库查询用户并验证
		SysUser user=
		sysUserDao.findUserByUserName(
				   uToken.getUsername());
		//2.1验证用户是否存在
		if(user==null)
	    throw new UnknownAccountException();
		//2.2验证用户是否被禁用
		if(user.getValid()==0)
		throw new LockedAccountException();
		//3.封装用户信息并返回
		ByteSource credentialsSalt=//凭证盐
		ByteSource.Util.bytes(user.getSalt());
		
		SimpleAuthenticationInfo info=
		new SimpleAuthenticationInfo(
				user,//principal(身份)
				user.getPassword(),//hashedCredentials
				credentialsSalt,//credentialsSalt(盐)
				getName());//realName
		return info;//返回给认证管理器
	}
	private Map<String,AuthorizationInfo> authorizationCache=
			new ConcurrentHashMap<>();
	/**负责授权信息的获取和封装*/
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
	     PrincipalCollection principals) {
		//1.获取登录用户信息
		SysUser user=(SysUser)
		principals.getPrimaryPrincipal();
		AuthorizationInfo aInfo=
		authorizationCache.get(user.getUsername());
		if(aInfo!=null)return aInfo;
		System.out.println("===doGetAuthorizationInfo==");
		//2.基于登录用户id获取对应的角色id
		List<Integer> roleIds=
		sysUserRoleDao.findRoleIdsByUserId(user.getId());
		if(roleIds==null||roleIds.size()==0)
	    throw new AuthorizationException();
		//3.基于用户角色获得对应的菜单id
		Integer[] array={};
		List<Integer> menuIds=
		sysRoleMenuDao.findMenuIdsByRoleIds(
				roleIds.toArray(array));
		if(menuIds==null||roleIds.size()==0)
		throw new AuthorizationException();
		//4.基于菜单id获取菜单对应的权限标识
		List<String> permissionList=
		sysMenuDao.findPermissions(
		menuIds.toArray(array));
		if(permissionList==null||permissionList.size()==0)
		throw new AuthorizationException();
		//5.对用户权限进行封装并返回(授权管理器)
		Set<String> stringPermissions=new HashSet<>();
		for(String p:permissionList){
			if(!StringUtils.isEmpty(p)){
				stringPermissions.add(p);
			}
		}
		SimpleAuthorizationInfo info=
		new SimpleAuthorizationInfo();
		info.setStringPermissions(stringPermissions);
		authorizationCache.put(user.getUsername(),info);
		return info;//返回给授权管理器
	}
	/***
	 * 执行退出操作
	 */
	public void logout(){
		SysUser user=ShiroUtils.getUser();
		authorizationCache.remove(user.getUsername());
		//系统底层会将用户从shiro中的session对象移除
   	    SecurityUtils.getSubject().logout();
	}
}








