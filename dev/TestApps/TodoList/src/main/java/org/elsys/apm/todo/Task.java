package org.elsys.apm.todo;

public interface Task {

    Status getStatus();

    String getDescription();

    Priority getPriority();

    String[] getTags();
}
