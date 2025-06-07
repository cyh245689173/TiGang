package com.copico.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.copico.mapper.UserMapper;
import com.copico.model.domain.User;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author yuhao.chen
 * @since 2025-06-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
