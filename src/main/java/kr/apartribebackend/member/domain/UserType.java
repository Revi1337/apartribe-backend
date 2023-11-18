package kr.apartribebackend.member.domain;

import lombok.Getter;

@Getter
public enum UserType {

    RESIDENT("resident"),
    MANAGER("manager");

    private final String name;

    UserType(String name) {
        this.name = name;
    }
}
