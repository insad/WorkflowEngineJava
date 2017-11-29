package business.helpers;

import business.models.StructDivision;
import business.persistence.ApplicationContextProvider;
import business.repository.StructDivisionRepository;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StructDivisionHelper {
    private static final Object LOCK = new Object();
    private static volatile List<StructDivision> structDivisionAll = null;

    public static List<StructDivision> getStructDivisionCache() {
        if (structDivisionAll == null) {
            synchronized (LOCK) {
                if (structDivisionAll == null) {
                    structDivisionAll = getAll();
                }
            }
        }

        return structDivisionAll;
    }

    private static List<StructDivision> getAll() {
        StructDivisionRepository structDivisionRepository = ApplicationContextProvider.getBean(StructDivisionRepository.class);

        Iterable<StructDivision> structDivisions = structDivisionRepository.findAll();
        List<StructDivision> result = new ArrayList<>();
        for (StructDivision structDivision : structDivisions) {
            result.add(structDivision);
        }
        return result;
    }
}