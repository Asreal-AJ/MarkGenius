package me.asreal.markgenius.io.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AiFeedbackRequest {

    private String paperId;
    private String paperSummery;
    private String focus;

}
