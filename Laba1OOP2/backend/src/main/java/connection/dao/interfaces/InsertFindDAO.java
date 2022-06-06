package connection.dao.interfaces;

import entity.Entity;

import java.util.List;

/**
 * Provides declarations of create, find and findAll operations
 * @param <E> class of Entity object DAO is working with
 */
public interface InsertFindDAO<E extends Entity> {
    public List<E> getAll();

    public E get(int id);

    public boolean add(E entity);
}
