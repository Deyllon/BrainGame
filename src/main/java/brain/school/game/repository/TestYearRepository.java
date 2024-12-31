package brain.school.game.repository;

import brain.school.game.model.TestYear;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestYearRepository extends JpaRepository<TestYear, Long> {
    TestYear findByDisciplina(String disciplina);
}
