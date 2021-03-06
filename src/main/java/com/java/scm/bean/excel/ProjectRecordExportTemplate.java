package com.java.scm.bean.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

/**
 * 工程明细模型（导出）
 *
 * @author yupan
 * @date 2020-06-25 23:34
 */
@Getter
@Setter
public class ProjectRecordExportTemplate {

    @Excel(name = "序号", orderNum = "0")
    private String num;

    @Excel(name = "类型", orderNum = "1")
    private String type;

    @Excel(name = "日期", orderNum = "2", width = 15.0D)
    private String recordDate;

    @Excel(name = "摘要", orderNum = "3", width = 20.0D)
    private String digest;

    @Excel(name = "金额", orderNum = "4")
    private String money;

    @Excel(name = "经手人", orderNum = "5", width = 20.0D)
    private String handle;

    @Excel(name = "备注", orderNum = "6", width = 20.0D)
    private String remark;

}
