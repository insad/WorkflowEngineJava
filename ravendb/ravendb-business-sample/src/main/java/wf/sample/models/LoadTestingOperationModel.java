package wf.sample.models;

import com.mysema.query.annotations.QueryEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@QueryEntity
@Getter
@Setter
@EqualsAndHashCode
public class LoadTestingOperationModel {
    private UUID id;
    private Date date;
    private String type;
    private double durationMilliseconds;
}