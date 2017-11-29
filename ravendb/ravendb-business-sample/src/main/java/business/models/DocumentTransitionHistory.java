package business.models;

import com.mysema.query.annotations.QueryEmbeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@QueryEmbeddable
@Getter
@Setter
@EqualsAndHashCode
public class DocumentTransitionHistory {
    private UUID id;
    private String allowedToEmployeeNames;
    private String destinationState;
    private String initialState;
    private String command;
    private UUID employeeId;
    private String employeeName;
    private Date transitionTime;
}
