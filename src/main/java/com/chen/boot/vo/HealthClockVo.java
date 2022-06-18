package com.chen.boot.vo;

import com.chen.boot.entity.HealthClock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Forget_chen
 * @className HealthClockVo
 * @desc
 * @date 2022/6/6 15:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthClockVo extends HealthClock {
    private Integer page;
    private Integer limit;
}
