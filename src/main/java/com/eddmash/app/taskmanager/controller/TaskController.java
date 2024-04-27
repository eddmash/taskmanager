package com.eddmash.app.taskmanager.controller;

import org.springframework.web.bind.annotation.RestController;

import com.eddmash.app.authlibrary.dto.AuthUser;
import com.eddmash.app.shared.dto.GenericResponse;
import com.eddmash.app.taskmanager.dto.TaskDto;
import com.eddmash.app.taskmanager.service.TaskService;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {
        @Getter
        private final TaskService taskService;

        /**
         * Create a task
         * 
         * @param taskDto
         * @return
         */
        @PostMapping
        public ResponseEntity<GenericResponse<TaskDto>> createTask(@Valid @RequestBody TaskDto taskDto,
                        @AuthenticationPrincipal AuthUser authUser) {
                GenericResponse<TaskDto> response = getTaskService().createTask(taskDto, authUser);
                return new ResponseEntity<GenericResponse<TaskDto>>(response, new LinkedMultiValueMap<>(),
                                response.getStatus());
        }

        /**
         * Update a task
         * 
         * @param taskDto
         * @return
         */
        @PutMapping("/{taskID}")
        public ResponseEntity<GenericResponse<TaskDto>> createTask(@PathVariable Long taskID,
                        @Valid @RequestBody TaskDto taskDto,
                        @AuthenticationPrincipal AuthUser authUser) {
                GenericResponse<TaskDto> response = getTaskService().updateTask(taskID, taskDto, authUser);
                return new ResponseEntity<GenericResponse<TaskDto>>(response, new LinkedMultiValueMap<>(),
                                response.getStatus());
        }

        /**
         * Fetch a task
         * 
         * @param taskDto
         * @return
         */
        @GetMapping("/{taskID}")
        public ResponseEntity<GenericResponse<TaskDto>> fetchTask(@PathVariable Long taskID,
                        @AuthenticationPrincipal AuthUser authUser) {
                GenericResponse<TaskDto> response = getTaskService().getTask(taskID);
                return new ResponseEntity<GenericResponse<TaskDto>>(response, new LinkedMultiValueMap<>(),
                                response.getStatus());
        }

        /**
         * Fetch a task
         * 
         * @param taskDto
         * @return
         */
        @DeleteMapping("/{taskID}")
        public ResponseEntity<GenericResponse<TaskDto>> deleteTask(@PathVariable Long taskID,
                        @AuthenticationPrincipal AuthUser authUser) {
                GenericResponse<TaskDto> response = getTaskService().deleteTask(taskID);
                return new ResponseEntity<GenericResponse<TaskDto>>(response, new LinkedMultiValueMap<>(),
                                response.getStatus());
        }

        /**
         * Fetch all tasks
         * 
         * @param taskDto
         * @return
         */
        @GetMapping
        public ResponseEntity<GenericResponse<List<TaskDto>>> fetchAllTasks(
                        @RequestParam(required = false) String name,
                        @RequestParam(defaultValue = "0", required = false) int pageNo,
                        @RequestParam(defaultValue = "100", required = false) int pageSize) {
                GenericResponse<List<TaskDto>> response = getTaskService().fetchAllTasks(name, pageNo, pageSize);
                return new ResponseEntity<GenericResponse<List<TaskDto>>>(response, new LinkedMultiValueMap<>(),
                                response.getStatus());
        }

}
