package com.example.flim.controllers;

import com.example.flim.entities.Actor;
import com.example.flim.request.ActorRequest;
import com.example.flim.services.ActorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/actor")
public class ActorController {

    private final ActorService actorService;

    @PostMapping("")
    public ResponseEntity<?> createActor(@RequestBody @Valid ActorRequest actorRequest,
                                         BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        Actor actor = actorService.save(actorRequest);
        return ResponseEntity.ok(actor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActor(@PathVariable int id,
                                         @RequestBody @Valid ActorRequest actorRequest,
                                         BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessage);
        }
        try {
            Actor updatedActor = actorService.update(id, actorRequest);
            return ResponseEntity.ok(updatedActor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable int id) {
        try {
            actorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
