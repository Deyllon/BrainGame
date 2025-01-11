package brain.school.game.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RankedUserDto {
    private Long id;
    private String nome;
    private int points;
    private int rank;


}