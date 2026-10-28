package org.example.member.exception;

import org.example.global.exception.BusinessException;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}
