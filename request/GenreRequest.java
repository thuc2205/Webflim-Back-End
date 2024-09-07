package com.example.flim.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenreRequest {

    @NotBlank(message = "Name không được để trống")
    @Size(max = 50, message = "Name không được vượt quá 50 ký tự")
    private String name;
}
