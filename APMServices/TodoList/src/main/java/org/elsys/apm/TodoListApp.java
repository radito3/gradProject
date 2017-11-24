package org.elsys.apm;

import org.elsys.apm.todo.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Stream;

@Path("/TodoList")
public class TodoListApp {

    @GET
    @Produces("text/plain")
    public String getTodoList() {
        TodoList list1 = TodoList.parse(
                "TODO    | Do OOP homework              | High   | school, programming\r\n" +
                "TODO    | Get 8 hours of sleep.        | Low    | health\r\n" +
                "DOING   | Party hard.                  | Normal | social\r\n" +
                "DONE    | Netflix and chill.           | Normal | tv shows\r\n" +
                "TODO    | Find missing socks.          | Low    | meh\r\n");
        TodoList list2 = TodoList.parse(
                "TODO    | Finish maths homework        | Normal | school, programming\r\n" +
                "DONE    | Watch Star Wars 7.           | Normal | movie\r\n" +
                "TODO    | Find missing shirts.         | Low    | style\r\n");
        List<Task> todoList = list1.join(list2).getTasks();
        return formattedResult(todoList.stream());
    }

    private String formattedResult(Stream<Task> stream) {
        StringBuilder result = new StringBuilder();
        result.append("Todo List tasks:\n");
        stream.forEach(t -> {
            result.append("Task: ");
            result.append(t.getDescription());
            result.append("\n\t");
            result.append("Status: ");
            result.append(t.getStatus());
            result.append("\n\t");
            result.append("Priority: ");
            result.append(t.getPriority());
            result.append("\n\t");
            result.append("Tags: ");
            result.append(t.getTags());
            result.append("\n");
        });
        return result.toString();
    }

}
