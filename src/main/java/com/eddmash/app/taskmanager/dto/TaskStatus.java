package com.eddmash.app.taskmanager.dto;

 import com.eddmash.app.taskmanager.exception.TaskException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE;

    @JsonCreator
    public  static TaskStatus fromString(String val) {
        for (TaskStatus status : values()) {
            if (status.name().equalsIgnoreCase(val)) {
                return status;
            }
        }
        throw new TaskException("Uknown TaskStatus [" + val + "] provided");
    }
}
