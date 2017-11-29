package business.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Document")
@Getter
@Setter
@EqualsAndHashCode
public class Document {
    @Id
    @Column(name = "Id")
    private UUID id;
    @Column(name = "Number", insertable = false, updatable = false)
    private Long number;
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "Name")
    private String name;
    @Column(name = "Comment")
    private String comment;
    @ManyToOne
    @JoinColumn(name = "AuthorId")
    private Employee author;
    @ManyToOne
    @JoinColumn(name = "EmloyeeControlerId")
    private Employee employeeController;
    @NotNull
    @ValidSum
    @Column(name = "Sum")
    private BigDecimal sum = BigDecimal.ZERO;
    @Column(name = "State")
    private String stateName;
    @Column(name = "StateName")
    private String stateNameToSet;
    @OneToMany(mappedBy = "document")
    @OrderBy("transitionTimeForSort, orderNumber")
    private List<DocumentTransitionHistory> transitionHistories;

    @SuppressWarnings("unused")
    public UUID getEmployeeControllerId() {
        // note used from script
        return employeeController != null ? employeeController.getId() : null;
    }

    @SuppressWarnings("unused")
    public UUID getAuthorId() {
        // note used from script
        return author.getId();
    }

    @SuppressWarnings("unused")
    public BigDecimal getAmount() {
        // note used from script
        return sum;
    }
}