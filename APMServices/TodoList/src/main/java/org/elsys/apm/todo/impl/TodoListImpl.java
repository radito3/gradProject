package org.elsys.apm.todo.impl;

import org.elsys.apm.todo.Priority;
import org.elsys.apm.todo.Status;
import org.elsys.apm.todo.Task;
import org.elsys.apm.todo.TodoList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodoListImpl implements TodoList {

    private List<Task> tasks = new ArrayList<>();

    public TodoListImpl(String input) {
        List<String> lines = Arrays.asList(input.split("\n"));
        lines.forEach(line -> {
                Pattern pattern =
                        Pattern.compile("^(\\w+)\\s+\\|\\s(.+)\\s+\\|\\s(\\w+)\\s*\\|\\s(.+)\r$");
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    Status status = Status.valueOf(Status.class, matcher.group(1));
                    String descr = matcher.group(2);
                    Priority priority = Priority.valueOf(Priority.class, matcher.group(3).toUpperCase());
                    String[] tags = matcher.group(4).split(", ");
                    this.tasks.add(new TaskImpl(status, descr, priority, tags));
                }
            });
    }

    private TodoListImpl(List<Task> list) {
        this.tasks = list;
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
    }

    @Override
    public TodoList join(TodoList other) {
        List<Task> list = this.getTasks();
        other.getTasks().stream().filter(t -> !list.contains(t)).forEach(list::add);
        return new TodoListImpl(list);
    }

}
