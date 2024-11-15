package com.Ahmed.dto;

import lombok.*;

import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    @Size(max = 4096)
    private String content;
}
