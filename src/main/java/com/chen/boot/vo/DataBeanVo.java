package com.chen.boot.vo;

import com.chen.boot.entity.DataBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Forget_chen
 * @className DataBeanVo
 * @desc
 * @date 2022/6/4 10:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataBeanVo extends DataBean {
    private Integer page;
    private Integer limit;
}
