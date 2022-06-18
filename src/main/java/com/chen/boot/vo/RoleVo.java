package com.chen.boot.vo;

import com.chen.boot.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Forget_chen
 * @className RoleVo
 * @desc
 * @date 2022/6/14 21:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVo extends Role {
    private Integer page;
    private Integer limit;
}
