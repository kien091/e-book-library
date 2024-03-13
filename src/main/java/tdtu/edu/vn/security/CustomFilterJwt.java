package tdtu.edu.vn.security;
import javax.servlet.FilterChain; // Change this
import javax.servlet.ServletException; // Change this
import javax.servlet.http.HttpServletRequest; // Change this
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tdtu.edu.vn.model.User;
import tdtu.edu.vn.service.ebook.UserService;
import tdtu.edu.vn.util.JwtUtilsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CustomFilterJwt extends OncePerRequestFilter {
    JwtUtilsHelper jwtUtilsHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromHeader(request);

        System.out.println("Token: " + token);

        if (token != null) {
            if (jwtUtilsHelper.verifyToken(token)) {
                System.out.println("Token kiemtra: " + token);

                String email = jwtUtilsHelper.getEmailFromToken(token);
                String role = jwtUtilsHelper.getRoleFromToken(token);
                System.out.println("Email: " + email);
                System.out.println("Role: " + role);

                List<GrantedAuthority> authorities = new ArrayList<>();

                if (role != null && !role.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(role));
                } else {
                    System.out.println("Role is null or empty, assigning default role 'USER'");
                    authorities.add(new SimpleGrantedAuthority("USER"));
                }

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContext securityContext = SecurityContextHolder.getContext();

                securityContext.setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        String token = null;

        if(StringUtils.hasText(header) && header.startsWith("Bearer ")){
            token = header.substring(7);
        }

        return token;
    }
}
