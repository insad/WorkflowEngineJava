package business.models;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "StructDivision")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StructDivision implements Serializable{
    @Id
    @Column(name = "Id")
    private UUID id;
    @Column(name = "Name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "ParentId")
    private StructDivision parent;

    public StructDivision(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
