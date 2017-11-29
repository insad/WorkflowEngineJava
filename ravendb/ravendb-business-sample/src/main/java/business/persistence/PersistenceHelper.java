package business.persistence;

import business.models.*;
import business.workflow.WorkflowInit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ravendb.abstractions.data.IndexQuery;
import net.ravendb.abstractions.indexing.IndexDefinition;
import net.ravendb.client.IDocumentQuery;
import net.ravendb.client.IDocumentSession;
import net.ravendb.client.connection.Operation;
import net.ravendb.client.document.DocumentQueryCustomizationFactory;
import net.ravendb.client.document.DocumentStore;
import net.ravendb.client.linq.IRavenQueryable;
import optimajet.workflow.core.persistence.IPersistenceProvider;
import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.core.runtime.ProcessStatusChangedEventArgs;
import optimajet.workflow.core.util.UUIDUtil;
import optimajet.workflow.ravendb.RavenDbProvider;
import wf.sample.models.PageResponse;

import java.io.IOException;
import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersistenceHelper {
    private static final String INDEX_DEFINITION = "from doc in docs.Documents\n" +
            " from transition in doc.TransitionHistories\n" +
            " select new { WFExecutorId = transition.EmployeeId }";
    private static final String INDEX_NAME = "DocumentByWFExecutorId";

    public static <T> PageResponse<T> getPageResponse(int page, int pageSize, Class<T> cls) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            IRavenQueryable<T> query = session.query(cls);
            int count = query.count();
            int actual = page * pageSize;

            List<T> documents = query.skip(actual).take(pageSize).toList();
            return new PageResponse<>(count, documents);
        }
    }

    public static <T> T get(Class<T> cls, UUID id) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            return session.load(cls, id);
        }
    }

    public static <T> T get(Class<T> cls, String id) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            return session.load(cls, id);
        }
    }

    public static <T> List<T> asList(Class<T> cls) {
        int start = 0;
        List<T> result = new ArrayList<>();
        try (IDocumentSession session = getDocumentStore().openSession()) {
            while (true) {
                List<T> list = session.query(cls).take(1024).skip(start).toList();
                if (list.isEmpty()) {
                    break;
                }
                start += list.size();
                result.addAll(list);
            }
        }
        return result;
    }

    public static <T> void save(T entity) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            session.store(entity);
            session.saveChanges();
        }
    }

    public static <T> void save(Iterable<T> entities) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            for (T t : entities) {
                session.store(t);
            }
            session.saveChanges();
        }
    }

    public static <T> void clean(Class<T> cls) {
        Operation operation = getDocumentStore().getDatabaseCommands()
                .deleteByIndex("Raven/DocumentsByEntityName",
                        new IndexQuery("Tag:" + cls.getSimpleName() + "s"));

        operation.waitForCompletion();
    }

    @SuppressWarnings("unused")
    public static Document getDocument(UUID id) {
        // note used from script
        return PersistenceHelper.get(Document.class, id);
    }

    @SuppressWarnings("unused")
    public static Employee getEmployee(UUID id) {
        // note used from script
        return PersistenceHelper.get(Employee.class, id);
    }

    public static int getWorkflowInboxCountForEmployee(Employee employee) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            QWorkflowInbox qWorkflowInbox = QWorkflowInbox.workflowInbox;
            return session.query(WorkflowInbox.class)
                    .customize(new DocumentQueryCustomizationFactory().waitForNonStaleResultsAsOfNow())
                    .where(qWorkflowInbox.identityId.eq(UUIDUtil.asString(employee.getId())))
                    .count();
        }
    }

    public static WorkflowInbox getRandomWorkflowInbox(Employee employee, int skip) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            QWorkflowInbox qWorkflowInbox = QWorkflowInbox.workflowInbox;
            return session.query(WorkflowInbox.class)
                    .customize(new DocumentQueryCustomizationFactory().waitForNonStaleResultsAsOfNow())
                    .where(qWorkflowInbox.identityId.eq(UUIDUtil.asString(employee.getId())))
                    .skip(skip)
                    .first();
        }
    }

    public static void deleteInboxForProcess(ProcessStatusChangedEventArgs e) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            QWorkflowInbox qWorkflowInbox = QWorkflowInbox.workflowInbox;
            List<WorkflowInbox> workflowInboxList = session.query(WorkflowInbox.class)
                    .customize(new DocumentQueryCustomizationFactory().waitForNonStaleResultsAsOfNow())
                    .where(qWorkflowInbox.processId.eq(e.getProcessId()))
                    .toList();
            for (WorkflowInbox workflowInbox : workflowInboxList) {
                session.delete(workflowInbox);
            }
            session.saveChanges();
        }
    }

    public static void deleteProcess(List<UUID> ids) {
        getProvider().deleteProcess(ids.toArray(new UUID[]{}));
    }

    public static void deleteDocuments(List<UUID> uuidList) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            for (UUID id : uuidList) {
                session.delete(Document.class, id);
            }

            QWorkflowInbox qWorkflowInbox = QWorkflowInbox.workflowInbox;
            List<WorkflowInbox> workflowInboxList = session.query(WorkflowInbox.class)
                    .customize(new DocumentQueryCustomizationFactory().waitForNonStaleResultsAsOfNow())
                    .where(qWorkflowInbox.processId.in(uuidList)).toList();
            for (WorkflowInbox workflowInbox : workflowInboxList) {
                session.delete(workflowInbox);
            }

            session.saveChanges();
        }
    }

    public static PageResponse<Document> getInbox(UUID identityId, int page, int pageSize) {
        try (IDocumentSession session = getDocumentStore().openSession()) {
            QWorkflowInbox qWorkflowInbox = QWorkflowInbox.workflowInbox;
            IRavenQueryable<WorkflowInbox> query = session.query(WorkflowInbox.class)
                    .customize(new DocumentQueryCustomizationFactory().waitForNonStaleResultsAsOfNow())
                    .where(qWorkflowInbox.identityId.eq(UUIDUtil.asString(identityId)));
            int count = query.count();
            int actual = page * pageSize;

            List<WorkflowInbox> inboxList = query.skip(actual).take(pageSize).toList();
            if (inboxList.isEmpty()) {
                return new PageResponse<>(count, new ArrayList<Document>());
            }

            Set<String> idSet = new HashSet<>();
            for (WorkflowInbox workflowInbox : inboxList) {
                idSet.add("documents/" + workflowInbox.getProcessId().toString());
            }

            Document[] documents = session.load(Document.class, idSet);
            return new PageResponse<>(count, Arrays.asList(documents));
        }
    }

    public static PageResponse<Document> getOutbox(UUID identityId, int page, int pageSize) {
        DocumentStore documentStore = getDocumentStore();
        try (IDocumentSession session = documentStore.openSession()) {
            IndexDefinition index = documentStore.getDatabaseCommands().getIndex(INDEX_NAME);
            if (index == null) {
                documentStore.getDatabaseCommands().putIndex(INDEX_NAME, new IndexDefinition(INDEX_DEFINITION));
            }

            IDocumentQuery<Document> query = session.advanced().documentQuery(Document.class, INDEX_NAME)
                    .where("WFExecutorId:" + identityId);
            int count = query
                    .getQueryResult()
                    .getTotalResults();
            int actual = page * pageSize;

            List<Document> documents = query.skip(actual).take(pageSize).toList();
            return new PageResponse<>(count, documents);
        }
    }

    public static long getNextDocumentNumber() {
        long result = 1;
        try (IDocumentSession session = getDocumentStore().openSession()) {
            SettingParam number = session.load(SettingParam.class, "documentnumber");

            if (number == null) {
                session.store(new SettingParam("documentnumber", "" + (result + 1)));
            } else {
                result = Long.parseLong(number.getValue());
                number.setValue("" + (result + 1));
                session.store(number);
            }

            session.saveChanges();
        }
        return result;
    }

    public static WorkflowDocumentProvider createProvider() {
        Properties ravenProperties = getApplicationProperties();
        String url = ravenProperties.getProperty("ravendb.url");
        String database = ravenProperties.getProperty("ravendb.database");
        log.info("Using url {} and database {}", url, database);
        DocumentStore documentStore = new DocumentStore(url, database);
        return new RavenDbProvider(documentStore);
    }

    private static Properties getApplicationProperties() {
        try {
            Properties properties = new Properties();
            properties.load(WorkflowInit.class.getResourceAsStream("/application.properties"));
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static DocumentStore getDocumentStore() {
        return getProvider().getDocumentStore();
    }

    private static RavenDbProvider getProvider() {
        IPersistenceProvider persistenceProvider = WorkflowInit.getRuntime().getPersistenceProvider();
        if (persistenceProvider instanceof RavenDbProvider) {
            return (RavenDbProvider) persistenceProvider;
        }
        throw new IllegalArgumentException("Runtime is not instance of MongoDbProvider");
    }
}