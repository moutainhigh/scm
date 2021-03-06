package com.java.scm.bean.so;

import com.java.scm.bean.base.PageCondition;
import lombok.Getter;
import lombok.Setter;

/**
 * 工程查询
 *
 * @author yupan
 * @date 2020-08-08 21:37
 */
@Getter
@Setter
public class ProjectSO extends PageCondition {

    /**
     * 客户名称
     */
    private String customer;

    /**
     * 工程名称
     */
    private String name;

    /**
     * 工程进度
     */
    private String progress;

    /**
     * 状态 0：启用 1：禁用
     */
    private Byte state;

}
