package com.chen.boot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提供给layui的返回数据实体
 *
 * @author Forget_chen
 * @className DtatView
 * @desc
 * @date 2022/6/4 09:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultEntity<T> {
    public static final Integer SUCCESS = 200; // 代表成功
    public static final Integer FAILED = 400; // 代表失败

    /**
     * 状态码
     */
    private  Integer code;

    /**
     * 提示信息
     */
    private String msg = "";

    /**
     * 数量：为了给layui的分页接口传递所设置的全部数据条数
     */
    private Long count = 0L;

    /**
     * 传输的数据
     */
    private T data;



    /*
    请求成功并且不需要返回数据时，使用的工具方法（增删改使用）
     */
    public static <Type> ResultEntity<Type> successWithOutData() {
        return new ResultEntity<Type>(SUCCESS, null, null, null);
    }
    /*
    请求成功并且不需要返回数据时，可以自定义 message提示信息 使用的工具方法（增删改使用）
     */
    public static <Type> ResultEntity<Type> successWithOutData(String msg) {
        return new ResultEntity<Type>(SUCCESS, msg, null, null);
    }

    /*
    请求成功并且返回数据时，只需要传递数据
     */
    public static <Type> ResultEntity<Type> successWithData(Type data) {
        return new ResultEntity<Type>(SUCCESS, null, null, data);
    }

    /*
    请求成功并且返回数据时，提供给Layui的分页数据格式（查询使用）
     */
    public static <Type> ResultEntity<Type> successWithData(Long total, Type data) {
        return new ResultEntity<Type>(SUCCESS, null, total, data);
    }

    /*
    请求成功并且返回数据时，提供给Layui的下拉树生成数据
     */
    public static <Type> ResultEntity<Type> successWithData(Integer code, Type data) {
        return new ResultEntity<Type>(code, null, null, data);
    }

    /*
    请求成功并且返回数据时，可以自定义code数据
     */
    public static <Type> ResultEntity<Type> successWithData(Integer code,Long total, Type data) {
        return new ResultEntity<Type>(code, null, total, data);
    }

    /*
    请求失败使用的工具方法（失败时调用）
     */
    public static <Type> ResultEntity<Type> failed(String message) {
        return new ResultEntity<Type>(FAILED, message, null, null);
    }
}
