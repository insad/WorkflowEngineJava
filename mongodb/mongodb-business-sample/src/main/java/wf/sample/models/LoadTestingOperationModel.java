package wf.sample.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;
import java.util.UUID;

@Entity("LoadTestingOperationModel")
@Getter
@Setter
@EqualsAndHashCode
public class LoadTestingOperationModel {
    @Id
    private UUID id;
    private Date date;
    private String type;
    private double durationMilliseconds;
}