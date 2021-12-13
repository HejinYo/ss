package cn.hejinyo.ss.common.core.util;

import cn.hejinyo.ss.common.core.constant.BaseStatusConstant;
import cn.hejinyo.ss.common.core.constant.CommonStatusConstant;

import java.util.HashMap;

/**
 * 返回结果
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/5 18:25
 */
public class Result extends HashMap<String, Object> {
    private static final int SUCCESS = CommonStatusConstant.SUCCESS.getCode();
    private static final int ERROR = CommonStatusConstant.FAILURE.getCode();
    private static final int INITIAL = 4;
    private static final String MSG = "msg";
    private static final String CODE = "code";
    private static final String RESULT = "result";

    private Result() {
        super();
    }

    private Result(int initialCapacity) {
        super(initialCapacity);
    }

    public static Result ok() {
        Result jsonMap = new Result(INITIAL);
        jsonMap.put(CODE, SUCCESS);
        return jsonMap;
    }

    public static Result ok(Object result) {
        Result jsonMap = ok();
        jsonMap.put(RESULT, result);
        return jsonMap;
    }

    public static Result error() {
        Result jsonMap = new Result(INITIAL);
        jsonMap.put(CODE, ERROR);
        return jsonMap;
    }

    public static Result error(String msg) {
        Result jsonMap = error();
        jsonMap.put(MSG, msg);
        return jsonMap;
    }

    public static Result status(BaseStatusConstant statusCode) {
        Result jsonMap = new Result(INITIAL);
        jsonMap.put(CODE, statusCode.getCode());
        jsonMap.put(MSG, statusCode.getMsg());
        return jsonMap;
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }

}
