package com.develop.logsentry.domain.team.entity;

public enum TeamRole {
    ADMIN(TeamRole.Authority.ADMIN),
    MEMBER(TeamRole.Authority.MEMBER);

    private final String authority;

    TeamRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String MEMBER = "ROLE_MEMBER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}