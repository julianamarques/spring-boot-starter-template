package br.com.project.springboot.starter.template.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogContextEnum {
    API_CONTEXT("api");

    private final String description;

    public String getDescription(String arg) {
        return this.description + " :: " + arg;
    }
}
