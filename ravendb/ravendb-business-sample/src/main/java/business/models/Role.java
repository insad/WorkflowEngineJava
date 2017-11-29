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
public class Role {
    private UUID id;
    private String name;
}
