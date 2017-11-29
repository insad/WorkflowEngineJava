package wf.sample.controllers;

import business.helpers.DocumentHelper;
import business.models.Document;
import business.persistence.ApplicationContextProvider;
import business.persistence.PersistenceHelper;
import business.repository.DocumentRepository;
import business.repository.EmployeeRepository;
import business.repository.WorkflowInboxRepository;
import business.workflow.WorkflowInit;
import optimajet.workflow.core.runtime.CommandParameter;
import optimajet.workflow.core.runtime.WorkflowCommand;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.StringUtil;
import optimajet.workflow.core.util.UUIDUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wf.sample.helpers.CurrentUserSettings;
import wf.sample.inbox.InboxThreadCalculator;
import wf.sample.models.PageResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;

@Controller
@Transactional
public class DocumentController {

    private static final int PAGE_SIZE = 15;

    private final DocumentRepository documentRepository;
    private final WorkflowInboxRepository workflowInboxRepository;
    private final EmployeeRepository employeeRepository;

    public DocumentController(DocumentRepository documentRepository,
                              WorkflowInboxRepository workflowInboxRepository,
                              EmployeeRepository employeeRepository) {
        this.documentRepository = documentRepository;
        this.workflowInboxRepository = workflowInboxRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public String index(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                        Map<String, Object> model) {
        int pageSize = PAGE_SIZE;

        Page<Document> documents = documentRepository.findAll(
                new PageRequest(page, pageSize, Sort.Direction.DESC, "number"));
        model.put("page", page);
        model.put("pageSize", pageSize);
        model.put("docs", documents.getContent());
        model.put("count", documents.getTotalElements());
        model.put("folder", "all");
        return "document/index";
    }

    @GetMapping("Document/Inbox")
    public String inbox(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                        Map<String, Object> model, HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {
        int pageSize = PAGE_SIZE;
        UUID currentUser = CurrentUserSettings.getCurrentUser(httpServletRequest, httpServletResponse);
        PageResponse<Document> pageResponse = DocumentHelper.getInbox(currentUser, page, pageSize);
        model.put("page", page);
        model.put("pageSize", pageSize);
        model.put("docs", pageResponse.getResult());
        model.put("count", pageResponse.getCount());
        model.put("folder", "inbox");
        return "document/index";
    }

    @GetMapping("Document/Outbox")
    public String outbox(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                         Map<String, Object> model, HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse) {
        int pageSize = PAGE_SIZE;
        UUID currentUser = CurrentUserSettings.getCurrentUser(httpServletRequest, httpServletResponse);
        PageResponse<Document> pageResponse = DocumentHelper.getOutbox(currentUser, page, pageSize);
        model.put("page", page);
        model.put("pageSize", pageSize);
        model.put("docs", pageResponse.getResult());
        model.put("count", pageResponse.getCount());
        model.put("folder", "outbox");
        return "document/index";
    }

    @GetMapping("Document/Edit")
    public String edit(Map<String, Object> model, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return edit(null, model, httpServletRequest, httpServletResponse);
    }

    @GetMapping("Document/Edit/{id}")
    public String edit(@PathVariable(value = "id", required = false) UUID id, Map<String, Object> model,
                       HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final Document document;
        if (id != null) {
            createWorkflowIfNotExists(id);
            document = documentRepository.findOne(id);
        } else {
            UUID userId = CurrentUserSettings.getCurrentUser(httpServletRequest, httpServletResponse);
            document = new Document();
            document.setAuthor(employeeRepository.findOne(userId));
            document.setStateName("Draft");
        }

        model.put("model", document);
        return "document/edit";
    }

    @PostMapping("Document/Edit")
    public String editDocument(@RequestParam("button") String button,
                               @Valid Document document, BindingResult bindingResult, Map<String, Object> model,
                               HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (bindingResult.hasErrors()) {
            return formError(document, bindingResult, model);
        }

        Document target;
        try {
            if (document.getId() != null) {
                target = documentRepository.findOne(document.getId());
                if (target == null) {
                    model.put("error", "Row not found!");
                    document.setId(null);
                    return formError(document, bindingResult, model);
                }
            } else {
                target = new Document();
                target.setId(UUID.randomUUID());
                target.setAuthor(document.getAuthor());
                target.setStateName("Draft");
            }

            target.setName(document.getName());
            target.setEmployeeController(document.getEmployeeController());
            target.setComment(document.getComment());
            target.setSum(document.getSum());
            documentRepository.save(target);
        } catch (Exception ex) {
            StringBuilder sb = new StringBuilder("Error saving. " + ex.getMessage());
            if (ex.getCause() != null) {
                sb.append("\n").append(ex.getCause().getMessage());
            }
            model.put("error", sb.toString());
            return formError(document, bindingResult, model);
        }

        if ("SaveAndExit".equals(button)) {
            return "redirect:/";
        }
        if (!"Save".equals(button)) {
            executeCommand(target.getId(), button, document, httpServletRequest, httpServletResponse);
        }
        return "redirect:/Document/Edit/" + target.getId();
    }

    private String formError(@Valid Document document, BindingResult bindingResult, Map<String, Object> model) {
        model.put("model", document);
        model.put("bindingResult", bindingResult);
        return "document/edit";
    }

    @PostMapping("Document/DeleteRows")
    @ResponseBody
    public String deleteRows(@RequestParam("ids") ArrayList<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return "Items not selected";
        }

        try {
            documentRepository.deleteByIdIn(ids);
            workflowInboxRepository.deleteByProcessIdIn(ids);

            PersistenceHelper.deleteProcess(ids);
        } catch (Exception ex) {
            return ex.getMessage();
        }

        return "Rows deleted";
    }

    private void executeCommand(UUID id, final String commandName, Document document,
                                HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String currentUser = UUIDUtil.asString(CurrentUserSettings.getCurrentUser(httpServletRequest, httpServletResponse));

        if ("SetState".equalsIgnoreCase(commandName)) {
            if (StringUtil.isNullOrEmpty(document.getStateNameToSet())) {
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("Comment", document.getComment());
            WorkflowInit.getRuntime().setState(id, currentUser, currentUser, document.getStateNameToSet(), params);
            return;
        }

        if ("Draft".equals(WorkflowInit.getRuntime().getCurrentStateName(id))) {
            WorkflowInit.getRuntime().preExecuteFromInitialActivity(id);
        }

        Collection<WorkflowCommand> commands = WorkflowInit.getRuntime().getAvailableCommands(id, currentUser);

        WorkflowCommand command = CollectionUtil.firstOrDefault(commands, new CollectionUtil.ItemCondition<WorkflowCommand>() {
            @Override
            public boolean check(WorkflowCommand c) {
                return c.getCommandName().equalsIgnoreCase(commandName);
            }
        });

        if (command == null) {
            return;
        }

        CommandParameter commentParameter = null;
        for (CommandParameter parameter : command.getParameters()) {
            if ("Comment".equals(parameter.getParameterName())) {
                if (commentParameter == null) {
                    commentParameter = parameter;
                } else {
                    commentParameter = null;
                    break;
                }
            }
        }

        if (commentParameter != null) {
            commentParameter.setValue(document.getComment() != null ? document.getComment() : StringUtil.EMPTY);
        }

        WorkflowInit.getRuntime().executeCommand(command, currentUser, currentUser);
    }

    private void createWorkflowIfNotExists(UUID id) {
        if (WorkflowInit.getRuntime().isProcessExists(id)) {
            return;
        }

        WorkflowInit.getRuntime().createInstance("SimpleWF", id);
    }

    @PostMapping("Document/RecalcInbox")
    @ResponseBody
    public String recalcInbox() {
        InboxThreadCalculator threadCalculator = ApplicationContextProvider.getBean(InboxThreadCalculator.class);
        threadCalculator.setName("InboxThreadCalculator");
        threadCalculator.start();
        return "Calculating inbox started!!";
    }
}