package org.example.member.domain;

import org.example.global.entity.BaseEntity;
import org.example.global.exception.BusinessException;
import org.example.member.exception.MemberErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    protected Member() {
    }

    public Member(String name) {
        validateName(name);
        this.name = name.trim();
    }

    private void validateName(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(MemberErrorCode.NAME_REQUIRED);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
