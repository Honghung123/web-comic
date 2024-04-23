package com.group17.comic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor  
public class ConverterPlugin {
    Integer id;
    String name;
    String blobType; // Cái này dùng bên Client để download file 
}

