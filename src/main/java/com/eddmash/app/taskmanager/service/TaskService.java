package com.eddmash.app.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.eddmash.app.authentication.model.User;
import com.eddmash.app.authentication.repository.UserRepository;
import com.eddmash.app.authlibrary.dto.AuthUser;
import com.eddmash.app.shared.dto.GenericResponse;
import com.eddmash.app.shared.dto.GenericResponse.Pagination;
import com.eddmash.app.taskmanager.dto.TaskDto;
import com.eddmash.app.taskmanager.dto.TaskStatus;
import com.eddmash.app.taskmanager.exception.TaskException;
import com.eddmash.app.taskmanager.model.Task;
import com.eddmash.app.taskmanager.repository.TaskRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Getter
    private final TaskRepository taskRepository;

    @Getter
    private final UserRepository userRepository;

    /**
     * Create task
     * 
     * @param taskDto
     * @param authUser
     * @return
     */
    public GenericResponse<TaskDto> createTask(TaskDto taskDto, AuthUser authUser) {

        Task task = getTaskRepository().save(Task.builder()
                .active(1)
                .description(taskDto.getDescription())
                .dueDate(taskDto.getDueDate())
                .statusCode(taskDto.getTaskStatus().name())
                .dateCreated(LocalDateTime.now())
                .dateModified(LocalDateTime.now())
                .insertedBy(authUser.getUser())
                .updatedBy(authUser.getUser())
                .build());
        return buildTaskResponse(task);

    }

    /**
     * update task
     * 
     * @param taskID
     * @param taskDto
     * @param authUser
     * @return
     */
    public GenericResponse<TaskDto> updateTask(Long taskID, TaskDto taskDto, AuthUser authUser) {
        Task task = getTaskRepository().findById(taskID).orElseThrow(() -> {
            return new TaskException("Task does not exist");
        });

        task.setDueDate(taskDto.getDueDate());
        task.setDescription(taskDto.getDescription());
        task.setStatusCode(taskDto.getTaskStatus().name());
        task.setDateModified(LocalDateTime.now());
        task.setUpdatedBy(authUser.getUser());
        getTaskRepository().save(task);

        return buildTaskResponse(task);

    }

    /**
     * build the reponse
     * 
     * @param task
     * @param taskDto
     * @return
     */
    private GenericResponse<TaskDto> buildTaskResponse(Task task) {

        return GenericResponse.<TaskDto>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(buildTaskDto(task))
                .build();
    }

    private TaskDto buildTaskDto(Task task) {
        TaskDto taskDto = TaskDto.builder().id(task.getID())
                .dueDate(task.getDueDate())
                .taskStatus(TaskStatus.fromString(task.getStatusCode()))
                .description(task.getDescription())
                .build();
        taskDto.setDateCreated(task.getDateCreated());
        taskDto.setDateModified(task.getDateModified());
        taskDto.setInsertedByID(task.getInsertedBy().getId());
        taskDto.setUpdatedByID(task.getUpdatedBy().getId());
        taskDto.setInsertedBy(task.getInsertedBy().getEmail());
        taskDto.setUpdatedBy(task.getUpdatedBy().getEmail());
        return taskDto;
    }

    /**
     * fetch task
     * 
     * @param taskID
     * @param taskDto
     * @param authUser
     * @return
     */
    public GenericResponse<TaskDto> getTask(Long taskID) {
        Task task = getTaskRepository().findById(taskID).orElseThrow(() -> {
            return new TaskException("Task does not exist");
        });
        return buildTaskResponse(task);
    }

    /**
     * Delete task
     * 
     * @param taskID
     * @param taskDto
     * @param authUser
     * @return
     */
    public GenericResponse<TaskDto> deleteTask(Long taskID) {
        Task task = getTaskRepository().findById(taskID).orElseThrow(() -> {
            return new TaskException("Task does not exist");
        });
        getTaskRepository().delete(task);
        return GenericResponse.<TaskDto>builder()
                .data(null).message("Success").status(HttpStatus.OK.value())
                .build();
    }

    /**
     * fetch all tasks, with capability to filter by assigned user
     * 
     * @param taskID
     * @param taskDto
     * @param authUser
     * @return
     */
    public GenericResponse<List<TaskDto>> fetchAllTasks(String name, int pageNo, int pageSize) {
        pageNo = pageNo - 1;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Example<Task> filter = Example.of(Task.builder().description(name).build(),
                ExampleMatcher.matching()
                        .withIgnoreNullValues()
                        .withMatcher("description",
                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()));
        Page<Task> page = getTaskRepository().findAllTasks(filter, pageable);
        return buildPagedResponse(page);
    }

    /**
     * populate pagination details on the response.
     */
    private GenericResponse<List<TaskDto>> buildPagedResponse(Page<Task> page) {
        List<TaskDto> tasks = page.getContent().stream().map(this::buildTaskDto).toList();

        return GenericResponse.<List<TaskDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(tasks)
                .pagination(Pagination.builder()
                        .pageNo(page.getNumber() + 1)
                        .pageSize(page.getSize())
                        .totalPages(page.getTotalPages())
                        .totalRecords(page.getTotalElements())
                        .build())
                .build();
    }
}
