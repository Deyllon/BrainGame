package brain.school.game.converter;

import brain.school.game.model.Alternativa;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Converter
public class AlternativaConverter implements AttributeConverter<List<Alternativa>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Alternativa> alternativas) {
        try {
            return mapper.writeValueAsString(alternativas);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter lista de alternativas para JSON", e);
        }
    }

    @Override
    public List<Alternativa> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<List<Alternativa>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON para lista de alternativas", e);
        }
    }
}