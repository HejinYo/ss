package cn.hejinyo.ss.common.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * 查询参数
 */
public class PageQuery<T> {
    /**
     * 最大分页大小为100
     */
    private static final Integer MAX_PAGE_SIZE = 100;
    /**
     * 默认大小10
     */
    private static final Integer DEF_PAGE_SIZE = 10;
    /**
     * 查询对象
     */
    @Getter
    @Setter
    private T queryParam;
    /**
     * 当前页
     */
    @Setter
    private int pageNum;
    /**
     * 每页的数量
     */
    @Setter
    private int pageSize;
    /**
     * 排序字段
     */
    @Setter
    private String sidx;
    /**
     * 排序方式
     */
    @Setter
    private String sort;
    /**
     * 字段排序
     */
    private String order;

    public int getPageNum() {
        return this.pageNum;
    }

    public int getPageSize() {
        return this.pageSize > MAX_PAGE_SIZE ? MAX_PAGE_SIZE : (this.pageSize < 1 ? DEF_PAGE_SIZE : this.pageSize);
    }

    public String getSidx() {
        return StringUtils.underscoreName(this.sidx);
    }

    public String getSort() {
        return Optional.ofNullable(this.sort).map(v -> v.toUpperCase().contains("ASC") ? "ASC" : "DESC").orElse("DESC");
    }

    public String getOrder() {
        if (StringUtils.isNotEmpty(this.getSidx())) {
            return this.getSidx() + " " + this.getSort();
        }
        return null;
    }

}
