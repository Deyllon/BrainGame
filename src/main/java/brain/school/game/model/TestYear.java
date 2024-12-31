package brain.school.game.model;

import brain.school.game.converter.AlternativaConverter;
import jakarta.persistence.*;
import lombok.*;


import java.io.Serializable;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TestYear")
@EqualsAndHashCode(of = "id")
public class TestYear implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false)
    private String disciplina;

    @Column(nullable = false)
    private String alternativaCorreta;

    @Column(length = 3000)
    private String contexto;

    @Column(length = 3000)
    private String introducao;

    @Convert(converter = AlternativaConverter.class)
    @Column(name = "alternativa", length = 3000)
    private List<Alternativa> alternativa;
}
