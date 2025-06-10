package com.Joseph.WEE_GEnder_Tracker;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

public class QAItem {
    private String question;
    private String answer;
    private @ServerTimestamp Timestamp timestamp;
    private Timestamp answerTimestamp;
    private String documentId;
    private String userId;          // ID of user who asked the question
    private String answerUserId;    // ID of user who answered (nullable)

    // Required empty constructor
    public QAItem() {}

    public QAItem(String question, String answer, Timestamp timestamp) {
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    // Constructor with user tracking
    public QAItem(String question, String answer, Timestamp timestamp, String userId) {
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Getters and setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public Timestamp getAnswerTimestamp() { return answerTimestamp; }
    public void setAnswerTimestamp(Timestamp answerTimestamp) {
        this.answerTimestamp = answerTimestamp;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAnswerUserId() { return answerUserId; }
    public void setAnswerUserId(String answerUserId) {
        this.answerUserId = answerUserId;
    }

    @Exclude
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
}