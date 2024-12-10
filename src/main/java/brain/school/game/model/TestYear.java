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

    @Column
    private String contexto;

    @Column
    private String introducao;

    @Convert(converter = AlternativaConverter.class)
    @Column(name = "alternativa")
    private List<Alternativa> alternativa;
}
