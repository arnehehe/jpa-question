import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.acme.MyEntity;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class RowVersionTest {

    @Inject
    EntityManager em;

    @Test
    public void testRowVersion(){
        MyEntity entity = createEntity();
        Long id = entity.id;

        for (int i=0; i < 10; i++){
            MyEntity entityById = getById(id);
            entityById.field += i;
            System.out.println("Before: " +entityById.field + " - RowVersion: " + entityById.rowVersion);
            update(entityById);
            System.out.println("After: " +entityById.field + " - RowVersion: " + entityById.rowVersion);
        }
    }

    @Transactional
    public MyEntity createEntity() {
        MyEntity newEntity = new MyEntity();
        newEntity.field = "Hello";
        em.persist(newEntity);
        return newEntity;
    }

    public MyEntity getById(Long id){
        TypedQuery<MyEntity> query = em.createQuery("Select e from MyEntity e where e.id = :id", MyEntity.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Transactional
    public void update(MyEntity entity){
        em.merge(entity);
    }
}
