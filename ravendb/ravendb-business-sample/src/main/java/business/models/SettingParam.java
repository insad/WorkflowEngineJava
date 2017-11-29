package business.models;

import com.mysema.query.annotations.QueryEntity;
import lombok.*;

@QueryEntity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SettingParam {
    private String id;
    private String value;
}
