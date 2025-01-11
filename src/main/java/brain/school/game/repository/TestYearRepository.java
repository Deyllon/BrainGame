package brain.school.game.repository;

import brain.school.game.model.TestYear;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestYearRepository extends JpaRepository<TestYear, Long> {
    List<TestYear> findByDisciplina(String disciplina);
    List<TestYear> findByAno(int ano);
    List<TestYear> findByDisciplinaAndAno(String disciplina, int ano);
}
