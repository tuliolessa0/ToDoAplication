package br.com.marcotulio.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    IUserRepository iUserRepository;

    @PostMapping("")
    public ResponseEntity create(@RequestBody UserModel userModel){
        var user = iUserRepository.findByUsername(userModel.getUsername());
        if (user != null){
            System.out.println("Usuário já Existente");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario ja existe");
        }

        var passwordHasred = BCrypt.withDefaults().hashToString(12,userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHasred);

    var userCreated = iUserRepository.save(userModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

}
