package br.com.marcotulio.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.marcotulio.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component//GERENCIAMENTO DO SPRING (generico, todas as requisções passam por aqui)
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    IUserRepository iUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //especificar a rota para o filtro

        var serveletPath = request.getServletPath();

        if (serveletPath.equals("/tasks/")) {
            //pegar autenticação (user,password)
            var authorization = request.getHeader("Authorization");
            var authEnconded = authorization.substring(5).trim();

            byte[] authDecode = Base64.getDecoder().decode(authEnconded);

            var authString = new String(authDecode);
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            System.out.println("Authorization");
            System.out.println(username);
            System.out.println(password);

            //valida usuario

            var user = iUserRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401);
            } else {
                //validas senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    //vai embora
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }

            }
        } else {
            filterChain.doFilter(request, response);
        }


    }
}
