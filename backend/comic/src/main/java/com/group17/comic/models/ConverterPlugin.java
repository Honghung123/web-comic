package com.group17.comic.models;

import com.group17.comic.tagging_interfaces.IConcretePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter 
@AllArgsConstructor  
public class ConverterPlugin implements IConcretePlugin {
    UUID id;
    String name;
    String blobType; // Cái này dùng bên Client để download file

}

