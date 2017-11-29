package workflowapp;

import optimajet.workflow.core.provider.WorkflowDocumentProvider;
import optimajet.workflow.core.runtime.WorkflowRuntime;
import optimajet.workflow.ignite.IgniteProvider;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.IgnitionEx;
import org.apache.ignite.internal.processors.resource.GridSpringResourceContext;
import org.apache.ignite.lang.IgniteBiTuple;

final class ProviderHelper {

    private ProviderHelper() {
    }

    static WorkflowDocumentProvider getProvider() {
        try {
            IgniteBiTuple<IgniteConfiguration, GridSpringResourceContext> biTuple =
                    IgnitionEx.loadConfiguration(WorkflowRuntime.class.getResource("/ignite.xml"));
            Ignite ignite = Ignition.getOrStart(biTuple.getKey());
            return new IgniteProvider(ignite);
        } catch (IgniteCheckedException e) {
            throw new RuntimeException(e);
        }
    }
}
