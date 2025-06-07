package com.copico.common.entity;

import com.copico.common.exception.BizException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.relational.core.sql.In;

/**
 * @author 陈玉皓
 * @date 2025/6/7 11:03
 * @description: 所有分页接口的请求参数类都必须继承或使用此类
 */
@Data
public class PageParam {
    /**
     * 当前页数
     */
    @Schema(description = "当前页数")
    private Integer currentPage;

    /**
     * 分页条数
     */
    @Schema(description = "分页条数")
    private Integer pageSize;


    @Schema(hidden = true)
    public void validatePage() {
        if (this.currentPage == null || this.currentPage < 1 || this.currentPage > 1000
                || this.pageSize == null || this.pageSize < 1 || this.pageSize > 100) {
            throw  new BizException("分页参数不正确, currentPage = " + this.currentPage + " pageSize = " + pageSize);
        }
    }

    /**
     * 分页时，根据currentPage跳过的记录数
     * @return int 跳过的记录数 LIMIT skip,pageSize
     */
    @Schema(hidden = true)
    public int getSkip(){
        this.validatePage();
        return (this.getCurrentPage() -1) * this.getPageSize();
    }

}
