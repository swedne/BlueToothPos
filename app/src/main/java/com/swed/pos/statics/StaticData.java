package com.swed.pos.statics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/12.
 */

public class StaticData {
    public static Map<String, String> statusMap = new HashMap();

    static {
        statusMap.put("1", "成功");
        statusMap.put("0", "银行审核中");
        statusMap.put("2", "密码错误");
        statusMap.put("-1", "失败");
    }
}