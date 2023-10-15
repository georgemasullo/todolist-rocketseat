package br.com.george.todolist.filter;
import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.george.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    private IUserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/tasks/")){
            var authorization = request.getHeader("Authorization");

            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecode = Base64.getDecoder().decode(authEncoded);
            var auth = new String(authDecode).split(":");
            String username = auth[0];
            String password = auth[1];

            var user = this.userRepository.findByUserName(username);
            if (user == null){
                response.sendError(401);
            }else {
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(),user.getPassword());
                if (passwordVerify.verified){
                    request.setAttribute("idUser",user.getId());
                    filterChain.doFilter(request, response);
                }else {
                    response.sendError(401);
                }
            }
        }




    }
}
