package business.repository;

import business.models.Document;
import business.models.DocumentTransitionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DocumentTransitionHistoryRepository extends PagingAndSortingRepository<DocumentTransitionHistory, UUID> {
    @Query("select distinct h.document from DocumentTransitionHistory h where h.employee.id = :id")
    Page<Document> findByEmployeeId(@Param("id") UUID id, Pageable pageable);

    @Modifying
    @Query("delete from DocumentTransitionHistory h where h.document.id = :processId and h.transitionTime is null")
    void deleteEmptyPreHistory(@Param("processId") UUID processId);
}
