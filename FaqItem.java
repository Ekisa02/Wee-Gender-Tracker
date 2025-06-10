package com.Joseph.WEE_GEnder_Tracker;

public class FaqItem {
    private String id;          // Firestore document ID or null for local items
    private String question;
    private String answer;

    public FaqItem(String id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
