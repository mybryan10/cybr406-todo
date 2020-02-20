package com.cybr406.todo;

import org.springframework.beans.factory.annotation.Autowired;
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
    InMemoryTodoRepository JSON;

    @PostMapping("/todos")
    public ResponseEntity<Todo> CreateToDo(@Valid @RequestBody Todo todo){

        if(todo.getAuthor().isEmpty() || todo.getDetails().isEmpty()) {
            return new ResponseEntity<>(todo, HttpStatus.BAD_REQUEST);
        } else{
            return new ResponseEntity<>(JSON.create(todo), HttpStatus.CREATED);
        }
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> ReturnTodoDetail(@PathVariable long id){

        Optional<Todo> list = JSON.find(id);

        if (list.isPresent()) {
            Todo sample = list.get();

            return new ResponseEntity<>(sample, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Todo>> SubsetTodos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){

        List<Todo> BigList = JSON.findAll(page, size);

        return new ResponseEntity<>(BigList, HttpStatus.OK);
    }

    @PostMapping("/todos/{id}/tasks")
    public ResponseEntity AddingTasks(@PathVariable long id,@RequestBody Task task){

        Todo todo = JSON.addTask(id, task);

        List<Task> Lists = todo.getTasks();

        if (!Lists.isEmpty()) {
            return new ResponseEntity<>(todo, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Todo> DeletingTodos(@PathVariable long id){

        try {
            JSON.delete(id);
        }catch (NoSuchElementException e){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Todo> DeletingTasks(@PathVariable long id){

        try {
            JSON.deleteTask(id);
        }catch (NoSuchElementException e){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

