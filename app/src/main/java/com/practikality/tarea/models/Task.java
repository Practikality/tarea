package com.practikality.tarea.models;

public class Task {
    private String taskTo;
    private String title;
    private String priority;

    public Task(String taskTo, String title, String priority) {
        this.taskTo = taskTo;
        this.title = title;
        this.priority = priority;
    }

    public Task() {
    }

    public String getTaskTo() {
        return taskTo;
    }

    public void setTaskTo(String taskTo) {
        this.taskTo = taskTo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskTo='" + taskTo + '\'' +
                ", title='" + title + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }
}
