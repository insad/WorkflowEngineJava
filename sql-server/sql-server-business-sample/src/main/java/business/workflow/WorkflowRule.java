package business.workflow;

import business.helpers.EmployeeHelper;
import business.helpers.StructDivisionHelper;
import business.models.Document;
import business.models.Employee;
import business.models.StructDivision;
import business.repository.DocumentRepository;
import optimajet.workflow.core.model.ProcessInstance;
import optimajet.workflow.core.runtime.IWorkflowRuleProvider;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.UUIDUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WorkflowRule implements IWorkflowRuleProvider {
    private final Map<String, RuleFunction> functions;
    private final DocumentRepository documentRepository;

    WorkflowRule(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;

        Map<String, RuleFunction> map = new HashMap<>();
        map.put("IsDocumentAuthor", new RuleFunction() {
            @Override
            public boolean checkFunction(ProcessInstance processInstance, String identityId, String parameter) {
                return isDocumentAuthor(processInstance, identityId);
            }

            @Override
            public Collection<String> getFunction(ProcessInstance processInstance, String parameter) {
                return getDocumentAuthor(processInstance);
            }
        });

        map.put("IsAuthorsBoss", new RuleFunction() {
            @Override
            public boolean checkFunction(ProcessInstance processInstance, String identityId, String parameter) {
                return isAuthorsBoss(processInstance, identityId);
            }

            @Override
            public Collection<String> getFunction(ProcessInstance processInstance, String parameter) {
                return getAuthorsBoss(processInstance);
            }
        });

        map.put("IsDocumentController", new RuleFunction() {
            @Override
            public boolean checkFunction(ProcessInstance processInstance, String identityId, String parameter) {
                return isDocumentController(processInstance, identityId);
            }

            @Override
            public Collection<String> getFunction(ProcessInstance processInstance, String parameter) {
                return getDocumentController(processInstance);
            }
        });

        map.put("CheckRole", new RuleFunction() {
            @Override
            public boolean checkFunction(ProcessInstance processInstance, String identityId, String parameter) {
                return checkRole(identityId, parameter);
            }

            @Override
            public Collection<String> getFunction(ProcessInstance processInstance, String parameter) {
                return getInRole(parameter);
            }
        });
        this.functions = Collections.unmodifiableMap(map);
    }

    private static Collection<String> getInRole(final String parameter) {
        return CollectionUtil.select(EmployeeHelper.getEmployeeCache(),
                new CollectionUtil.ItemCondition<Employee>() {
                    @Override
                    public boolean check(Employee c) {
                        return c.inRole(parameter);
                    }
                }, new CollectionUtil.ItemTransformer<Employee, String>() {
                    @Override
                    public String transform(Employee c) {
                        return UUIDUtil.asString(c.getId());
                    }
                });
    }

    private static boolean checkRole(final String identityId, String parameter) {
        Employee emp = CollectionUtil.firstOrDefault(EmployeeHelper.getEmployeeCache(),
                new CollectionUtil.ItemCondition<Employee>() {
                    @Override
                    public boolean check(Employee c) {
                        return c.getId().equals(UUIDUtil.fromString(identityId));
                    }
                });
        return emp.inRole(parameter);
    }

    private boolean isDocumentAuthor(ProcessInstance processInstance, String identityId) {
        Document document = documentRepository.findOne(processInstance.getProcessId());
        return document != null && document.getAuthor().getId().equals(UUIDUtil.fromString(identityId));
    }

    private Collection<String> getDocumentAuthor(ProcessInstance processInstance) {
        Document document = documentRepository.findOne(processInstance.getProcessId());
        if (document == null) {
            return new ArrayList<>();
        }
        return Collections.singletonList(UUIDUtil.asString(document.getAuthor().getId()));
    }

    private boolean isAuthorsBoss(ProcessInstance processInstance, String identityId) {
        return getAuthorsBoss(processInstance).contains(identityId);
    }

    private Collection<String> getAuthorsBoss(ProcessInstance processInstance) {
        Set<String> res = new HashSet<>();
        final Document document = documentRepository.findOne(processInstance.getProcessId());
        if (document == null) {
            return res;
        }

        List<StructDivision> sds = StructDivisionHelper.getStructDivisionCache();
        List<Employee> employeeList = EmployeeHelper.getEmployeeCache();

        final Employee author = CollectionUtil.firstOrDefault(employeeList,
                new CollectionUtil.ItemCondition<Employee>() {
                    @Override
                    public boolean check(Employee c) {
                        return c.getId().equals(document.getAuthor().getId());
                    }
                });
        if (author == null) {
            return res;
        }

        StructDivision currentSD = CollectionUtil.firstOrDefault(sds, new CollectionUtil.ItemCondition<StructDivision>() {
            @Override
            public boolean check(StructDivision c) {
                return c.getId().equals(author.getStructDivision().getId());
            }
        });

        while (currentSD != null) {
            final StructDivision csd = currentSD;
            Collection<String> headEmpIds = CollectionUtil.select(employeeList, new CollectionUtil.ItemCondition<Employee>() {
                @Override
                public boolean check(Employee c) {
                    return c.isHead() && c.getStructDivision().getId().equals(csd.getId());
                }
            }, new CollectionUtil.ItemTransformer<Employee, String>() {
                @Override
                public String transform(Employee c) {
                    return UUIDUtil.asString(c.getId());
                }
            });
            res.addAll(headEmpIds);

            if (currentSD.getParent() != null) {
                currentSD = CollectionUtil.firstOrDefault(sds, new CollectionUtil.ItemCondition<StructDivision>() {
                    @Override
                    public boolean check(StructDivision c) {
                        return c.getId().equals(csd.getParent().getId());
                    }
                });
            } else {
                currentSD = null;
            }
        }

        return res;
    }

    private Collection<String> getDocumentController(ProcessInstance processInstance) {
        Document document = documentRepository.findOne(processInstance.getProcessId());
        if (document == null || document.getEmployeeController() == null) {
            return new ArrayList<>();
        }

        return Collections.singletonList(UUIDUtil.asString(document.getEmployeeController().getId()));
    }

    private boolean isDocumentController(ProcessInstance processInstance, String identityId) {
        Document document = documentRepository.findOne(processInstance.getProcessId());
        return document != null && document.getEmployeeController() != null && document.getEmployeeController().getId().equals(UUIDUtil.fromString(identityId));
    }

    @Override
    public List<String> getRules() {
        return new ArrayList<>(functions.keySet());
    }

    @Override
    public boolean check(ProcessInstance processInstance, WorkflowRuntime runtime, String identityId, String ruleName, String parameter) {
        RuleFunction ruleFunction = functions.get(ruleName);
        return ruleFunction != null && ruleFunction.checkFunction(processInstance, identityId, parameter);
    }

    @Override
    public Collection<String> getIdentities(ProcessInstance processInstance, WorkflowRuntime runtime, String ruleName, String parameter) {
        RuleFunction ruleFunction = functions.get(ruleName);
        if (ruleFunction == null) {
            return new ArrayList<>();
        }
        return ruleFunction.getFunction(processInstance, parameter);
    }

    private interface RuleFunction {
        boolean checkFunction(ProcessInstance processInstance, String identityId, String parameter);

        Collection<String> getFunction(ProcessInstance processInstance, String parameter);
    }
}