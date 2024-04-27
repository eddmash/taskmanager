package com.eddmash.app.taskmanager.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.eddmash.app.authentication.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NamedEntityGraph(name = "Task.user", attributeNodes = { @NamedAttributeNode("insertedBy"),
        @NamedAttributeNode("updatedBy") })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tm_task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long ID;
    @Column(nullable = false)
    private String statusCode;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDateTime dueDate;
    @Column(nullable = false)
    private LocalDateTime dateCreated;
    private Integer active;
    @Column(nullable = false)
    private LocalDateTime dateModified;

    @ManyToMany
    private Set<User> assignedTo;

    @ManyToOne
    @JoinColumn(name = "inserted_by_id", nullable = false)
    private User insertedBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_id", nullable = false)
    private User updatedBy;
}
