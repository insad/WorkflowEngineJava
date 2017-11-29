package wf.sample.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoadTestingStatisticItemModel {
    private String type;
    private int count;
    private double duration;
    private Double minDuration;
    private Double maxDuration;

    public LoadTestingStatisticItemModel(String type) {
        this.type = type;
    }

    public double getAverageDuration() {
        return duration / count;
    }

    public void checkDurationMinMax(double d) {
        if (minDuration == null || d < minDuration) {
            minDuration = d;
        }

        if (maxDuration == null || d > maxDuration) {
            maxDuration = d;
        }
    }
}
