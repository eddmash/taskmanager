package com.eddmash.app.taskmanager.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eddmash.app.taskmanager.model.Task;

@Repository
public interface TaskRepository
        extends JpaRepository<Task, Long> {

    /**
     * avoid N+1 problem with the entity graph
     * 
     * @param pageable
     * @return
     */
    @EntityGraph("Task.user")
    @Query("from Task")
    Page<Task> findAllTasks(Example<Task> example,Pageable pageable);
}
