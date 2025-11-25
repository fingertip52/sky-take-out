package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from sky_take_out.employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 添加员工
     * @param employee
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into sky_take_out.employee (name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user)"
            + "values (#{name} ,#{username} ,#{password} ,#{phone} ,#{sex} ,#{idNumber} ,#{status} ,#{createTime} ,#{updateTime} ,#{createUser} ,#{updateUser})")
    void insert(Employee employee);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工信息(启禁用账号，修改信息等与修改相关的功能)
     * @param employee
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工
     */
    @Select("select * from sky_take_out.employee where id = #{id}")
    Employee getById(Long id);
}
