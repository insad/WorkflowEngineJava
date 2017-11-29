package wf.sample.loadtest;

import business.models.Document;
import business.models.Employee;
import business.repository.DocumentRepository;
import business.repository.LoadTestingOperationModelRepository;
import business.workflow.WorkflowInit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class DocumentCreator {
    private static final AtomicInteger DOCUMENT_NUMBER = new AtomicInteger(0);

    private final LoadTestingOperationModelRepository loadTestingOperationModelRepository;
    private final DocumentRepository documentRepository;

    public DocumentCreator(LoadTestingOperationModelRepository loadTestingOperationModelRepository,
                           DocumentRepository documentRepository) {
        this.loadTestingOperationModelRepository = loadTestingOperationModelRepository;
        this.documentRepository = documentRepository;
    }

    void createDocumentAndWorkflow(List<Employee> employees, Random r) {
        UUID id = createDocument(employees, r);
        documentCreateWorkflow(id);
    }

    private void documentCreateWorkflow(UUID id) {
        Date opStart = new Date();
        WorkflowInit.getRuntime().createInstance("SimpleWF", id);
        LoadTestUtil.addOperation(opStart, new Date(), "CreatingWorkflow", loadTestingOperationModelRepository);
    }

    private UUID createDocument(List<Employee> employees, Random r) {
        UUID id = UUID.randomUUID();

        Date opStart = new Date();

        Employee author = employees.get(r.nextInt(employees.size()));
        Employee controller = employees.get(r.nextInt(employees.size()));

        Document doc = new Document();
        doc.setId(id);
        doc.setAuthor(author);
        doc.setStateName("Draft");
        doc.setName("AG_Doc " + DOCUMENT_NUMBER.incrementAndGet());

        if (r.nextBoolean()) {
            doc.setEmployeeController(controller);
        }
        String shortTime = new SimpleDateFormat("HH:mm").format(new Date());
        doc.setComment(String.format("Auto-generated document. %s.", shortTime));
        doc.setSum(new BigDecimal(r.nextInt(10000) + 1));

        documentRepository.save(doc);
        LoadTestUtil.addOperation(opStart, new Date(), "CreatingDocument", loadTestingOperationModelRepository);

        return id;
    }
}
