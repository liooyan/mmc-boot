package cn.lioyan.autoconfigure.data.mybaits;

import cn.lioyan.core.model.base.BaseBean;
import cn.lioyan.core.model.base.page.FieldSort;
import cn.lioyan.core.model.base.page.PageData;
import cn.lioyan.core.model.base.page.PageSort;
import cn.lioyan.core.util.NullUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.lioyan.autoconfigure.data.BaseRepository;
import com.sun.istack.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * {@link BaseMapping}
 *
 * @author cn.lioyan
 * @since 2022/4/14 17:30
 */
public interface BaseMapping<T extends BaseBean> extends BaseRepository<T>, BaseMapper<T> {


    default <S extends T> S save(S entity) {
        insert(entity);
        return entity;
    }

    default <S extends T> List<S> saveAll(Iterable<S> entities) {
        if (NullUtil.isNull(entities)) {
            return new ArrayList<>();
        }
        List<S> list = new ArrayList<>();
        for (S entity : entities) {
            insert(entity);
            list.add(entity);
        }
        return list;
    }

    default <S extends T> T update(S domain) {
        updateById(domain);
        return domain;
    }

    default Optional<T> findById(Long id) {
        T t = selectById(id);
        return Optional.ofNullable(t);
    }

    default long count() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        return selectCount(queryWrapper);
    }

    default boolean existsById(Long id) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return exists(queryWrapper);
    }


    default List<T> findAll() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        return selectList(queryWrapper);
    }

    default List<T> findAllById(Iterable<Long> ids) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        return selectList(queryWrapper);
    }


    default void delete(T entity) {
        deleteById(entity);
    }

    default void deleteAllById(Iterable<? extends Long> ids) {
        if (NullUtil.notNull(ids)) {
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", ids);
            delete(queryWrapper);
        }
    }

    default void deleteAll(Iterable<? extends T> entities) {
        if (NullUtil.notNull(entities)) {
            List<Number> list = new ArrayList<>();
            for (T entity : entities) {
                if (NullUtil.notNull(entity.getId())) {
                    list.add(entity.getId());
                }
            }
            QueryWrapper<T> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", list);
            delete(queryWrapper);
        }
    }

    default List<T> findAll(T s) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(s);
        return selectList(queryWrapper);
    }

    default PageData<T> findAll(T s, PageSort pageable) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(s);
        IPage<T> iPage = pageSort2IPage(pageable);
        iPage = selectPage(iPage, queryWrapper);
        return page2PageData(iPage);
    }

    default long deleteByIdIn(Collection<Long> ids) {
        return deleteBatchIds(ids);
    }

    default void deleteAll() {
        delete(new QueryWrapper<>());
    }


    default PageData<T> findAll(PageSort pageSort) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        IPage<T> iPage = pageSort2IPage(pageSort);
        iPage = selectPage(iPage, queryWrapper);
        return page2PageData(iPage);
    }


    default Optional<T> findOne(T s) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(s);
        return Optional.ofNullable(selectOne(queryWrapper));
    }


    default long count(T s) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(s);
        return selectCount(queryWrapper);
    }

    default boolean exists(T s) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(s);
        return exists(queryWrapper);
    }


    default T getOne(Long id) {
        return findById(id).get();
    }


    static <T> PageData<T> page2PageData(@NotNull IPage<T> page) {
        return new PageData<>(page.getRecords(), page.getTotal());

    }

    static <T> IPage<T> pageSort2IPage(@NotNull PageSort pageSort) {
        Page<T> page = new Page<>(pageSort.getPageNo(), pageSort.getPageSize());
        List<FieldSort> sort = pageSort.getSort();
        if (NullUtil.notNull(sort)) {
            sort.forEach(s -> {
                String sortKey = s.getSort();
                if (FieldSort.ASC.equals(sortKey)) {
                    page.addOrder(OrderItem.asc(s.getField()));
                } else {
                    page.addOrder(OrderItem.desc(s.getField()));
                }
            });
        }
        return  page;

    }
}
