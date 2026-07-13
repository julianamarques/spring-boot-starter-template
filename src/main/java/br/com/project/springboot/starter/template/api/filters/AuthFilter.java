package br.com.project.springboot.starter.template.api.filters;

import br.com.project.springboot.starter.template.api.dtos.response.Response;
import br.com.project.springboot.starter.template.api.entities.User;
import br.com.project.springboot.starter.template.api.enums.ApiMessageEnum;
import br.com.project.springboot.starter.template.api.exceptions.ApiException;
import br.com.project.springboot.starter.template.api.service.AuthService;
import br.com.project.springboot.starter.template.api.utils.JsonUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final JsonUtils jsonUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (Objects.isNull(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);

            return;
        }

        final String token = authHeader.substring(BEARER_PREFIX.length());

        if (token.isBlank()) {
            filterChain.doFilter(request, response);

            return;
        }

        try {
            User user = authService.validateToken(token);
            List<GrantedAuthority> roles = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), roles);

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException | ApiException e) {
            Response<Object> responseApi = new Response<>(HttpStatus.UNAUTHORIZED, ApiMessageEnum.ACCESS_DENIED.getMessage(), null);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().print(jsonUtils.objectToJson(responseApi));

            return;
        }

        filterChain.doFilter(request, response);
    }
}
