package business.models;

import com.mysema.query.annotations.QueryEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@QueryEntity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Employee {
    private String name;
    private Map<UUID, String> roles = new HashMap<>();
    private UUID structDivisionId;
    private String structDivisionName;
    private UUID id;
    private boolean head;

    public Employee(UUID id, String name, UUID structDivisionId, boolean head) {
        this.id = id;
        this.name = name;
        this.structDivisionId = structDivisionId;
        this.head = head;
    }
}
