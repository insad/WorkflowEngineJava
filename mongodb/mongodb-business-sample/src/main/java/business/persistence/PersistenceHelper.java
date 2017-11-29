package business.persistence;

import business.models.Document;
import business.models.Employee;
import business.models.SettingParam;
import business.models.WorkflowInbox;
import business.workflow.WorkflowInit;
import com.mongodb.MongoClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import optimajet.workflow.core.persistence.IPersistenceProvider;
import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.core.runtime.ProcessStatusChangedEventArgs;
import optimajet.workflow.core.util.UUIDUtil;
import optimajet.workflow.mongodb.MongoDbProvider;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import wf.sample.models.PageResponse;

import java.io.IOException;
import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersistenceHelper {

    public static <T> PageResponse<T> getPageResponse(int page, int pageSize, Class<T> cls) {
        Query<T> query = getDatastore().find(cls);
        long count = query.count();
        int actual = page * pageSize;
        List<T> documents = query.asList(new FindOptions().skip(actual).limit(pageSize));
        return new PageResponse<>(count, documents);
    }

    public static <T> T get(Class<T> cls, Object id) {
        return getDatastore().get(cls, id);
    }

    public static <T> List<T> asList(Class<T> cls) {
        return getDatastore().find(cls).asList();
    }

    public static <T> void save(T entity) {
        getDatastore().save(entity);
    }

    public static <T> void save(Iterable<T> entities) {
        getDatastore().save(entities);
    }

    public static <T> void clean(Class<T> cls) {
        Datastore datastore = getDatastore();
        Query<T> query = datastore.createQuery(cls);
        datastore.delete(query);
    }

    @SuppressWarnings("unused")
    public static Document getDocument(Object id) {
        // note used from script
        return PersistenceHelper.get(Document.class, id);
    }

    @SuppressWarnings("unused")
    public static Employee getEmployee(Object id) {
        // note used from script
        return PersistenceHelper.get(Employee.class, id);
    }

    public static int getWorkflowInboxCountForEmployee(Employee employee) {
        Query<WorkflowInbox> query = getDatastore().createQuery(WorkflowInbox.class)
                .field("identityId").equal(UUIDUtil.asString(employee.getId()));
        return (int) query.count();
    }

    public static WorkflowInbox getRandomWorkflowInbox(Employee employee, int skip) {
        Query<WorkflowInbox> inboxQuery = getDatastore().createQuery(WorkflowInbox.class)
                .field("identityId").equal(UUIDUtil.asString(employee.getId()));
        return inboxQuery.get(new FindOptions().skip(skip).limit(1));
    }

    public static void deleteInboxForProcess(ProcessStatusChangedEventArgs e) {
        Datastore datastore = getDatastore();
        Query<WorkflowInbox> query = datastore.createQuery(WorkflowInbox.class)
                .field("processId").equal(e.getProcessId());
        datastore.delete(query);
    }

    public static void deleteProcess(List<UUID> ids) {
        getProvider().deleteProcess(ids.toArray(new UUID[]{}));
    }

    public static void deleteDocuments(List<UUID> uuidList) {
        Datastore datastore = getDatastore();
        Query<Document> documentQuery = datastore
                .createQuery(Document.class)
                .field("id").in(uuidList);
        datastore.delete(documentQuery);

        Query<WorkflowInbox> workflowInboxQuery = datastore
                .createQuery(WorkflowInbox.class)
                .field("processId").in(uuidList);
        datastore.delete(workflowInboxQuery);
    }

    public static PageResponse<Document> getInbox(UUID identityId, int page, int pageSize) {
        Datastore datastore = getDatastore();
        Query<WorkflowInbox> query = datastore.find(WorkflowInbox.class)
                .field("identityId").equal(UUIDUtil.asString(identityId));
        long count = query.count();
        int actual = page * pageSize;

        List<WorkflowInbox> inboxList = query.asList(new FindOptions().skip(actual).limit(pageSize));
        if (inboxList.isEmpty()) {
            return new PageResponse<>(count, new ArrayList<Document>());
        }

        Set<UUID> idSet = new HashSet<>();
        for (WorkflowInbox workflowInbox : inboxList) {
            idSet.add(workflowInbox.getProcessId());
        }
        List<Document> documents = datastore.find(Document.class)
                .field("id").in(idSet).asList();
        return new PageResponse<>(count, documents);
    }

    public static PageResponse<Document> getOutbox(UUID identityId, int page, int pageSize) {
        Query<Document> query = getDatastore().find(Document.class)
                .field("transitionHistories.employeeId").equal(identityId);
        long count = query.count();
        int actual = page * pageSize;

        List<Document> documents = query.asList(new FindOptions().skip(actual).limit(pageSize));
        return new PageResponse<>(count, documents);
    }

    public static long getNextDocumentNumber() {
        long result = 1;
        Datastore datastore = getDatastore();
        SettingParam number = datastore.get(SettingParam.class, "documentnumber");
        if (number == null) {
            datastore.save(new SettingParam("documentnumber", "" + (result + 1)));
        } else {
            result = Long.parseLong(number.getValue());
            number.setValue("" + (result + 1));
            datastore.save(number);
        }
        return result;
    }

    public static WorkflowDocumentProvider createProvider() {
        Properties mongoProperties = getApplicationProperties();
        String mongoHost = mongoProperties.getProperty("spring.data.mongodb.host");
        String mongoPort = mongoProperties.getProperty("spring.data.mongodb.port");
        String mongoDatabase = mongoProperties.getProperty("spring.data.mongodb.database");
        log.info("Using url {}, port {} and database {}", mongoHost, mongoPort, mongoDatabase);
        MongoClient mongoClient = new MongoClient(mongoHost, Integer.parseInt(mongoPort));
        return new MongoDbProvider(mongoClient, mongoDatabase);
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

    private static Datastore getDatastore() {
        return getProvider().getDatastore();
    }

    private static MongoDbProvider getProvider() {
        IPersistenceProvider persistenceProvider = WorkflowInit.getRuntime().getPersistenceProvider();
        if (persistenceProvider instanceof MongoDbProvider) {
            return (MongoDbProvider) persistenceProvider;
        }
        throw new IllegalArgumentException("Runtime is not instance of MongoDbProvider");
    }
}