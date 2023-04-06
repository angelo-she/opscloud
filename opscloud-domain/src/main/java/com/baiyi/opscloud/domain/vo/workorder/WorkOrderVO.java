package com.baiyi.opscloud.domain.vo.workorder;

import com.baiyi.opscloud.domain.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

/**
 * @Author baiyi
 * @Date 2022/1/6 1:21 PM
 * @Version 1.0
 */
public class WorkOrderVO {

    public interface IWorkOrder {

        Integer getWorkOrderId();

        void setWorkOrder(WorkOrderVO.WorkOrder workOrder);
    }

    @EqualsAndHashCode(callSuper = true)
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    public static class WorkOrder extends BaseVO {

        private WorkOrderVO.Group workOrderGroup;

        private Integer id;

        @Schema(name = "工单名称")
        @NotEmpty(message = "工单名称不能为空")
        private String name;

        @Schema(name = "顺序")
        private Integer seq;

        @Schema(name = "图标")
        private String icon;

        @Schema(name = "工单Key")
        private String workOrderKey;

        @Schema(name = "帮助文档ID")
        private Integer sysDocumentId;

        @Schema(name = "工单组ID")
        private Integer workOrderGroupId;

        @Schema(name = "状态 0 正常 1 开发 2 停用 3")
        private Integer status;

        @Schema(name = "报表颜色")
        private String color;

        @Schema(name = "说明")
        private String comment;

        @Schema(name = "文档地址")
        private String docs;

        private Boolean isActive;

        @Schema(name = "工作流配置")
        private String workflow;

        /**
         * 前端选择用
         */
        private Boolean loading;

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @Schema
    public static class Group extends BaseVO {

        @Schema(name = "工单数量", example = "1")
        private Integer workOrderSize;

        @Schema(name = "组成员工单")
        private List<WorkOrder> workOrders;

        private Integer id;

        @Schema(name = "工单组名称")
        @NotEmpty(message = "工单组名称不能为空")
        private String name;

        @Schema(name = "顺序")
        private Integer seq;

        @Schema(name = "图标")
        private String icon;

        @Schema(name = "工单组类型")
        private Integer groupType;

        @Schema(name = "说明")
        private String comment;
    }
}
