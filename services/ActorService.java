package com.example.flim.services;

import com.example.flim.entities.Actor;
import com.example.flim.repositories.ActorRepository;
import com.example.flim.request.ActorRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    @Transactional
    public Actor save(ActorRequest actorRequest){
        Actor actor = Actor.builder()
                .name(actorRequest.getName())
                .bio(actorRequest.getBio())
                .birthdate(actorRequest.getBirthdate())
                .build();
        return actorRepository.save(actor);
    }



    @Transactional
    public void delete(int id) {
        actorRepository.deleteById(id);
    }

    @Transactional
    public Actor update(int id, ActorRequest actorRequest) {
        Optional<Actor> existingActorOpt = actorRepository.findById(id);
        if (existingActorOpt.isPresent()) {
            Actor existingActor = existingActorOpt.get();
            existingActor.setName(actorRequest.getName());
            existingActor.setBio(actorRequest.getBio());
            existingActor.setBirthdate(actorRequest.getBirthdate());
            return actorRepository.save(existingActor);
        } else {
            throw new RuntimeException("Actor not found with id: " + id);
        }
    }

    public Actor findById(int id) {
        return actorRepository.findById(id).orElseThrow(() -> new RuntimeException("Actor not found with id: " + id));
    }

}
