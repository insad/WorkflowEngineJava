package business.helpers;

import business.models.Document;
import business.models.DocumentCommandModel;
import business.persistence.PersistenceHelper;
import business.workflow.WorkflowInit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import optimajet.workflow.core.runtime.WorkflowCommand;
import optimajet.workflow.core.runtime.WorkflowState;
import optimajet.workflow.core.util.StringUtil;
import wf.sample.models.PageResponse;

import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentHelper {

    public static PageResponse<Document> get() {
        return get(0, 128);
    }

    public static PageResponse<Document> get(int page, int pageSize) {
        return PersistenceHelper.getPageResponse(page, pageSize, Document.class);
    }

    public static PageResponse<Document> getInbox(UUID identityId, int page, int pageSize) {
        return PersistenceHelper.getInbox(identityId, page, pageSize);
    }

    public static PageResponse<Document> getOutbox(UUID identityId, int page, int pageSize) {
        return PersistenceHelper.getOutbox(identityId, page, pageSize);
    }

    public static Document get(UUID id) {
        return PersistenceHelper.get(Document.class, id);
    }

    public static void delete(List<UUID> uuidList) {
        PersistenceHelper.deleteDocuments(uuidList);
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

    public static long getNextNumber() {
        return PersistenceHelper.getNextDocumentNumber();
    }
}