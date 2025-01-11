package brain.school.game.service;


import brain.school.game.dto.RankedUserDto;
import brain.school.game.model.User;
import brain.school.game.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private List<RankedUserDto> foundUserAndAroundUser(Long userId, int range, String municipio, String estado) {
        Integer userRank = userRepository.findUserRank(userId);

        if (userRank == null) {
            throw new IllegalArgumentException("Usuário não encontrado com o ID: " + userId);
        }

        int minRank = Math.max(userRank - range, 1);
        int maxRank = userRank + range;

        // Definindo a parte comum do SQL
        String sql = """
        WITH ranked_users AS (
            SELECT u.id, u.nome, u.points, u.municipio, u.estado, RANK() OVER (ORDER BY u.points DESC) AS rank
            FROM user_table u
        )
        SELECT id, nome, points, rank
        FROM ranked_users
        WHERE rank BETWEEN :minRank AND :maxRank
    """;

        // Adicionando filtros dinamicamente
        if (municipio != null && !municipio.isBlank()) {
            sql += " AND municipio = :municipio";
        }
        if (estado != null && !estado.isBlank()) {
            sql += " AND estado = :estado";
        }

        // Criando a query e definindo os parâmetros
        Query query = entityManager.createNativeQuery(sql)
                .setParameter("minRank", minRank)
                .setParameter("maxRank", maxRank);

        // Adicionando os parâmetros extras de filtro
        if (municipio != null && !municipio.isBlank()) {
            query.setParameter("municipio", municipio);
        }
        if (estado != null && !estado.isBlank()) {
            query.setParameter("estado", estado);
        }

        // Executando a consulta e recuperando os resultados
        List<Object[]> result = query.getResultList();

        // Mapeando o resultado para a lista de RankedUserDto
        List<RankedUserDto> rank = result.stream()
                .map(row -> new RankedUserDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).intValue(),
                        ((Number) row[3]).intValue()
                ))
                .collect(Collectors.toList());

        return rank;
    }




    public List<RankedUserDto> ranking(Long userId, String municipio , String estado){
        List<User> top100 = null;
        List<RankedUserDto> userAndAroundUser;

        if (municipio != null && !municipio.isBlank()) {
            userAndAroundUser = foundUserAndAroundUser(userId, 5, municipio, null);
            top100 = userRepository.findCityTop100ByOrderByPointsDesc(municipio);
        } else if (estado != null && !estado.isBlank()) {
            userAndAroundUser = foundUserAndAroundUser(userId, 5, null, estado);
            top100 = userRepository.findStateTop100ByOrderByPointsDesc(estado);
        } else {
            userAndAroundUser = foundUserAndAroundUser(userId, 5, null, null);
            top100 = userRepository.findTop100ByOrderByPointsDesc();
        }

        List<RankedUserDto> rankedTop100 = top100.stream()
                .map(user -> new RankedUserDto(user.getId(), user.getNome(), user.getPoints(), 0)) // rank será calculado depois
                .collect(Collectors.toList());

        List<RankedUserDto> result = new ArrayList<>(rankedTop100);
        result.addAll(userAndAroundUser);

        Map<Long, RankedUserDto> uniqueUsersMap = new HashMap<>();
        for (RankedUserDto userDto : result) {
            uniqueUsersMap.put(userDto.getId(), userDto);  // Vai sobrescrever se houver duplicação de ID
        }

        List<RankedUserDto> uniqueResult = new ArrayList<>(uniqueUsersMap.values());

        // Ordena a lista pela pontuação em ordem decrescente
        uniqueResult.sort(Comparator.comparingInt(RankedUserDto::getPoints).reversed());

        // Atribui o rank baseado na ordem (1 é o maior ponto)
        for (int i = 0; i < uniqueResult.size(); i++) {
            RankedUserDto userDto = uniqueResult.get(i);
            userDto.setRank(i + 1); // O primeiro da lista recebe rank 1
        }

        return uniqueResult;
    }
}
