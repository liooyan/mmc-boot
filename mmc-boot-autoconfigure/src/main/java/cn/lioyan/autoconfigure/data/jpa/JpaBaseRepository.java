package cn.lioyan.autoconfigure.data.jpa;

import cn.lioyan.core.model.base.BaseBean;
import cn.lioyan.autoconfigure.data.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link JpaBaseRepository}
 *
 * @author cn.lioyan
 * @since 2022/4/14 17:30
 */
public interface JpaBaseRepository<T extends BaseBean> extends JpaRepository<T, Long>, BaseRepository<T> {








}
