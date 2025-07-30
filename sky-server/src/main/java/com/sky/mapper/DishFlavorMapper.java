package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insert(List<DishFlavor> flavors);

    /**
     * 删除版本一，单个删除
     * 根据id删除口味数据
     * @param dishId
     */
    @Delete("delete from sky_take_out.dish_flavor where dish_flavor.dish_id = #{dishId}")
    void deleteById(Long dishId);

    /**
     * 删除版本二，批量删除
     * 根据ids删除口味数据
     * @param dishIds
     */
    void deleteByIds(List<Long> dishIds);

    /**
     * 根据dishId查询口味
     * @param dishId
     * @return
     */
    @Select("select * from sky_take_out.dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

}
      