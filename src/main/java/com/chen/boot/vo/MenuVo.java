package com.chen.boot.vo;

import com.chen.boot.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Forget_chen
 * @className MenuVo
 * @desc
 * @date 2022/6/13 23:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuVo extends Menu {
    private Integer page;
    private Integer limit;
}
