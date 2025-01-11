package brain.school.game.controller;


import brain.school.game.dto.Login;
import brain.school.game.dto.UpdateUserDto;
import brain.school.game.dto.UserDto;
import brain.school.game.model.User;
import brain.school.game.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
    }


    @PostMapping("/teste")
    public ResponseEntity<String> login(@RequestBody Login login) {
        return new ResponseEntity<>(userService.login(login), HttpStatus.CREATED);
    }

    @PutMapping("/{email}")
    public ResponseEntity<User> updateUser(@PathVariable("email") String email, @RequestBody UpdateUserDto updateUserDto){
        return  new ResponseEntity<User>(userService.update(email, updateUserDto), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getUsers(){
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> getUser(@PathVariable("email") String email){
        return new ResponseEntity<>(userService.getYouUser(email), HttpStatus.OK);
    }
}
