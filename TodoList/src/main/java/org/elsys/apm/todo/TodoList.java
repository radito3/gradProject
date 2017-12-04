package org.elsys.apm.todo;

import org.elsys.apm.todo.impl.TodoListImpl;

import java.util.List;

public interface TodoList {

    static TodoList parse(String input) {
        return new TodoListImpl(input);
    }

    List<Task> getTasks();

    TodoList join(TodoList other);
}
