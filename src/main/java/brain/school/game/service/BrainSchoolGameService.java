package brain.school.game.service;

import brain.school.game.dto.TestYearDto;
import brain.school.game.model.Alternativa;
import brain.school.game.model.TestYear;
import brain.school.game.repository.TestYearRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrainSchoolGameService {

    @Autowired
    private TestYearRepository testYear;

    public TestYear create(TestYearDto createTestYear){
        List<Alternativa> alternativas = createTestYear.alternativa.stream()
                .map(a -> Alternativa.builder()
                        .letra(a.letra)
                        .texto(a.texto)
                        .build())
                .toList();

        TestYear test = TestYear.builder()
                .ano(createTestYear.ano)
                .disciplina(createTestYear.disciplina)
                .alternativaCorreta(createTestYear.alternativaCorreta)
                .alternativa(alternativas)
                .contexto(createTestYear.contexto)
                .introducao(createTestYear.introducao)
                .build();

        return testYear.save(test);

    }

    public List<TestYear> get(){
        return  testYear.findAll();
    }



}
