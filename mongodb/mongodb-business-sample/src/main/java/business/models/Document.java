package business.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity("Document")
@Getter
@Setter
@EqualsAndHashCode
public class Document {
    @Id
    private UUID id;
    private Long number;
    @NotNull
    @Size(min = 1, max = 256)
    private String name;
    private String comment;
    private UUID authorId;
    private String authorName;
    private UUID employeeControllerId;
    private String employeeControllerName;
    // note в старой версии монги нет сериализации BigDecimal
    @ValidSum
    private String sum = BigDecimal.ZERO.toPlainString();
    private String stateName;
    private String stateNameToSet;
    @Embedded
    private List<DocumentTransitionHistory> transitionHistories = new ArrayList<>();

    public List<DocumentTransitionHistory> getTransitionHistories() {
        if (transitionHistories == null) {
            transitionHistories = new ArrayList<>();
        }
        return transitionHistories;
    }

    @SuppressWarnings("unused")
    public BigDecimal getAmount() {
        return sum == null ? null : new BigDecimal(sum);
    }
}