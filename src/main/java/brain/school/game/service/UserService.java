package brain.school.game.service;




import brain.school.game.config.TokenService;
import brain.school.game.dto.*;
import brain.school.game.model.User;
import brain.school.game.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${geoname.url}")
    private String geonameUrl;

    public User create(UserDto createUser){
        RestTemplate restTemplate = new RestTemplate();

        GeoNameResponse response = restTemplate.getForObject(geonameUrl + "/childrenJSON?geonameId=3469034", GeoNameResponse.class);

        logger.info(response.toString());
        Boolean exists = false;

        String geonameId = null;

        for(GeoObject geo : response.geonames){
            if(geo.toponymName.toUpperCase().equals(createUser.estado.toUpperCase())){
                exists = true;
                geonameId = geo.geonameId;
            }

        }
        if(!exists || geonameId == null){
            throw new RuntimeException("Estado não encontrado na resposta GeoName.");
        }

        GeoNameResponse stateResponse = restTemplate.getForObject(geonameUrl + "/childrenJSON?geonameId=" + geonameId, GeoNameResponse.class);

        Boolean existsCity = false;
        for(GeoObject geo : stateResponse.geonames){
            if(geo.toponymName.toUpperCase().equals(createUser.municipio.toUpperCase())){
                existsCity = true;
            }

        }

        if(!existsCity){
            throw new RuntimeException("Cidade não encontrado na resposta GeoName.");
        }
        String encodedPassword = encoder.encode(createUser.senha);

        User user = User.builder()
                .nome(createUser.nome)
                .senha(encodedPassword)
                .email(createUser.email)
                .municipio(createUser.municipio)
                .estado(createUser.estado)
                .build();

        return userRepository.save(user);

    }


    public String login(Login login){

        User data = userRepository.findByEmail(login.email);

        if (data == null) {
            throw new IllegalArgumentException("Usuário não encontrado com o e-mail fornecido.");
        }


        var token = tokenService.generateToken(login);

        return token;

    }

    public User update(String email, UpdateUserDto updateUserDto){
        User data = userRepository.findByEmail(email);
        data.updateUser(updateUserDto);
        return userRepository.save(data);
    }

    public List<User> getUsers(){
        return userRepository.findAll();

    }

    public User getYouUser(String email){
        return userRepository.findByEmail(email);
    }
}
