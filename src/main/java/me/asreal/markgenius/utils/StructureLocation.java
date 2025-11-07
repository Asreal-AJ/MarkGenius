package me.asreal.markgenius.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class StructureLocation {

    private Integer[] startPoint;
    private List<Integer[]> range;

}
