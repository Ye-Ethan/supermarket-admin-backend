package org.yaojiu.supermarket.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaojiu.supermarket.entity.UserDTO;
import org.yaojiu.supermarket.entity.UserEntity;
import org.yaojiu.supermarket.exception.LoginException;
import org.yaojiu.supermarket.mapper.UserMapper;
import org.yaojiu.supermarket.service.AuthService;
import org.yaojiu.supermarket.utils.SecurityUtils;

@Service
public class AuthServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements AuthService {

    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public UserDTO login(UserEntity userEntity) {
        UserEntity user = getOne(Wrappers.lambdaQuery(UserEntity.class).eq(UserEntity::getUsername, userEntity.getUsername()));
        if (user != null && securityUtils.verify(user.getPassword(), userEntity.getPassword())) return user.toDTO();
        throw new LoginException("账号密码有误，请重试") ;
    }

     @Override
    public boolean register(UserEntity userEntity) {
         userEntity.setPassword(securityUtils.hash(userEntity.getPassword()));
         UserEntity one = getOne(Wrappers.lambdaQuery(UserEntity.class).eq(UserEntity::getUsername, userEntity.getUsername()));
         if(one != null) throw new LoginException("用户已存在");
         return save(userEntity);
     }
}
