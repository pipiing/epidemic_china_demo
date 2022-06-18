package com.chen.boot.vo;

import com.chen.boot.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Forget_chen
 * @className AdminVo
 * @desc
 * @date 2022/6/15 15:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminVo extends Admin {
    private Integer page;
    private Integer limit;
}
