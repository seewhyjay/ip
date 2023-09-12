package duke;

import java.util.EnumSet;

/**
 * Parses user input commands and performs corresponding actions on the task list and user interface.
 * The Parser class handles various commands related to managing tasks and interacting with the user.
 */
public class Parser {
    private final TaskList taskList;
    private final Ui ui;

    public Parser(Ui ui, TaskList taskList) {
        this.ui = ui;
        this.taskList = taskList;
    }

    /**
     * Represents different types of commands that the user can input.
     */
    private enum Command {
        invalid, bye, list, mark, unmark, delete, find, todo, deadline, event, duplicates;

        /**
         * Returns an EnumSet of Command containing task-related command types.
         * This is useful for determining whether a given command involves adding tasks.
         * If more task types are added in the future can just add here so I don't have to change in main
         * when checking if it's an adding task command
         *
         * @return An EnumSet containing task-related command types (todo, deadline, event).
         */
        public static EnumSet<Command> taskTypes() {
            return EnumSet.of(todo, deadline, event);
        }
    }

    /**
     * Parses the user's input command and executes the corresponding action.
     *
     * @param input The user's input command.
     * @return The response from Duke
     */
    public String parseCommand(String input) {
        Command cmd = Command.invalid;
        for (Command c : Command.values()) {
            if (input.startsWith(c.toString())) {
                cmd = c;
            }
        }
        if (cmd.equals(Command.duplicates)) {
            TaskList duplicates = taskList.removeDuplicates();
            return "The following duplicate tasks were removed:\n" + duplicates;
        } else if (cmd.equals(Command.bye)) {
            ui.showByeMessage();
            return "Bye! Hope to see you soon!";
        } else if (cmd.equals(Command.list)) {
            ui.showTaskList(taskList);
            return taskList.toString();
        } else if (cmd.equals(Command.mark)) {
            try {
                return taskList.markTask(Integer.parseInt(input.split(" ")[1]));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                ui.showInvalidIndexError();
                return "Invalid task index!";
            }
        } else if (cmd.equals(Command.unmark)) {
            try {
                return taskList.unmarkTask(Integer.parseInt(input.split(" ")[1]));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                ui.showInvalidIndexError();
                return "Invalid task index!";
            }
        } else if (cmd.equals(Command.delete)) {
            try {
                return taskList.deleteTask(Integer.parseInt(input.split(" ")[1]));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                ui.showInvalidIndexError();
                return "Invalid task index!";
            }
        } else if (cmd.equals(Command.invalid)) {
            ui.showInvalidCommandError();
            return "Invalid command!";
        } else if (cmd.equals(Command.find)) {
            ui.showFindResults(taskList.find(input.split(" ", 2)[1]));
            return taskList.find(input.split(" ", 2)[1]).toString();
        } else if (Command.taskTypes().contains(cmd)) {
            try {
                return createTask(cmd, input);
            } catch (DukeInvalidDateException e) {
                ui.showAddTaskError(e.getMessage());
                return e.getMessage();
            }
        }
        return "";
    }

    private String getTaskInfo(String input) {
        for (Command cmd : Command.taskTypes()) {
            if (input.startsWith(cmd.toString())) {
                return input.replace(cmd + " ", "");
            }
        }
        // Will never happen
        return null;
    }

    /**
     * Parses the input information to create and add an Event task to the TaskList.
     *
     * @param info The input information containing details about the Event task.
     * @return String indicating the event information or the error when trying to create event
     * @throws DukeInvalidDateException If the input contains invalid date formats for the event's start and end times.
     */
    private String parseAndAndEvent(String info) throws DukeInvalidDateException {
        if (!info.matches(".*\\b /by \\b.*") || !info.matches(".*\\b /to \\b.*")) {
            return "An event must contain a description," +
                    " start and end specified with `/by` and `/to`!";
        } else {
            // In case the user does /to before /by, split /by and /to and vice versa to get by and to
            try {
                String by = info.split(" /by ")[1];
                by = by.split(" /to ")[0];
                String to = info.split(" /to ")[1];
                to = to.split(" /by ")[0];
                // Get index 0 to get before the /by and /to, so either way you'll get only the task
                String task = info.split(" /to ")[0].split(" /by ")[0];
                task = task.replaceFirst("event ", "");
                Event event = new Event(task, by, to);
                taskList.addTask(event);
                return "Got it! I've added the Event: " + event;
            } catch (IndexOutOfBoundsException e) {
                ui.showAddTaskError("Description, /by and /to cannot be empty!");
                return "Event description, /by and /to cannot be empty!";
            }
        }
    }

    /**
     * Creates a task based on the provided command and input, and adds it to the task list.
     *
     * @param cmd The command indicating the type of task to create.
     * @param input The user's input containing task information.
     * @return String indicating the task information or the error when trying to create task
     * @throws DukeInvalidDateException If the input contains an invalid date format for tasks that require dates.
     */
    private String createTask(Command cmd, String input) throws DukeInvalidDateException {
        String[] splitInput = input.split(" ");
        if (splitInput.length < 2) {
            ui.showAddTaskError("Task description cannot be empty!");
            return "Task description cannot be empty!";
        }
        String info = getTaskInfo(input);
        if (cmd.equals(Command.todo)) {
            Todo todo = new Todo(info);
            taskList.addTask(todo);
            return "Got it! I've added the Todo: " + todo;
        } else if (cmd.equals(Command.deadline)) {
            assert info != null;
            String[] splitInfo = info.split(" /by ", 2);
            if (splitInfo.length == 2) {
                Deadline deadline = new Deadline(splitInfo[0], splitInfo[1]);
                taskList.addTask(deadline);
                return "Got it! I've added the Deadline: " + deadline;
            } else {
                ui.showAddTaskError("Description and /by cannot be empty!");
                return "Deadline description and /by cannot be empty!";
            }
        } else if (cmd.equals(Command.event)) {
            assert info != null;
            return parseAndAndEvent(info);
        }
        return "Unknown task type!";  // Should never happen
    }
}
