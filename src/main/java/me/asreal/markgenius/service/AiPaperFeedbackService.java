package me.asreal.markgenius.service;

public interface AiPaperFeedbackService {

    String summerizePaper(String paper);
    String feedback(String paper, String focus);
    String retrieveFeedbackAsJson(String summery, String focus);


}
