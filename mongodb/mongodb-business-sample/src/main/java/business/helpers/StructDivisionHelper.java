package business.helpers;

import business.models.StructDivision;
import business.persistence.PersistenceHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
        return PersistenceHelper.asList(StructDivision.class);
    }
}