package brain.school.game.model;

import brain.school.game.dto.UpdateUserDto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.lang.reflect.Field;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UserTable")
@EqualsAndHashCode(of = "id")
public class User implements Serializable  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private int points = 0;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column()
    private String senha;

    @Column()
    private String municipio;

    @Column()
    private String estado;


    public User updateUser(UpdateUserDto updateUserDto){
        Field[] fields = UpdateUserDto.class.getDeclaredFields();
        for(Field field : fields){
            try {
                Object value = field.get(updateUserDto);
                if(value != null){
                    Field user = User.class.getDeclaredField(field.getName());

                    user.set(this, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Erro ao atualizar campo: " + field.getName(), e);
            }
        }
        return this;
    }

}



