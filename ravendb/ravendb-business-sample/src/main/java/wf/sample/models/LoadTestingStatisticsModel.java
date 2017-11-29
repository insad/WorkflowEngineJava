package wf.sample.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import optimajet.workflow.core.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LoadTestingStatisticsModel {
    private Date date;
    private List<LoadTestingStatisticItemModel> items = new ArrayList<>();

    public LoadTestingStatisticsModel(Date date) {
        this.date = date;
    }

    public static List<LoadTestingStatisticItemModel> getByType(List<LoadTestingStatisticsModel> stats) {
        List<LoadTestingStatisticItemModel> res = new ArrayList<>();

        for (LoadTestingStatisticsModel stat : stats) {
            for (final LoadTestingStatisticItemModel item : stat.items) {
                LoadTestingStatisticItemModel r = CollectionUtil.firstOrDefault(res,
                        new CollectionUtil.ItemCondition<LoadTestingStatisticItemModel>() {
                            @Override
                            public boolean check(LoadTestingStatisticItemModel c) {
                                return c.getType().equals(item.getType());
                            }
                        });
                if (r == null) {
                    r = new LoadTestingStatisticItemModel();
                    r.setType(item.getType());
                    r.setMinDuration(item.getMinDuration());
                    r.setMaxDuration(item.getMaxDuration());
                    res.add(r);
                } else {
                    if (item.getMinDuration() < r.getMinDuration()) {
                        r.setMinDuration(item.getMinDuration());
                    }

                    if (item.getMaxDuration() > r.getMaxDuration()) {
                        r.setMaxDuration(item.getMaxDuration());
                    }
                }

                r.setDuration(r.getDuration() + item.getDuration());
                r.setCount(r.getCount() + item.getCount());
            }
        }

        return res;
    }
}

    