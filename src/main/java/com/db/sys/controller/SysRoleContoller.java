package com.db.sys.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.db.common.vo.JsonResult;
import com.db.sys.entity.SysRole;
import com.db.sys.service.SysRoleService;

@RequestMapping("/role/")
@Controller
public class SysRoleContoller {

	 @Autowired
	 private SysRoleService sysRoleService;
	 
	 @RequestMapping("doRoleListUI")
	 public String doRoleListUI(){
		 return "sys/role_list";
	 }
	 
	 @RequestMapping("doRoleEditUI")
	 public String doRoleEditUI(){
		 return "sys/role_edit";
	 }
	 
	 @RequestMapping("doFindRoles")
	 @ResponseBody
	 public JsonResult doFindObjects(){
	  	 return new JsonResult(sysRoleService.findObjects());
	 }
	 @RequestMapping("doFindObjectById")
	 @ResponseBody
	 public JsonResult doFindObjectById(Integer id){
	     return new JsonResult(sysRoleService.findObjectById(id));
	 }

	 @RequestMapping("doUpdateObject")
	 @ResponseBody
	 public JsonResult doUpdateObject(
			 SysRole entity,Integer[] menuIds){
		 System.out.println("entity="+entity);
		 System.out.println("menuIds="+Arrays.toString(menuIds));
		 sysRoleService.updateObject(entity, menuIds);
		 return new JsonResult("update ok");
	 }
	 
	 @RequestMapping("doSaveObject")
	 @ResponseBody
	 public JsonResult doSaveObject(
			 SysRole entity,Integer[] menuIds){
		 sysRoleService.saveObject(entity, menuIds);
		 return new JsonResult("save ok");
	 }
	 
	 @RequestMapping("doDeleteObject")
	 @ResponseBody
	 public JsonResult doDeleteObject(Integer id){
		 sysRoleService.deleteObject(id);
		 return new JsonResult("delete ok");
	 }
	 
	 
	 
	 @GetMapping("doFindPageObjects")
	 @ResponseBody
	 public JsonResult doFindPageObjects(String name,Integer pageCurrent){
		 System.out.println("name="+name);
		 return new JsonResult(
		sysRoleService.findPageObjects(
				name, pageCurrent));
	 }
	 
	 
	 
}
