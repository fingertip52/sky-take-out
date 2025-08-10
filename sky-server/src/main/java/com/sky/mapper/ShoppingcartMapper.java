package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface ShoppingcartMapper {

    /**
     * 动态条件查询
     *
     * @param shoppingCart
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);


    /**
     * 将购物车中某商品数量加一
     *
     * @param list
     */
    @Update("update sky_take_out.shopping_cart set number = #{number} where id = #{id}")
    void update(ShoppingCart list);

    /**
     * 将购物车中的数据插入到数据库中
     *
     * @param shoppingCart
     */
    @Insert("insert into sky_take_out.shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) values " +
            "(#{name} ,#{image} ,#{userId} ,#{dishId} ,#{setmealId} ,#{dishFlavor} ,#{number} ,#{amount} ,#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 查看购物车
     * @return
     */
    @Select("select * from sky_take_out.shopping_cart where user_id = #{userId}")
    List<ShoppingCart> getAll(Long userId);

    /**
     * 清空购物车
     * @param userId
     */
    @Delete("delete from sky_take_out.shopping_cart where user_id = #{userId}")
    void clean(Long userId);

    /**
     * 根据id删除购物车中某个商品
     * @param id
     */
    @Delete("delete from sky_take_out.shopping_cart where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据用户id删除购物车中商品
     * @param userId
     */
    @Delete("delete from sky_take_out.shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);
}
