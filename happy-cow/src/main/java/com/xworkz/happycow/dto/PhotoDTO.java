package com.xworkz.happycow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
    private byte[] bytes;
    private String contentType; // e.g., "image/jpeg", "image/png", "image/webp"
}