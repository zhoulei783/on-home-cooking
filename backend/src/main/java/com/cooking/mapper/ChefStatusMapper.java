package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.ChefStatus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 厨师状态Mapper接口
 */
@Mapper
public interface ChefStatusMapper extends BaseMapper<ChefStatus> {
}