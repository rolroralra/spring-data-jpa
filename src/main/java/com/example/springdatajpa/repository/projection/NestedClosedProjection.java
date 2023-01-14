package com.example.springdatajpa.repository.projection;

public interface NestedClosedProjection {
    String getName();

    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

}
