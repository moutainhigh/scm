package com.java.scm.controller;

import com.github.pagehelper.PageInfo;
import com.java.scm.bean.InoutStock;
import com.java.scm.bean.base.BaseResult;
import com.java.scm.bean.excel.InoutStockReportTemplate;
import com.java.scm.bean.excel.InoutStockTemplate;
import com.java.scm.bean.so.InoutStockSO;
import com.java.scm.config.exception.BusinessException;
import com.java.scm.enums.InoutStockTypeEnum;
import com.java.scm.service.InoutStockService;
import com.java.scm.util.RequestUtil;
import com.java.scm.util.StringUtil;
import com.java.scm.util.excel.ExcelTypeEnum;
import com.java.scm.util.excel.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
        return new BaseResult(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 导出
     * @throws Exception
     */
    @GetMapping("/exportInoutStock")
    public void exportInoutStock(@RequestParam("type") Byte type,
                                 @RequestParam("warehouseId") String warehouseId,
                                 @RequestParam("project") String project,
                                 @RequestParam("product") String product,
                                 @RequestParam("model") String model,
                                 @RequestParam("source") String source,
                                 @RequestParam("startTime") String startTime,
                                 @RequestParam("endTime") String endTime,
                                 HttpServletResponse response) throws Exception {
        InoutStockSO inoutStockSO = new InoutStockSO();
        inoutStockSO.setType(type);
        // 当前仓库为空时，获取当前人所属仓库id
        if (StringUtil.isEmpty(warehouseId)) {
            warehouseId = RequestUtil.getWarehouseId();
        }
        inoutStockSO.setWarehouseId(warehouseId);
        if (StringUtil.isNotEmpty(project)) {
            inoutStockSO.setProject(URLDecoder.decode(project, "utf-8"));
        }
        if (StringUtil.isNotEmpty(product)) {
            inoutStockSO.setProduct(URLDecoder.decode(product, "utf-8"));
        }
        if (StringUtil.isNotEmpty(model)) {
            inoutStockSO.setModel(URLDecoder.decode(model, "utf-8"));
        }
        if (StringUtil.isNotEmpty(source)) {
            inoutStockSO.setSource(URLDecoder.decode(source, "utf-8"));
        }
        inoutStockSO.setStartTime(startTime);
        inoutStockSO.setEndTime(endTime);
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
        String fileName = "出入库统计";
        if (Objects.equals(type, InoutStockTypeEnum.入库.getType())) {
            fileName = "入库统计";
        } else if (Objects.equals(type, InoutStockTypeEnum.出库.getType())) {
            fileName = "出库统计";
        }
        ExcelUtils.exportExcel(exportList, InoutStockReportTemplate.class, fileName, fileName, response);
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
    public BaseResult importInoutStock(@RequestParam("file")MultipartFile file, @PathVariable("type") Byte type) throws Exception {
        if (file.getOriginalFilename().indexOf(ExcelTypeEnum.XLS.getValue()) == -1 && file.getOriginalFilename().indexOf(ExcelTypeEnum.XLSX.getValue()) == -1) {
            throw new BusinessException("导入文件类型不正确，只能导入.xls和.xlsx后缀的文件！");
        }

        // 获取导入的excel数据
        List<InoutStockTemplate> importList = ExcelUtils.importExcel(file, 1,1, InoutStockTemplate.class);
        if (CollectionUtils.isEmpty(importList)) {
            throw new BusinessException("导入excel数据为空！");
        }

        Byte inoutStockType = InoutStockTypeEnum.入库.getType();
        if (Objects.equals(type, InoutStockTypeEnum.出库.getType())) {
            inoutStockType = InoutStockTypeEnum.出库.getType();
        }

        inoutStockService.importInoutStock(importList, inoutStockType);
        return BaseResult.successResult();
    }

    /**
     * 新增出入库
     * @return
     */
    @PostMapping("/save")
    public BaseResult listInoutStock(@RequestBody InoutStock inoutStock) {
        inoutStockService.saveInoutStock(inoutStock);
        return BaseResult.successResult();
    }
}
