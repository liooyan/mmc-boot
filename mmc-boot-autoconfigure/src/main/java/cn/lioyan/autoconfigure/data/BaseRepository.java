package cn.lioyan.autoconfigure.data;

import cn.lioyan.core.model.base.BaseBean;
import cn.lioyan.core.model.base.page.PageData;
import cn.lioyan.core.model.base.page.PageSort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * {@link BaseRepository}
 *
 * @author cn.lioyan
 * @since 2022/4/14 17:30
 */
public interface BaseRepository<T extends BaseBean>  {


    <S extends T> S save(S entity);
    <S extends T> List<S> saveAll(Iterable<S> entities);

    <S extends T>  T update(S domain);

    Optional<T> findById(Long id);

    long count();

    boolean existsById(Long id);




    List<T> findAll();

    List<T> findAllById(Iterable<Long> ids);

    void deleteById(Long id);

    void delete(T entity);

    void deleteAllById(Iterable<? extends Long> ids);

    void deleteAll(Iterable<? extends T> entities);

    List<T> findAll(T s);

    PageData<T> findAll(T s, PageSort pageable);

    long deleteByIdIn(Collection<Long> ids);

    void deleteAll();


    PageData<T> findAll(PageSort pageSort);


    Optional<T> findOne(T s);



     long count(T s);

    boolean exists(T s);


    T getOne(Long id);

}
