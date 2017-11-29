package wf.sample.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "LoadTestingOperation")
@Getter
@Setter
@EqualsAndHashCode
public class LoadTestingOperationModel implements Serializable {
    @Id
    @Column(name = "Id")
    private UUID id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "Date")
    private Date date;
    @Column(name = "Type")
    private String type;
    @Column(name = "DurationMilliseconds")
    private double durationMilliseconds;
}