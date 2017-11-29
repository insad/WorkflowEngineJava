package business.models;

import com.mysema.query.annotations.QueryEntity;
import lombok.*;

import java.util.UUID;

@QueryEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StructDivision {
    private UUID id;
    private String name;
    private UUID parentId;

    public StructDivision(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
