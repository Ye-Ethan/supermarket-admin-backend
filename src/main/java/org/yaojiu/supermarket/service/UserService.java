package org.yaojiu.supermarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yaojiu.supermarket.entity.UserDTO;
import org.yaojiu.supermarket.entity.UserEntity;

public interface UserService extends IService<UserEntity> {
    public UserDTO login(UserEntity userEntity);
    public boolean register(UserEntity userEntity);
    public UserDTO getUserById(Integer userId);
}
