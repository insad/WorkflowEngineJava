package business.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "WorkflowInbox")
@Getter
@Setter
@EqualsAndHashCode
public class WorkflowInbox implements Serializable {
    @Id
    @Column(name = "Id")
    private UUID id;
    @Column(name = "ProcessId")
    private UUID processId;
    @ManyToOne
    @JoinColumn(name = "IdentityId")
    private Employee employee;
    @ManyToOne
    @JoinColumn(name = "ProcessId", insertable = false, updatable = false)
    private Document document;
}
