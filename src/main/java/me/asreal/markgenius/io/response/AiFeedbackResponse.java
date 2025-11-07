package me.asreal.markgenius.io.response;

import lombok.*;
import me.asreal.markgenius.utils.StructureLocation;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class AiFeedbackResponse {

    private String paperId;
    private String sectionFocus;
    private String[] modelUtilized;
    private String paperContextLink;

    private FeedbackDetail aiFeedbackDetail;
    private Map<String, Object> responseMetadata;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedbackDetail {
        private String summery;
        private StructureLocation coords;
        private List<String> aiSuggestions;
        private Map<Integer, Integer> sectionScore;
    }

}
