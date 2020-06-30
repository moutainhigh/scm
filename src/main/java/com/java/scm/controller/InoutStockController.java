package com.java.scm.controller;

import com.github.pagehelper.PageInfo;
import com.java.scm.bean.InoutStock;
import com.java.scm.bean.User;
import com.java.scm.bean.base.BaseResult;
import com.java.scm.bean.excel.InoutStockReportTemplate;
import com.java.scm.bean.excel.InoutStockTemplate;
import com.java.scm.bean.so.InoutStockSO;
import com.java.scm.config.exception.BusinessException;
import com.java.scm.enums.InoutStockTypeEnum;
import com.java.scm.service.InoutStockService;
import com.java.scm.util.AssertUtils;
import com.java.scm.util.DateUtils;
import com.java.scm.util.RequestUtil;
import com.java.scm.util.excel.ExcelTypeEnum;
import com.java.scm.util.excel.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 出入库
 *
 * @author yupan
 * @date 2020-06-25 23:49
 */
@RestController
@RequestMapping("/inoutStock")
public class InoutStockController {

    @Autowired
    private InoutStockService inoutStockService;

    /**
     * 查询出入库列表
     * @return
     */
    @PostMapping("/list")
    public BaseResult listInoutStock(@RequestBody InoutStockSO inoutStockSO) {
        inoutStockSO.setWarehouseId(RequestUtil.getWarehouseId());
        PageInfo<InoutStock> pageInfo = inoutStockService.listInoutStock(inoutStockSO);
        return new BaseResult(pageInfo.getList(), Long.valueOf(pageInfo.getTotal()).intValue());
    }

    /**
     * 导出
     * @throws Exception
     */
    @GetMapping("/exportInoutStock")
    public void exportInoutStock(@RequestParam("type") Byte type,
                                 @RequestParam("warehouseId") Integer warehouseId,
                                 @RequestParam("project") String project,
                                 @RequestParam("product") String product,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime,
                                 HttpServletResponse response) throws Exception {
        InoutStockSO inoutStockSO = new InoutStockSO();
        inoutStockSO.setType(type);
        inoutStockSO.setWarehouseId(warehouseId);
        inoutStockSO.setProject(URLDecoder.decode(project, "utf-8"));
        inoutStockSO.setProduct(URLDecoder.decode(product, "utf-8"));
        inoutStockSO.setStartTime(DateUtils.parseDateTime(startTime));
        inoutStockSO.setEndTime(DateUtils.parseDateTime(endTime));
        PageInfo<InoutStock> pageInfo = inoutStockService.listInoutStock(inoutStockSO);
        List<InoutStockReportTemplate> exportList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pageInfo.getList())) {
            for(int i = 0; i < pageInfo.getList().size(); i++) {
                InoutStock inoutStock = pageInfo.getList().get(i);
                InoutStockReportTemplate template = new InoutStockReportTemplate();
                template.setNum(String.valueOf(i+1));
                template.setType(inoutStock.getTypeText());
                template.setWarehouse(inoutStock.getWarehouseName());
                template.setProject(inoutStock.getProject());
                template.setProduct(inoutStock.getProduct());
                template.setModel(inoutStock.getModel());
                template.setUnit(inoutStock.getUnit());
                template.setCount(String.valueOf(inoutStock.getCount()));
                template.setPrice(inoutStock.getPrice().toString());
                template.setSource(inoutStock.getSource());
                template.setHandle(inoutStock.getHandle());
                template.setRemark(inoutStock.getRemark());
                exportList.add(template);
            }
        }
        ExcelUtils.exportExcel(exportList, InoutStockReportTemplate.class, "出入库统计", "出入库统计", response);
    }

    /**
     * 导出模版
     * @throws Exception
     */
    @GetMapping("/exportTemplate/{type}")
    public void exportTemplate(HttpServletResponse response, @PathVariable("type") Byte type) throws Exception {
        String title = "入库单";
        String fileName = "入库单模版";
        if (Objects.equals(type, InoutStockTypeEnum.出库.getType())) {
            title = "出库单";
            fileName = "出库单模版";
        }
        ExcelUtils.exportExcel(new ArrayList<>(), InoutStockTemplate.class, title, fileName, response);
    }

    /**
     * 导入
     * @throws Exception
     */
    @PostMapping("/import/{type}")
    public BaseResult importInoutStock(@RequestParam("file")MultipartFile file, @PathVariable("type") Byte type, HttpServletRequest request) throws Exception {
        if (file.getOriginalFilename().indexOf(ExcelTypeEnum.XLS.getValue()) == -1 && file.getOriginalFilename().indexOf(ExcelTypeEnum.XLSX.getValue()) == -1) {
            throw new BusinessException("导入文件类型不正确，只能导入.xls和.xlsx后缀的文件！");
        }

        // 1、获取导入的excel数据
        List<InoutStockTemplate> importList = ExcelUtils.importExcel(file, 1,1, InoutStockTemplate.class);
        if (CollectionUtils.isEmpty(importList)) {
            throw new BusinessException("导入excel数据为空！");
        }

        // 2、校验数据
        importList.forEach(p -> {
            AssertUtils.notEmpty(p.getProject(), "工程名称不能为空！");
            AssertUtils.notEmpty(p.getProduct(), "物资名称不能为空！");
            AssertUtils.notEmpty(p.getModel(), "物资型号不能为空！");
            AssertUtils.notEmpty(p.getUnit(), "单位不能为空！");
            AssertUtils.notEmpty(p.getCount(), "数量不能为空！");
            // 判断库存是否存在
        });

        Byte inoutStockType = InoutStockTypeEnum.入库.getType();
        if (Objects.equals(type, InoutStockTypeEnum.出库.getType())) {
            inoutStockType = InoutStockTypeEnum.出库.getType();
        }

        // 获取当前登陆用户
        User user = RequestUtil.getCurrentUser();

        // 保存数据
        for (InoutStockTemplate template : importList) {
            InoutStock inoutStock = new InoutStock();
            inoutStock.setType(inoutStockType);
            inoutStock.setWarehouseId(user.getWarehouseId());
            inoutStock.setProject(template.getProject());
            inoutStock.setProduct(template.getProduct());
            inoutStock.setModel(template.getModel());
            inoutStock.setUnit(template.getUnit());
            inoutStock.setCount(Integer.valueOf(template.getCount()));
            inoutStock.setPrice(new BigDecimal(template.getPrice()));
            inoutStock.setSource(template.getSource());
            inoutStock.setHandle(template.getHandle());
            inoutStock.setRemark(template.getRemark());
            inoutStock.setCreateUser(user.getName());
            Long inoutStockId = inoutStockService.insertInoutStock(inoutStock);
        }
        return BaseResult.successResult();
    }
}
