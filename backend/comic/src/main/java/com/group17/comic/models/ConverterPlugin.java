package com.group17.comic.models;

import java.util.UUID;

import com.group17.comic.tagging_interfaces.IConcretePlugin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConverterPlugin implements IConcretePlugin {
    UUID id;
    String name;
    String blobType; // Cái này dùng bên Client để download file
}
