package duke;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class TestTaskList {
    @Test
    public void taskList_testAddTasks() throws DukeInvalidDateException {
        Ui ui = new Ui();
        TaskList taskList = new TaskList(ui);

        Todo todo = new Todo("Todo1");
        taskList.addTask(todo);
        assertTrue(taskList.getTasks().contains(todo));

        Deadline deadline = new Deadline("Deadline1", "2023-08-31 12:00");
        taskList.addTask(deadline);
        assertTrue(taskList.getTasks().contains(deadline));

        Event event = new Event("Deadline1", "2023-08-31 12:00", "2023-08-31 12:00");
        taskList.addTask(event);
        assertTrue(taskList.getTasks().contains(event));
    }

    @Test
    public void taskList_testDeleteTask() {
        Ui ui = new Ui();
        TaskList taskList = new TaskList(ui);

        Todo todo = new Todo("Todo1");
        taskList.addTask(todo);
        assertTrue(taskList.getTasks().contains(todo));

        taskList.deleteTask(0);
        assertTrue(taskList.getTasks().isEmpty());
    }

    @Test
    public void taskList_testDeleteTask_invalidIndex() {
        Ui ui = new Ui();
        TaskList taskList = new TaskList(ui);

        Todo todo = new Todo("Todo1");
        taskList.addTask(todo);
        assertTrue(taskList.getTasks().contains(todo));

        taskList.deleteTask(1);
        assertTrue(taskList.getTasks().contains(todo));
    }
    @Test
    public void taskList_testMarkTask() {
        Ui ui = new Ui();
        TaskList taskList = new TaskList(ui);

        Todo todo = new Todo("Todo1");
        taskList.addTask(todo);
        assertTrue(taskList.getTasks().contains(todo));

        taskList.markTask(0);
        assertTrue(taskList.getTasks().get(0).isCompleted);
    }

    @Test
    public void taskList_testMarkAndUnmarkTask() {
        Ui ui = new Ui();
        TaskList taskList = new TaskList(ui);

        Todo todo = new Todo("Todo1");
        taskList.addTask(todo);
        assertTrue(taskList.getTasks().contains(todo));

        taskList.markTask(0);
        assertTrue(taskList.getTasks().get(0).isCompleted);

        taskList.unmarkTask(0);
        assertFalse(taskList.getTasks().get(0).isCompleted);
    }

}
