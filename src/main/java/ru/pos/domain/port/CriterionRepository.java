package ru.pos.domain.port;

import ru.pos.domain.model.Criterion;
import ru.pos.domain.model.CriterionId;
import java.util.List;
import java.util.Optional;

public interface CriterionRepository {
    void save(Criterion criterion);
    Optional<Criterion> findById(CriterionId id);
    List<Criterion> findAll();
}
