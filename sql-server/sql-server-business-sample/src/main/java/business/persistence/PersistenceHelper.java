package business.persistence;

import business.models.Document;
import business.models.Employee;
import business.repository.DocumentRepository;
import business.repository.DocumentTransitionHistoryRepository;
import business.repository.EmployeeRepository;
import business.workflow.WorkflowInit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import optimajet.workflow.core.persistence.IPersistenceProvider;
import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.core.runtime.ProcessStatusChangedEventArgs;
import optimajet.workflow.mssql.MsSqlServerProvider;
import wf.sample.models.PageResponse;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersistenceHelper {

    @SuppressWarnings("unused")
    public static Document getDocument(UUID id) {
        // note used from script
        DocumentRepository documentRepository = ApplicationContextProvider.getBean(DocumentRepository.class);
        return documentRepository.findOne(id);
    }

    @SuppressWarnings("unused")
    public static Employee getEmployee(UUID id) {
        // note used from script
        EmployeeRepository employeeRepository = ApplicationContextProvider.getBean(EmployeeRepository.class);
        return employeeRepository.findOne(id);
    }

    public static void deleteProcess(List<UUID> ids) {
        getProvider().deleteProcess(ids.toArray(new UUID[]{}));
    }

    public static WorkflowDocumentProvider createProvider() {
        DataSource dataSource = (DataSource) ApplicationContextProvider.getBean("dataSource");
        return new MsSqlServerProvider(dataSource);
    }

    private static MsSqlServerProvider getProvider() {
        IPersistenceProvider persistenceProvider = WorkflowInit.getRuntime().getPersistenceProvider();
        if (persistenceProvider instanceof MsSqlServerProvider) {
            return (MsSqlServerProvider) persistenceProvider;
        }
        throw new IllegalArgumentException("Runtime is not instance of MongoDbProvider");
    }
}