package wf.sample.loadtest;

import business.repository.LoadTestingOperationModelRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import wf.sample.models.LoadTestingOperationModel;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class LoadTestUtil {

    static void addOperation(Date opStart, Date opEnd, String type,
                             LoadTestingOperationModelRepository loadTestingOperationModelRepository) {
        long duration = opEnd.getTime() - opStart.getTime();

        LoadTestingOperationModel loadTestingOperationModel = new LoadTestingOperationModel();
        loadTestingOperationModel.setId(UUID.randomUUID());
        loadTestingOperationModel.setDate(opStart);
        loadTestingOperationModel.setType(type);
        loadTestingOperationModel.setDurationMilliseconds(duration);
        loadTestingOperationModelRepository.save(loadTestingOperationModel);
    }
}
