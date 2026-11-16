package ru.pos.infrastructure.csv;

import ru.pos.domain.model.*;
import ru.pos.domain.port.CriterionRepository;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;

public final class CsvCriterionRepository implements CriterionRepository {
    private static final String HEADER = "id,name,max_score,weight";
    private final Path file;
    public CsvCriterionRepository(Path directory) { file = directory.resolve("criteria.csv"); }
    public synchronized void save(Criterion criterion) {
        Map<CriterionId, Criterion> all = new LinkedHashMap<>(); findAll().forEach(c -> all.put(c.id(), c)); all.put(criterion.id(), criterion);
        List<String> rows = all.values().stream().map(c -> Csv.row(c.id().toString(), c.name(), c.maxScore().toPlainString(), c.weight().toString())).toList(); CsvFile.write(file, HEADER, rows);
    }
    public synchronized Optional<Criterion> findById(CriterionId id) { return findAll().stream().filter(c -> c.id().equals(id)).findFirst(); }
    public synchronized List<Criterion> findAll() {
        return CsvFile.read(file, HEADER).stream().map(r -> new Criterion(CriterionId.of(r.get(0)), r.get(1), new BigDecimal(r.get(2)), Weight.of(r.get(3)))).toList();
    }
}
