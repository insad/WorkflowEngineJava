package business.helpers;

import business.models.Document;
import business.models.DocumentCommandModel;
import business.persistence.ApplicationContextProvider;
import business.repository.DocumentRepository;
import business.repository.DocumentTransitionHistoryRepository;
import business.repository.WorkflowInboxRepository;
import business.workflow.WorkflowInit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import optimajet.workflow.core.runtime.WorkflowCommand;
import optimajet.workflow.core.runtime.WorkflowState;
import optimajet.workflow.core.util.StringUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import wf.sample.models.PageResponse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentHelper {

    public static PageResponse<Document> get() {
        return get(0, 128);
    }

    public static PageResponse<Document> get(int page, int pageSize) {
        DocumentRepository documentRepository = getDocumentRepository();
        Page<Document> documents = documentRepository.findAll(new PageRequest(page, pageSize));
        return new PageResponse<>(documents.getTotalElements(), documents.getContent());
    }

    public static PageResponse<Document> getInbox(UUID identityId, int page, int pageSize) {
        WorkflowInboxRepository workflowInboxRepository = ApplicationContextProvider.getBean(WorkflowInboxRepository.class);
        Page<Document> documents = workflowInboxRepository.getInbox(identityId, new PageRequest(page, pageSize));
        return new PageResponse<>(documents.getTotalElements(), documents.getContent());
    }

    public static PageResponse<Document> getOutbox(UUID identityId, int page, int pageSize) {
        DocumentTransitionHistoryRepository documentTransitionHistoryRepository =
                ApplicationContextProvider.getBean(DocumentTransitionHistoryRepository.class);
        Page<Document> documents = documentTransitionHistoryRepository.findByEmployeeId(identityId, new PageRequest(page, pageSize));
        return new PageResponse<>(documents.getTotalElements(), documents.getContent());
    }

    public static Document get(UUID id) {
        DocumentRepository documentRepository = getDocumentRepository();
        return documentRepository.findOne(id);
    }

    public static DocumentCommandModel[] getCommands(UUID id, String userId) {
        Map<String, DocumentCommandModel> map = new HashMap<>();
        Collection<WorkflowCommand> commands = WorkflowInit.getRuntime().getAvailableCommands(id, userId);
        for (WorkflowCommand workflowCommand : commands) {
            if (!map.containsKey(workflowCommand.getCommandName())) {
                DocumentCommandModel documentCommandModel = new DocumentCommandModel();
                documentCommandModel.setKey(workflowCommand.getCommandName());
                documentCommandModel.setValue(workflowCommand.getLocalizedName());
                documentCommandModel.setClassifier(workflowCommand.getClassifier());
                map.put(workflowCommand.getCommandName(), documentCommandModel);
            }
        }
        return map.values().toArray(new DocumentCommandModel[]{});
    }

    public static Map<String, String> getStates(UUID id) {
        Map<String, String> result = new HashMap<>();
        Collection<WorkflowState> states = WorkflowInit.getRuntime().getAvailableStateToSet(id);
        for (WorkflowState state : states) {
            if (!StringUtil.isNullOrWhiteSpace(state.getName()) && !result.containsKey(state.getName())) {
                result.put(state.getName(), state.getVisibleName());
            }
        }
        return result;
    }

    private static DocumentRepository getDocumentRepository() {
        return ApplicationContextProvider.getBean(DocumentRepository.class);
    }
}