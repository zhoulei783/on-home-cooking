package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.Chef;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * 厨师Mapper接口（关联ChefMapper.xml）
 */
@Mapper
public interface ChefMapper extends BaseMapper<Chef> {

    /**
     * 根据状态查询厨师（对应XML中的selectByStatus）
     * @param status 厨师状态（0-空闲，1-忙碌）
     * @return 厨师列表
     */
    List<Chef> selectByStatus(Integer status);

    /**
     * 根据擅长菜系模糊查询（对应XML中的selectBySkill）
     * @param skill 菜系关键词
     * @return 厨师列表
     */
    List<Chef> selectBySkill(String skill);

    /**
     * 根据ID更新厨师状态（对应XML中的updateStatusById）
     * @param map 包含chefId和status的参数Map
     * @return 受影响行数
     */
    int updateStatusById(@Param("map") Map<String, Object> map);

    /**
     * 更新厨师收入信息（对应XML中的updateEarnings）
     * @param map 包含chefId、totalEarnings、paidAmount、remainingAmount的参数Map
     * @return 受影响行数
     */
    int updateEarnings(@Param("map") Map<String, Object> map);

    /**
     * 增加厨师收入（对应XML中的addEarnings）
     * @param map 包含chefId和amount的参数Map
     * @return 受影响行数
     */
    int addEarnings(@Param("map") Map<String, Object> map);

    /**
     * 发放厨师收入（对应XML中的payEarnings）
     * @param map 包含chefId和amount的参数Map
     * @return 受影响行数
     */
    int payEarnings(@Param("map") Map<String, Object> map);

    /**
     * 分页查询厨师（支持状态/菜系筛选，对应XML中的selectChefPage）
     * @param map 包含分页参数和查询条件的Map
     * @return 厨师列表
     */
    List<Chef> selectChefPage(@Param("map") Map<String, Object> map);
}