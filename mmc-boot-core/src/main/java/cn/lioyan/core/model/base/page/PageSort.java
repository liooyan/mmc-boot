package cn.lioyan.core.model.base.page;


import cn.lioyan.core.util.SecAssert;

import java.util.ArrayList;
import java.util.List;

/**
 * PageSort
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class PageSort {

    /**
     * 当前页，默认1
     */
    private int pageNo = 1;
    /**
     * 每页数，默认10
     */
    private int pageSize = 10;

    /**
     * 字多排序,支持多字段排序
     */
    private List<FieldSort> sort = new ArrayList<>(1);

    public PageSort(){}

    public PageSort(int pageNo, int pageSize) {
        setPageNo(pageNo);
        setPageSize(pageSize);
    }

    public PageSort(List<FieldSort> sort) {
        this.sort = sort;
    }

    public int getPageNo() {
        return pageNo ;
    }

    public int getSkip() {
        return (pageNo - 1) * pageSize;
    }

    public void setPageNo(int pageNo) {
        SecAssert.state(pageNo > 0,"pageNo must gt 0");
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        SecAssert.state(pageSize > 0,"pageSize must gt 0");
        this.pageSize = pageSize;
    }

    public List<FieldSort> getSort() {
        return sort;
    }

    public void setSort(List<FieldSort> sort) {
        SecAssert.notNull(sort,"sort must not be null!");
        this.sort = sort;
    }

}
