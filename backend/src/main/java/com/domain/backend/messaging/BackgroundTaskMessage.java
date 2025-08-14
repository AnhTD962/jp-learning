package com.domain.backend.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackgroundTaskMessage implements Serializable {
    private String taskId;
    private String taskType;
    private String payload;
}