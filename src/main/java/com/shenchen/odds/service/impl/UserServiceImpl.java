package com.shenchen.odds.service.impl;
import com.shenchen.odds.dao.IUserDao;
import com.shenchen.odds.model.User;
import com.shenchen.odds.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
  
  
@Service("userService")  
public class UserServiceImpl implements IUserService {
    @Resource  
    private IUserDao userDao;
    
    public User getUserById(int userId) {
        // TODO Auto-generated method stub  
        return this.userDao.selectByPrimaryKey(userId);  
    }  
  
}  
