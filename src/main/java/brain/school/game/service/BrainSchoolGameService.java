package brain.school.game.service;

import brain.school.game.dto.TestYearDto;
import brain.school.game.model.Alternativa;
import brain.school.game.model.TestYear;
import brain.school.game.repository.TestYearRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.StreamSupport;

@Service
public class BrainSchoolGameService {

    @Autowired
    private TestYearRepository testYear;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClassPathResource resource = new ClassPathResource("static");

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

    public TestYear getOne(String disciplina) {
        List<TestYear> questions = testYear.findByDisciplina(disciplina);

        int size = questions.size();
        Random random = new Random();
        int randomIndex = random.nextInt(0, size);

        return  questions.get(randomIndex);
    }

    public TestYear getByYearAndDiscipline(String year, String disciplina) {
        List<TestYear> questions = testYear.findByDisciplinaAndAno(disciplina, Integer.parseInt(year));

        int size = questions.size();
        Random random = new Random();
        int randomIndex = random.nextInt(0, size);

        return  questions.get(randomIndex);
    }

    public TestYear getYear(String ano) {
        List<TestYear> questions = testYear.findByAno(Integer.parseInt( ano));

        int size = questions.size();
        Random random = new Random();
        int randomIndex = random.nextInt(0, size);

        return  questions.get(randomIndex);
    }

    @PostConstruct
    private List<TestYear> populate() {
        List<TestYear> savedTests = new ArrayList<>();

        try {
            File folder = resource.getFile();

            if (!folder.exists()) {
                throw new RuntimeException("Folder does not exist: " + folder.getPath());
            }
            if (!folder.isDirectory()) {
                throw new RuntimeException("The specified resource is not a folder: " + folder.getPath());
            }

            for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (!fileEntry.isDirectory()) {
                    throw new RuntimeException("The specified resource is not a folder:");
                }
                File questionsFolder = new File(fileEntry, "questions");

                if (!questionsFolder.exists()) {
                    throw new RuntimeException("questionsFolder does not exist: " + questionsFolder.getPath());
                }
                if (!questionsFolder.isDirectory()) {
                    throw new RuntimeException("The specified resource is not a questionsFolder: " + questionsFolder.getPath());
                }
                for (File questionFolder : Objects.requireNonNull(questionsFolder.listFiles())) {
                    for (File jsonFile : Objects.requireNonNull(questionFolder.listFiles((dir, name) -> name.endsWith(".json")))) {
                        try (FileInputStream fileInputStream = new FileInputStream(jsonFile)) {
                            JsonNode rootNode = objectMapper.readTree(fileInputStream);

                            List<Alternativa> alternativas = StreamSupport.stream(rootNode.get("alternatives").spliterator(), false)
                                    .map(a -> Alternativa.builder()
                                            .letra(a.get("letter").asText())
                                            .texto(a.get("text").asText())
                                            .build())
                                    .toList();

                            TestYear test = TestYear.builder()
                                    .ano(rootNode.get("year").asInt())
                                    .disciplina(rootNode.get("discipline").asText())
                                    .alternativaCorreta(rootNode.get("correctAlternative").asText())
                                    .alternativa(alternativas)
                                    .contexto(rootNode.get("context").asText())
                                    .introducao(rootNode.get("alternativesIntroduction").asText())
                                    .build();

                            // Save and collect the TestYear object
                            savedTests.add(testYear.save(test));
                        } catch (IOException e) {
                            throw new RuntimeException("Error accessing resource folder", e);
                        }
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Error accessing resource folder", e);
        }

        return savedTests;
    }

}
