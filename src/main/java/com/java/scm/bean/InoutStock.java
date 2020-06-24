package com.java.scm.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 出入库表
 *
 * @author yupan
 * @date 2020/6/23 10:44
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inout_stock")
public class InoutStock {

    /**
     * 出入库Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 仓库id
     */
    private Integer warehouseId;

    /**
     * 工程名称
     */
    private String project;

    /**
     * 物资名称
     */
    private String product;

    /**
     * 物资型号
     */
    private String model;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 经手人
     */
    private String handle;

    /**
     * 物资来源
     */
    private String source;

    /**
     * 物资单价（元）
     */
    private BigDecimal price;

    /**
     * 备注
     */
    private String remark;

    /**
     * 类别 0：入库 1：出库
     */
    private Byte type;

    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;
}