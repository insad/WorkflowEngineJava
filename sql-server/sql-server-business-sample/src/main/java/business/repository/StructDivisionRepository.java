package business.repository;

import business.models.StructDivision;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface StructDivisionRepository extends CrudRepository<StructDivision, UUID> {
}
