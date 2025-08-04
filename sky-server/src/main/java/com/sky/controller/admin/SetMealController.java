package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetMealController {

    @Autowired
    private SetmealService setMealService;

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation("分页查询套餐")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setMealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增菜品
     * @param setmealDTO
     * @return
     */
    @ApiOperation("新增套餐")
    @PostMapping
    public Result insert(@RequestBody SetmealDTO setmealDTO){
        setMealService.insert(setmealDTO);
        return Result.success();
    }

    /**
     * 修改套餐售卖状态
     * @param id
     * @return
     */
    @ApiOperation("修改套餐售卖状态")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status, Long id){
        log.info("状态：{}，id：{}", status, id);
        setMealService.status(status, id);
        return Result.success();
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @ApiOperation("删除套餐")
    @DeleteMapping
    //接口类型参数（如 List、Map）必须显式添加 @RequestParam，否则 Spring 无法实例化接口，导致构造函数异常。
    //简单类型 / 数组参数（如 String、Integer[]）可省略 @RequestParam，但集合接口（List、Set）必须通过注解指定参数来源。
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除套餐:{}", ids);
        setMealService.delete(ids);
        return Result.success();
    }

    /**
     * 根据套餐id查询套餐，用于修改套餐
     * @param id
     * @return
     */
    @ApiOperation("根据id查询套餐")
    @GetMapping("/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        SetmealVO setmealVO = setMealService.getById(id);
        return Result.success(setmealVO);
    }


    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @ApiOperation("修改套餐")
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setMealService.update(setmealDTO);
        return Result.success();
    }
}
