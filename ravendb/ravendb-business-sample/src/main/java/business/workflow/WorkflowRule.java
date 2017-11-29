package business.workflow;

import business.helpers.EmployeeHelper;
import business.helpers.StructDivisionHelper;
import business.models.Document;
import business.models.Employee;
import business.models.StructDivision;
import business.persistence.PersistenceHelper;
import optimajet.workflow.core.model.ProcessInstance;
import optimajet.workflow.core.runtime.IWorkflowRuleProvider;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import optimajet.workflow.core.util.CollectionUtil;
import optimajet.workflow.core.util.UUIDUtil;

import java.util.*;

public class WorkflowRule implements IWorkflowRuleProvider {
    private final Map<String, RuleFunction> functions;

    WorkflowRule() {
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

    private static boolean isDocumentAuthor(ProcessInstance processInstance, String identityId) {
        Document document = PersistenceHelper.get(Document.class, processInstance.getProcessId());
        return document != null && document.getAuthorId().equals(UUIDUtil.fromString(identityId));
    }

    private static Collection<String> getDocumentAuthor(ProcessInstance processInstance) {
        Document document = PersistenceHelper.get(Document.class, processInstance.getProcessId());
        if (document == null) {
            return new ArrayList<>();
        }
        return Collections.singletonList(UUIDUtil.asString(document.getAuthorId()));
    }

    private static boolean isAuthorsBoss(ProcessInstance processInstance, String identityId) {
        return getAuthorsBoss(processInstance).contains(identityId);
    }

    private static Collection<String> getAuthorsBoss(ProcessInstance processInstance) {
        Set<String> res = new HashSet<>();
        final Document document = PersistenceHelper.get(Document.class, processInstance.getProcessId());
        if (document == null) {
            return res;
        }

        List<StructDivision> sds = StructDivisionHelper.getStructDivisionCache();
        List<Employee> emps = EmployeeHelper.getEmployeeCache();

        final Employee author = CollectionUtil.firstOrDefault(emps,
                new CollectionUtil.ItemCondition<Employee>() {
                    @Override
                    public boolean check(Employee c) {
                        return c.getId().equals(document.getAuthorId());
                    }
                });
        if (author == null) {
            return res;
        }

        StructDivision currentSD = CollectionUtil.firstOrDefault(sds, new CollectionUtil.ItemCondition<StructDivision>() {
            @Override
            public boolean check(StructDivision c) {
                return c.getId().equals(author.getStructDivisionId());
            }
        });

        while (currentSD != null) {
            final StructDivision csd = currentSD;
            Collection<String> headEmpIds = CollectionUtil.select(emps, new CollectionUtil.ItemCondition<Employee>() {
                @Override
                public boolean check(Employee c) {
                    return c.isHead() && c.getStructDivisionId().equals(csd.getId());
                }
            }, new CollectionUtil.ItemTransformer<Employee, String>() {
                @Override
                public String transform(Employee c) {
                    return UUIDUtil.asString(c.getId());
                }
            });
            res.addAll(headEmpIds);

            if (currentSD.getParentId() != null) {
                currentSD = CollectionUtil.firstOrDefault(sds, new CollectionUtil.ItemCondition<StructDivision>() {
                    @Override
                    public boolean check(StructDivision c) {
                        return c.getId().equals(csd.getParentId());
                    }
                });
            } else {
                currentSD = null;
            }
        }

        return res;
    }

    private static Collection<String> getInRole(final String parameter) {
        return CollectionUtil.select(EmployeeHelper.getEmployeeCache(),
                new CollectionUtil.ItemCondition<Employee>() {
                    @Override
                    public boolean check(Employee c) {
                        return c.getRoles().values().contains(parameter);
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
        return emp.getRoles().values().contains(parameter);
    }

    private static Collection<String> getDocumentController(ProcessInstance processInstance) {
        Document document = PersistenceHelper.get(Document.class, processInstance.getProcessId());
        if (document == null || document.getEmployeeControllerId() == null) {
            return new ArrayList<>();
        }

        return Collections.singletonList(UUIDUtil.asString(document.getEmployeeControllerId()));
    }

    private static boolean isDocumentController(ProcessInstance processInstance, String identityId) {
        Document document = PersistenceHelper.get(Document.class, processInstance.getProcessId());
        return document != null && document.getEmployeeControllerId() != null && document.getEmployeeControllerId().equals(UUIDUtil.fromString(identityId));
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