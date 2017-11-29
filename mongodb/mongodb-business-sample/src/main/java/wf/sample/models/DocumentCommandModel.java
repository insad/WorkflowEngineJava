package wf.sample.models;

import lombok.Getter;
import lombok.Setter;
import optimajet.workflow.core.model.TransitionClassifier;

@Getter
@Setter
public class DocumentCommandModel {
    private String key;
    private String value;
    private TransitionClassifier classifier;
}
