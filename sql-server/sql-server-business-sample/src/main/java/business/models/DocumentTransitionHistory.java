package business.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "DocumentTransitionHistory")
@Getter
@Setter
@EqualsAndHashCode
public class DocumentTransitionHistory {
    @Id
    @Column(name = "Id")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "DocumentId")
    private Document document;
    @Column(name = "AllowedToEmployeeNames")
    private String allowedToEmployeeNames;
    @Column(name = "DestinationState")
    private String destinationState;
    @Column(name = "InitialState")
    private String initialState;
    @Column(name = "Command")
    private String command;
    @ManyToOne
    @JoinColumn(name = "EmployeeId")
    private Employee employee;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TransitionTime")
    private Date transitionTime;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TransitionTimeForSort", insertable = false, updatable = false)
    private Date transitionTimeForSort;
    @Column(name = "[Order]", insertable = false, updatable = false)
    private BigInteger orderNumber;
}
