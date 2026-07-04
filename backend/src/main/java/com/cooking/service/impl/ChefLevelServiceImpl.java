package com.cooking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.ChefLevel;
import com.cooking.mapper.ChefLevelMapper;
import com.cooking.service.ChefLevelService;
import org.springframework.stereotype.Service;

/**
 * 厨师等级Service实现类
 */
@Service
public class ChefLevelServiceImpl extends ServiceImpl<ChefLevelMapper, ChefLevel> implements ChefLevelService {
}
