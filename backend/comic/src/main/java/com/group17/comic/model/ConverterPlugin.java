package com.group17.comic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter 
@AllArgsConstructor  
public class ConverterPlugin {
    UUID id;
    String name;
    String blobType; // Cái này dùng bên Client để download file 
}

