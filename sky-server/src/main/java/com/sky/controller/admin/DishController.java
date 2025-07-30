package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * 涉及到dish和category两个数据库表
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * 这里涉及到三张表dish、dish_flavor、setmeal_dish
     *
     * @param ids
     * @return
     */
    @ApiOperation("菜品批量删除")
    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteByIds(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品和口味
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品和口味")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("根据id查询菜品和口味：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品和口味
     *
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品和口味")
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.update(dishDTO);
        return Result.success();
    }

    @PostMapping("status/{status}")
    @ApiOperation("菜品起售、停售")
    public Result status(@PathVariable Integer status, Long id){
        dishService.status(status, id);
        return Result.success();
    }


    /**
     * 根据分类id查询菜品，用于套餐添加菜品时的查询
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> dish = dishService.getByCategoryId(categoryId);
        return Result.success(dish);
    }
}
