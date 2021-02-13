package com.db.sys.controller;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.db.common.vo.JsonResult;
import com.db.sys.entity.SysUser;
import com.db.sys.service.SysUserService;
import com.db.sys.service.realm.ShiroUserRealm;

@Controller
@RequestMapping("/user/")
public class SysUserController {
	 @Autowired
	 private SysUserService sysUserService;
	 @Autowired
	 private ShiroUserRealm shiroUserRealm;
	 
     @RequestMapping("doUserListUI")
	 public String doUserListUI(){
		return "sys/user_list"; 
	 }
     @RequestMapping("doUserEditUI")
     public String doUserEditUI(){
    	 return "sys/user_edit"; 
     }
     //基于CAS算法，实现的并发安全，底层乐观锁实现。
     private AtomicInteger counter=new AtomicInteger(0);
     //private int count;
     @RequestMapping("doLogin")
     @ResponseBody
     public JsonResult doLogin(String username,String password){
    	 //1.封装用户信息
    	 UsernamePasswordToken token=
    	 new UsernamePasswordToken(
    			 username,password);
    	 //2.提交用户信息
    	 Subject subject=SecurityUtils.getSubject();
    	 subject.login(token);//提交给SecurityManager
    	 /*synchronized (this) {//(排他锁，互斥锁，独占锁)
    		 count++;//本身这个操作线程不安全(不是原子操作)
    		 System.out.println("在线人数:"+count);
		 }*/
    	 int count=counter.incrementAndGet();//count+1;
    	 System.out.println("在线人数:"+count);
    	 return new JsonResult("login ok");
     }
     @RequestMapping("doLogout")
     public String doLogout(){
    	 System.out.println("=doLogout=");
    	 shiroUserRealm.logout();
    	 /*synchronized (this) {
    		 count--;
		 }*/
    	 counter.decrementAndGet();
    	 return "redirect:../doLoginUI.do";
     }
     
     @RequestMapping("doFindObjectByColumn")
     @ResponseBody
     public JsonResult doFindObjectByColumn(
    		 String columnName,
    		 String columnValue){
    	 return new JsonResult(
    		sysUserService.findObjectByColumn(
    			columnName, columnValue));
     }
     
     @RequestMapping("doFindObjectById")
     @ResponseBody
     public JsonResult doFindObjectById(Integer id){
    	 return new JsonResult(sysUserService.findObjectById(id));
     }
     
     @RequestMapping("doUpdateObject")
     @ResponseBody
     public JsonResult doUpdateObject(
    		 SysUser entity,
    		 Integer[] roleIds){
    	 sysUserService.updateObject(entity, roleIds);
    	 return new JsonResult("update OK");
     }
     @RequestMapping("doSaveObject")
     @ResponseBody
     public JsonResult doSaveObject(
    		 SysUser entity,
    		 Integer[] roleIds){
    	 sysUserService.saveObject(entity, roleIds);
    	 return new JsonResult("save OK");
     }
     
     @RequestMapping("doValidById")
     @ResponseBody
     public JsonResult doValidById(Integer id,Integer valid){
    	 System.out.println(sysUserService.getClass().getName());//$Proxy38
    	 sysUserService.validById(id,valid);//后续来自登录用户
    	 return new JsonResult("update ok");
     }
     
     @RequestMapping("doFindPageObjects")
     @ResponseBody
     public JsonResult doFindPageObjects(
    		 String username,
    		 Integer pageCurrent){
    	 System.out.println("find.service="+sysUserService.getClass().getName());
    	 return new JsonResult(
    			    sysUserService.findPageObjects(
    				username,pageCurrent));
     }
}



