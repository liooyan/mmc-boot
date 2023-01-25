package cn.lioyan.core.model.base.page;


import java.util.List;

/**
 * PageSort
 *
 * @author com.lioyan
 * @since 2022/4/13 15:14
 */
public class PageData<T> {

    private List<T> data;

    private Long count;

    public PageData(List<T> data, Long count) {
        this.data = data;
        this.count = count;
    }



    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
