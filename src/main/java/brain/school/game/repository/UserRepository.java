package brain.school.game.repository;


import brain.school.game.dto.RankedUserDto;
import brain.school.game.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query("""
        SELECT RANK() OVER (ORDER BY u.points DESC) 
        FROM User u 
        WHERE u.id = :userId
    """)
    Integer findUserRank(Long userId);

    @Query(value = """ 
                    SELECT u.*  FROM  user_table u ORDER BY u.points DESC LIMIT 100;
                    """, nativeQuery = true
    )
    List<User> findTop100ByOrderByPointsDesc();

    @Query(value = """ 
                    SELECT u.*  FROM  user_table u WHERE estado = :estado ORDER BY u.points DESC LIMIT 100;
                    """, nativeQuery = true
    )
    List<User> findStateTop100ByOrderByPointsDesc(@Param("estado") String estado);

    @Query(value = """ 
                    SELECT u.*  FROM  user_table u WHERE municipio = :municipio ORDER BY u.points DESC LIMIT 100;
                    """, nativeQuery = true
    )
    List<User> findCityTop100ByOrderByPointsDesc(@Param("municipio") String municipio);

    @Query(value = """
        WITH ranked_users AS (
                    SELECT  u.id, u.nome, u.points, RANK() OVER (ORDER BY u.points DESC) AS rank
                    FROM user_table u
                )
                SELECT *
                FROM ranked_users
                WHERE rank BETWEEN :minRank AND :maxRank;
            
    """, nativeQuery = true)
    List<RankedUserDto> findUsersInRankRange(@Param("minRank") int minRank, @Param("maxRank") int maxRank);
}
