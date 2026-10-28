package org.example.member.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(long id) {
        super("존재하지 않는 회원입니다. (ID: " + id + ")");
    }
}
