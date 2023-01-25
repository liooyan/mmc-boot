package cn.lioyan.autoconfigure.data;

import cn.lioyan.core.model.base.BaseBean;
import cn.lioyan.core.model.base.page.PageData;
import cn.lioyan.core.model.base.page.PageSort;
import cn.lioyan.autoconfigure.exception.DataExistsException;
import cn.lioyan.autoconfigure.exception.DataMissException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Abstract service implementation.
 *
 * @param <DOMAIN> domain type
 * @author johnniang
 */
public abstract class AbstractCrudService<DOMAIN extends BaseBean> implements CrudService<DOMAIN> {

    private final String domainName;

    private final BaseRepository<DOMAIN> repository;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCrudService.class);

    protected AbstractCrudService(BaseRepository<DOMAIN> repository) {
        this.repository = repository;

        // Get domain name
        @SuppressWarnings("unchecked")
        Class<DOMAIN> domainClass = (Class<DOMAIN>) fetchType(0);
        domainName = domainClass.getSimpleName();
    }

    /**
     * Gets actual generic type.
     *
     * @param index generic type index
     * @return real generic type will be returned
     */
    private Type fetchType(int index) {
        Assert.isTrue(index >= 0 && index <= 1, "type index must be between 0 to 1");

        return ((ParameterizedType) this.getClass().getGenericSuperclass())
            .getActualTypeArguments()[index];
    }

    /**
     * List All
     *
     * @return List
     */
    @Override
    public List<DOMAIN> listAll() {
        return repository.findAll();
    }


    /**
     * List all by pageable
     *
     * @param pageable pageable
     * @return Page
     */
    @Override
    public PageData<DOMAIN> listAll(PageSort pageable) {
        Assert.notNull(pageable, "Pageable info must not be null");

        return repository.findAll(pageable);
    }

    /**
     * List all by ids
     *
     * @param ids ids
     * @return List
     */
    @Override
    public List<DOMAIN> listAllByIds(Collection<Long> ids) {
        return CollectionUtils.isEmpty(ids) ? Collections.emptyList() : repository.findAllById(ids);
    }


    /**
     * Fetch by id
     *
     * @param id id
     * @return Optional
     */
    @Override
    public Optional<DOMAIN> fetchById(Long id) {
        Assert.notNull(id, domainName + " id must not be null");

        return repository.findById(id);
    }

    /**
     * Get by id
     *
     * @param id id
     * @return DOMAIN
     */
    @Override
    public DOMAIN getById(Long id) {
        return fetchById(id).orElseThrow(
            () -> new RuntimeException(domainName + " was not found or has been deleted"));
    }

    /**
     * Gets domain of nullable by id.
     *
     * @param id id
     * @return DOMAIN
     */
    @Override
    public DOMAIN getByIdOfNullable(Long id) {
        return fetchById(id).orElse(null);
    }

    /**
     * Exists by id.
     *
     * @param id id
     * @return boolean
     */
    @Override
    public boolean existsById(Long id) {
        Assert.notNull(id, domainName + " id must not be null");

        return repository.existsById(id);
    }

    /**
     * Must exist by id, or throw NotFoundException.
     *
     * @param id id
     */

    /**
     * count all
     *
     * @return long
     */
    @Override
    public long count() {
        return repository.count();
    }

    /**
     * save by domain
     *
     * @param domain domain
     * @return DOMAIN
     */
    @Override
    public DOMAIN create(DOMAIN domain) {
        Assert.notNull(domain, domainName + " data must not be null");

        return repository.save(domain);
    }

    /**
     * save by domains
     *
     * @param domains domains
     * @return List
     */
    @Override
    public List<DOMAIN> createInBatch(Collection<DOMAIN> domains) {
        return CollectionUtils.isEmpty(domains) ? Collections.emptyList() :
            repository.saveAll(domains);
    }

    /**
     * Updates by domain
     *
     * @param domain domain
     * @return DOMAIN
     */
    @Override
    public DOMAIN update(DOMAIN domain) {
        Assert.notNull(domain, domainName + " data must not be null");

        return repository.update(domain);
    }


    /**
     * Updates by domains
     *
     * @param domains domains
     * @return List
     */
    @Override
    public List<DOMAIN> updateInBatch(Collection<DOMAIN> domains) {
        return CollectionUtils.isEmpty(domains) ? Collections.emptyList() :
            repository.saveAll(domains);
    }

    /**
     * Removes by id
     *
     * @param id id
     * @return DOMAIN
     */
    @Override
    public DOMAIN removeById(Long id) {
        // Get non null domain by id
        DOMAIN domain = getById(id);

        // Remove it
        remove(domain);

        // return the deleted domain
        return domain;
    }

    /**
     * Removes by id if present.
     *
     * @param id id
     * @return DOMAIN
     */
    @Override
    public DOMAIN removeByIdOfNullable(Long id) {
        return fetchById(id).map(domain -> {
            remove(domain);
            return domain;
        }).orElse(null);
    }

    /**
     * Remove by domain
     *
     * @param domain domain
     */
    @Override
    public void remove(DOMAIN domain) {
        Assert.notNull(domain, domainName + " data must not be null");

        repository.delete(domain);
    }

    /**
     * Remove by ids
     *
     * @param ids ids
     */
    @Override
    public void removeInBatch(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            LOG.debug(domainName + " id collection is empty");
            return;
        }

        repository.deleteByIdIn(ids);
    }

    /**
     * Remove all by domains
     *
     * @param domains domains
     */
    @Override
    public void removeAll(Collection<DOMAIN> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            LOG.debug(domainName + " collection is empty");
            return;
        }
        repository.deleteAll(domains);
    }

    /**
     * Remove all
     */
    @Override
    public void existsThrowException(Long id)
    {
        if (existsById(id))
        {
            throw DataExistsException.newInstance();
        }
    }


    @Override
    public void existsThrowException(DOMAIN domain){
        if (repository.exists(domain))
        {
            throw DataExistsException.newInstance();
        }
    }
    @Override
    public void missThrowException(Long id){
        if (!existsById(id))
        {
            throw DataMissException.newInstance();
        }
    }
    @Override
    public void missThrowException(DOMAIN domain){
        if (!repository.exists(domain))
        {
            throw DataMissException.newInstance();
        }
    }


    @Override
    public void removeAll()
    {
        repository.deleteAll();
    }

}
