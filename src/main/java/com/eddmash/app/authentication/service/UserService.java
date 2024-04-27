package com.eddmash.app.authentication.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.eddmash.app.authentication.dto.SignUpDto;
import com.eddmash.app.authentication.exception.AuthenticationException;
import com.eddmash.app.authentication.model.User;
import com.eddmash.app.authentication.repository.UserRepository;
import com.eddmash.app.authlibrary.dto.AuthUser;
import com.eddmash.app.shared.dto.GenericResponse;
import com.eddmash.app.shared.dto.GenericResponse.Pagination;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
        @Getter
        private final UserRepository userRepository;

        public GenericResponse<User> updateUser(Long userID, SignUpDto signUpDto, AuthUser authUser) {
                User user = getUserRepository().findById(userID)
                                .orElseThrow(() -> new AuthenticationException("User not found"));
                user.setName(signUpDto.getName());
                user.setDateModified(LocalDateTime.now());
                user.setUpdatedBy(authUser.getUser().getId());
                getUserRepository().save(user);
                return GenericResponse.<User>builder()
                                .data(user)
                                .message("success")
                                .status(HttpStatus.OK.value())
                                .build();
        }

        public GenericResponse<User> getUser(Long userID, AuthUser authUser) {
                User user = getUserRepository().findById(userID)
                                .orElseThrow(() -> new AuthenticationException("User not found"));
                return GenericResponse.<User>builder()
                                .data(user)
                                .message("success")
                                .status(HttpStatus.OK.value())
                                .build();
        }

        public GenericResponse<List<User>> fetchAll(String name, AuthUser authUser, int pageNo, int pageSize) {
                log.info("Fetching users {Page: " + pageNo + " Name: " + name + "}");
                pageNo = pageNo - 1;
                ExampleMatcher filter = ExampleMatcher.matchingAny()
                                .withIgnoreNullValues()
                                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

                Page<User> page = getUserRepository().findAll(Example.of(User.builder().name(name).build(), filter),
                                PageRequest.of(pageNo, pageSize));
                return GenericResponse.<List<User>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Success")
                                .data(page.getContent())
                                .pagination(Pagination.builder()
                                                .pageNo(page.getNumber() + 1)
                                                .pageSize(page.getSize())
                                                .totalPages(page.getTotalPages())
                                                .totalRecords(page.getTotalElements())
                                                .build())
                                .build();
        }

        /**
         * Delete action, basically deactivate the users.
         * 
         * @param userID
         * @param authUser
         * @return
         */
        public GenericResponse<User> changeUserStatus(Long userID, Integer active, AuthUser authUser) {
                User user = getUserRepository().findById(userID)
                                .orElseThrow(() -> new AuthenticationException("User not found"));

                user.setActive(active);
                user.setUpdatedBy(authUser.getUser().getId());
                user.setDateModified(LocalDateTime.now());
                getUserRepository().save(user);
                return GenericResponse.<User>builder()
                                .data(null)
                                .message("success")
                                .status(HttpStatus.OK.value())
                                .build();
        }
}
