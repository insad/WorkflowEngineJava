package wf.sample.models;

import business.models.DocumentTransitionHistory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DocumentHistoryModel {
    private List<DocumentTransitionHistory> items;
}
