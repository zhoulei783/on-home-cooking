package com.cooking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.ChefStatus;
import com.cooking.mapper.ChefStatusMapper;
import com.cooking.service.ChefStatusService;
import org.springframework.stereotype.Service;

/**
 * 厨师状态Service实现类
 */
@Service
public class ChefStatusServiceImpl extends ServiceImpl<ChefStatusMapper, ChefStatus> implements ChefStatusService {
}