package cn.lioyan.autoconfigure.data;

import cn.lioyan.core.model.base.page.PageData;
import cn.lioyan.core.model.base.page.PageSort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * CrudService interface contains some common methods.
 *
 * @param <D> domain type
 * @author johnniang
 */
public interface CrudService<D> {


    /**
     * List All
     *
     * @return List
     */
    @NonNull
    List<D> listAll();


    /**
     * List all by pageable
     *
     * @param pageable pageable
     * @return Page
     */
    @NonNull
    PageData<D> listAll(@NonNull PageSort pageable);

    /**
     * List all by ids
     *
     * @param ids ids
     * @return List
     */
    @NonNull
    List<D> listAllByIds(@Nullable Collection<Long> ids);


    /**
     * Fetch by id
     *
     * @param id id
     * @return Optional
     */
    @NonNull
    Optional<D> fetchById(@NonNull Long id);

    /**
     * Get by id
     *
     * @param id id
     * @return DOMAIN
     */
    @NonNull
    D getById(@NonNull Long id);

    /**
     * Gets domain of nullable by id.
     *
     * @param id id
     * @return DOMAIN
     */
    @Nullable
    D getByIdOfNullable(@NonNull Long id);

    /**
     * Exists by id.
     *
     * @param id id
     * @return boolean
     */
    boolean existsById(@NonNull Long id);


    /**
     * count all
     *
     * @return long
     */
    long count();

    /**
     * save by domain
     *
     * @param domain domain
     * @return DOMAIN
     */
    @NonNull
    @Transactional
    D create(@NonNull D domain);

    /**
     * save by domains
     *
     * @param domains domains
     * @return List
     */
    @NonNull
    @Transactional
    List<D> createInBatch(@NonNull Collection<D> domains);

    /**
     * Updates by domain
     *
     * @param domain domain
     * @return DOMAIN
     */
    @NonNull
    @Transactional
    D update(@NonNull D domain);


    /**
     * Updates by domains
     *
     * @param domains domains
     * @return List
     */
    @NonNull
    @Transactional
    List<D> updateInBatch(@NonNull Collection<D> domains);

    /**
     * Removes by id
     *
     * @param id id
     * @return DOMAIN
     */
    @NonNull
    @Transactional
    D removeById(@NonNull Long id);

    /**
     * Removes by id if present.
     *
     * @param id id
     * @return DOMAIN
     */
    @Nullable
    @Transactional
    D removeByIdOfNullable(@NonNull Long id);

    /**
     * Remove by domain
     *
     * @param domain domain
     */
    @Transactional
    void remove(@NonNull D domain);

    /**
     * Remove by ids
     *
     * @param ids ids
     */
    @Transactional
    void removeInBatch(@NonNull Collection<Long> ids);

    /**
     * Remove all by domains
     *
     * @param domains domains
     */
    @Transactional
    void removeAll(@NonNull Collection<D> domains);

    void existsThrowException(Long id);
    void existsThrowException(D domain);
    void missThrowException(Long id);
    void missThrowException(D domain);

    /**
     * Remove all
     */
    @Transactional
    void removeAll();
}
