package business.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wf.sample.models.LoadTestingOperationModel;

import java.util.UUID;

@Repository
public interface LoadTestingOperationModelRepository extends CrudRepository<LoadTestingOperationModel, UUID> {
}
