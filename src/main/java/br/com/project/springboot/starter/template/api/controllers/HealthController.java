package br.com.project.springboot.starter.template.api.controllers;

import br.com.project.springboot.starter.template.api.dtos.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping(value = "/check")
    public ResponseEntity<Response<String>> check() {
        return Response.success("Up!");
    }
}
