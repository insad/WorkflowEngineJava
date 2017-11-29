package business.models;

import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

@Entity("WorkflowInbox")
@Getter
@Setter
public class WorkflowInbox {
    @Id
    private UUID id;
    private UUID processId;
    private String identityId;
}
