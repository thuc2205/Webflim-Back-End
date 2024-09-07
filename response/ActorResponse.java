package com.example.flim.response;

import com.example.flim.entities.Actor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Builder
public class ActorResponse {
    private Long id;
    private String name;
    private String bio;
    private LocalDate birthdate;

    public static ActorResponse fromActor(Actor actor) {
        return ActorResponse.builder()
                .id(actor.getId())
                .name(actor.getName())
                .bio(actor.getBio())
                .birthdate(actor.getBirthdate())
                .build();
    }
}
