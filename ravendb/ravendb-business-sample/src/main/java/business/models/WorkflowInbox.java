package business.models;

import com.mysema.query.annotations.QueryEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@QueryEntity
@Getter
@Setter
public class WorkflowInbox {
    private UUID id;
    private UUID processId;
    private String identityId;
}
