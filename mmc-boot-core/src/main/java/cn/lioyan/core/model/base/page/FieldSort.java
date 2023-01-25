package cn.lioyan.core.model.base.page;


import cn.lioyan.core.util.SecAssert;

/**
 * FieldSort
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class FieldSort {

    public static final String ASC = "asc";
    public static final String DESC = "desc";

    /**
     * 排序字段名称
     */
    private String field;

    /**
     * asc:升序
     * desc:降序
     * 忽略大小写比较
     */
    private String sort;

    public FieldSort() {
    }

    public FieldSort(String field, String sort) {
        setField(field);
        setSort(sort);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        SecAssert.hasText(field,"field must not be empty");
        this.field = field;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

}
