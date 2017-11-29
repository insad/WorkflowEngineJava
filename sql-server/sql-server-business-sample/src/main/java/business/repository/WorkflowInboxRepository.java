package business.repository;

import business.models.Document;
import business.models.Employee;
import business.models.WorkflowInbox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkflowInboxRepository extends PagingAndSortingRepository<WorkflowInbox, UUID> {

    @Modifying
    @Query("delete from WorkflowInbox i where i.processId in :processIds")
    void deleteByProcessIdIn(@Param("processIds") List<UUID> processIds);

    @Modifying
    @Query("delete from WorkflowInbox i where i.processId = :processId")
    void deleteByProcessId(@Param("processId") UUID processId);

    int countByEmployee(Employee employee);

    Page<WorkflowInbox> findByEmployee(Employee employee, Pageable pageable);

    @Query("select distinct i.document from WorkflowInbox i where i.employee.id = :id order by i.document.number desc")
    Page<Document> getInbox(@Param("id") UUID id, Pageable pageable);
}
