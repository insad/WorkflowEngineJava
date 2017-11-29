package business.models;

import lombok.*;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.UUID;

@Entity("StructDivision")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StructDivision {
    @Id
    private UUID id;
    private String name;
    private UUID parentId;

    public StructDivision(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
