package com.cybr406.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


/**
 * Delete a Todo and ALL of its Tasks with 1 request.
 *
 * Hint: Very similar to the last problem, but instead removing a task from an existing todo.
 *
 * Request:
 *     Path: /tasks/{id}
 *     Method: DELETE
 * Response:
 *     Code: 204 No Content on success, 404 Not Found if the task does not exist.
 *     Response Body: Nothing...an empty response.
 */

@RestController
public class TodoRestController {

    @Autowired
    TodoJpaRepository TodoJpaRepository ;

    @Autowired
    TaskJpaRepository TaskJpaRepository;

    @PostMapping("/todos")
    public ResponseEntity<Todo> CreateTodo(@Valid @RequestBody Todo todo){

        if(todo.getAuthor().isEmpty() || todo.getDetails().isEmpty()) {
            return new ResponseEntity<>(todo, HttpStatus.BAD_REQUEST);
        } else{
            return new ResponseEntity<>(TodoJpaRepository.save(todo), HttpStatus.CREATED);
        }
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> ReturnTodoDetail(@PathVariable long id){

        //Optional<Todo> list = JSON.find(id);
        Optional<Todo> list = TodoJpaRepository.findById(id);

        if (list.isPresent()) {
            Todo sample = list.get();

            return new ResponseEntity<>(sample, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/todos")
    public Page<Todo> SubsetTodos(Pageable page){

        //List<Todo> BigList = TodoJpaRepository.findAll(page);

        //return new ResponseEntity<>(BigList, HttpStatus.OK);
        return TodoJpaRepository.findAll(page);
    }

    @PostMapping("/todos/{id}/tasks")
    public ResponseEntity AddingTasks(@PathVariable long id,@RequestBody Task task){

        //Todo todo = JSON.addTask(id, task);
        //List<Task> Lists = todo.getTasks();

        Optional<Todo> todoOptional = TodoJpaRepository.findById(id);

        if(todoOptional.isPresent()){
            Todo todo = todoOptional.get();
            todo.getTasks().add(task);
            task.setTodo(todo);
            TaskJpaRepository.save(task);
            return new ResponseEntity<>(todo, HttpStatus.CREATED);
        } else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Todo> DeletingTodos(@PathVariable long id){


        if(TodoJpaRepository.existsById(id)){
            TodoJpaRepository.deleteById(id);
        }else{
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Todo> DeletingTasks(@PathVariable long id){

        if(TaskJpaRepository.existsById(id)){
            TaskJpaRepository.deleteById(id);
        }else{
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

