package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Transactional//保证执行的原子性
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        //属性拷贝,将DTO里的相关值拷贝到dish对象中
        BeanUtils.copyProperties(dishDTO, dish);

        //向菜品表中插入1条数据，这里插入dish对象即可
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        //判断集合中是否有数据
        if(flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            //向口味表中插入n条数据
            dishFlavorMapper.insert(flavors);
        }

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Transactional
    public void deleteByIds(List<Long> ids) {
        //判断当前菜品是否能够删除
        //是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //版本一：删除菜品表中的菜品数据
       /* for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除菜品关联的口味数据
            dishFlavorMapper.deleteById(id);
        }*/

        //版本二：优化帮，使用sql语句进行批量删除
        //批量删除菜品表中的菜品数据
        //sql:delete from dish where id in (1,2,3)
        dishMapper.deleteByIds(ids);

        //批量删除菜品关联的口味数据
        //sql:delete from dish_flavor where dish_id in (1,2,3)
        dishFlavorMapper.deleteByIds(ids);

    }

    /**
     * 根据id查询菜品和口味
     * @param id
     * @return
     */
    public DishVO getById(Long id) {

        //1.根据id查询菜品
        Dish dish = dishMapper.getById(id);

        //2.根据菜品id查询口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //3.将数据封装
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 修改菜品和口味
     *
     * @param dishDTO
     * @return
     */
    @Transactional
    public void update(DishDTO dishDTO) {
        //1.修改菜品表
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //2.修改口味表--直接修改的话，会有诸多问题
        //2.1 删除原来口味列表
        dishFlavorMapper.deleteById(dishDTO.getId());
        //2.2 将新增的口味列表重新插入
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //判断集合中是否有数据
        if(flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            //向口味表中插入n条数据
            dishFlavorMapper.insert(flavors);
        }

    }

    /**
     * 菜品起售、停售
     * @param status
     * @param id
     */
    @Transactional
    public void status(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        // 如果是停售操作，还需要将包含当前菜品的套餐也停售
        if(status == StatusConstant.DISABLE) {
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // 当前菜品包含的所有套餐，1,2,3
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if(setmealIds != null && setmealIds.size() > 0) {
                for(Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }

    }

    /**
     * 根据分类id查询菜品
     * @param id
     * @return
     */
    public List<Dish> getByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<Dish> dish_list = dishMapper.getByCategoryId(dish);
        return dish_list;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.getByCategoryId(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
