package connection.dao.interfaces;

import entity.Entity;

/**
 * Provides declarations of CRUD operations
 * @param <E> class of Entity object DAO is working with
 */
public interface BaseDAO<E extends Entity> extends InsertFindDAO<E> {
    public boolean edit(E entity);

    public boolean delete(int id);
}
