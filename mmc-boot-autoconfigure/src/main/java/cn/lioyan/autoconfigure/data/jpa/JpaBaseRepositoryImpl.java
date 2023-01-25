package cn.lioyan.autoconfigure.data.jpa;

import cn.lioyan.core.model.base.BaseBean;
import cn.lioyan.core.model.base.page.FieldSort;
import cn.lioyan.core.model.base.page.PageData;
import cn.lioyan.core.model.base.page.PageSort;
import cn.lioyan.core.util.NullUtil;
import com.sun.istack.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of base repository.
 *
 * @param <DOMAIN> domain type
 * @author johnniang
 * @author ryanwang
 * @since 2022/4/13 15:14
 */
public class JpaBaseRepositoryImpl<DOMAIN extends BaseBean> extends SimpleJpaRepository<DOMAIN, Long>
        implements JpaBaseRepository<DOMAIN> {


    private static final Logger LOG = LoggerFactory.getLogger(JpaBaseRepositoryImpl.class);

    private final JpaEntityInformation<DOMAIN, Long> entityInformation;

    private final EntityManager entityManager;

    public JpaBaseRepositoryImpl(JpaEntityInformation<DOMAIN, Long> entityInformation,
                                 EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }


    @Override
    public PageData<DOMAIN> findAll(PageSort pageSort) {

        Page<DOMAIN> all = super.findAll(pageSort2Pageable(pageSort));
        return page2PageData(all);
    }

    @Override
    public Optional<DOMAIN> findOne(DOMAIN s) {
        return super.findOne(Example.of(s));
    }


    @Override
    public List<DOMAIN> findAll(DOMAIN s) {
        return super.findAll(Example.of(s));
    }

    @Override
    public PageData<DOMAIN> findAll(DOMAIN s, PageSort pageable) {
        Page<DOMAIN> all = super.findAll(Example.of(s), pageSort2Pageable(pageable));
        return page2PageData(all);
    }

    @Override
    @Transactional
    public long deleteByIdIn(Collection<Long> ids) {

        LOG.debug("Customized deleteByIdIn method was invoked");
        // Find all domains
        List<DOMAIN> domains = findAllById(ids);

        // Delete in batch
        deleteInBatch(domains);

        // Return the size of domain deleted
        return domains.size();
    }


    @Override
    public long count(DOMAIN s) {
        return super.count(Example.of(s));
    }

    @Override
    public boolean exists(DOMAIN s) {
        return super.exists(Example.of(s));
    }



    public <S extends DOMAIN> S update(S domain) {
        return super.saveAndFlush(domain);
    }

    public static Pageable pageSort2Pageable(@NotNull PageSort pageSort) {
        List<FieldSort> sort = pageSort.getSort();
        if (NullUtil.notNull(sort)) {
            List<Sort.Order> collect = sort.stream().map(s -> {
                String sortKey = s.getSort();
                if (FieldSort.ASC.equals(sortKey)) {
                    return Sort.Order.asc(s.getField());
                } else {
                    return Sort.Order.desc(s.getField());
                }
            }).collect(Collectors.toList());
            return PageRequest.of(pageSort.getPageNo() - 1, pageSort.getPageSize(), Sort.by(collect));
        } else {
            return PageRequest.of(pageSort.getPageNo() - 1, pageSort.getPageSize());
        }

    }

    public static <S> PageData<S> page2PageData(@NotNull Page<S> page) {
        return new PageData<>(page.getContent(), page.getTotalElements());

    }

    private static final class ByIdsSpecification<T> implements Specification<T> {
        private static final long serialVersionUID = 1L;
        private final JpaEntityInformation<T, ?> entityInformation;
        @Nullable
        ParameterExpression<Collection> parameter;

        ByIdsSpecification(JpaEntityInformation<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Path<?> path = root.get(this.entityInformation.getIdAttribute());
            this.parameter = cb.parameter(Collection.class);
            return path.in(this.parameter);
        }
    }
}
