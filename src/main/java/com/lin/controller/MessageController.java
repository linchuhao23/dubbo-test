package com.lin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lin.dao.UserDao;
import com.lin.entity.User;
import com.lin.service.MessageService;

@Controller
public class MessageController {
	
	private final String base_url = "/hello";
	
	@Autowired
	private MessageService messageService;
	
	@RequestMapping(value = {base_url + "/say"})
	@ResponseBody
	public String say() {
		messageService.consumer("kll");
		return "林楚豪";
	}
	
	@Autowired
	private UserDao userDao;
	
	@Cacheable("listUser")
	@RequestMapping(value = {base_url + "/listUser"})
	@ResponseBody
	public List<User> listUser() {
		return userDao.listAll();
	}
	
	@CacheEvict(value = {"listUser"}, allEntries = true)
	@RequestMapping(value = {base_url + "/add"})
	@ResponseBody
	public User add() {
		User user = new User();
		user.setName("addUser");
		userDao.save(user);
		return user;
	}
	
	@RequestMapping(value = {base_url + "/goto"})
	public String hello() {
		return "hello";
	}

}