package business.repository;

import business.models.Document;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends PagingAndSortingRepository<Document, UUID> {
    @Modifying
    @Query("delete from Document d where id in :ids")
    void deleteByIdIn(@Param("ids") List<UUID> ids);
}
