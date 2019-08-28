import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class ToDoList {
    private ArrayList<Task> taskList;
    private String filePath;
    static SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HHmm");
    static SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMMM yyyy HH':'mma");
    ToDoList(String filePath) {
        taskList = new ArrayList<>(100);
        this.filePath = filePath;
    }

    private void addTask(String task, TaskType type, boolean printText) throws ParseException,EmptyEventDscDukeException, EmptyDeadlineDscDukeException, NoDateDukeException {
        Task addedTask;
        String date;
        switch (type) {
            case TODO:
                addedTask = new TodoTask(task);
                taskList.add(addedTask);
                if (printText) {
                    System.out.println("Got it. I've added this task:");
                    System.out.println(addedTask);
                }
                break;
            case DEADLINE:
                if (task.indexOf('/') == -1) {
                    throw new NoDateDukeException("/ wasn't found");
                }
                if (task.indexOf('/') == 0) {
                    throw new EmptyDeadlineDscDukeException("Description for deadline is empty.");
                }
                date = task.substring(task.indexOf('/') + 1);
                if (date.equals("")) {
                    throw new NoDateDukeException("No date provided");
                }
                date = date.substring(date.indexOf(" ") + 1);
                addedTask = new Deadline(
                    task.substring(0, task.indexOf('/') - 1),
                    ToDoList.inputDateFormat.parse(date)
                );
                taskList.add(addedTask);
                if (printText) {
                    System.out.println("Got it. I've added this task:");
                    System.out.println(addedTask);
                }
                break;
            case EVENT:
                if (task.indexOf('/') == -1) {
                    throw new NoDateDukeException("No date found");
                }
                if (task.indexOf('/') == 0) {
                    throw new EmptyEventDscDukeException("Description for event is empty.");
                }
                date = task.substring(task.indexOf('/') + 1);
                if (date.equals("")) {
                    throw new NoDateDukeException("No date provided");
                }
                date = date.substring(date.indexOf(" ") + 1);
                addedTask = new Event(
                        task.substring(0, task.indexOf('/') - 1),
                        ToDoList.inputDateFormat.parse(date)
                );
                taskList.add(addedTask);
                if (printText) {
                    System.out.println("Got it. I've added this task:");
                    System.out.println(addedTask);
                }
                break;
            default:
                // throw some exception here?
                break;
        }
        if (printText) {
            printTotalTasks();
        }
    }
    private void listAllTasks() {
        int total = taskList.size();
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < total; i++) {
            System.out.println((i + 1) + ". " + taskList.get(i));
        }
    }
    private void checkTask(int taskIndex) {
        Task t = taskList.get(taskIndex);
        t.taskDone();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(t);
    }
    private void deleteTask(int taskIndex) throws InvalidTaskIndexDukeException {
        if (taskIndex < 0 || taskIndex > taskList.size() - 1) {
            throw new InvalidTaskIndexDukeException(taskIndex + " exceeds the range of taskList.");
        }
        System.out.println("Noted. I've removed this task:");
        System.out.println(taskList.get(taskIndex));
        taskList.remove(taskIndex);
        printTotalTasks();
    }

    private String saveInfo() {
        StringBuilder info = new StringBuilder();
        boolean isFirstIteration = true;
        for (Task t : taskList) {
            if (isFirstIteration) {
                info.append(t.saveInfo());
                isFirstIteration = false;
            } else {
                info.append(System.getProperty("line.separator")).append(t.saveInfo());
            }

        }
        return info.toString();
    }

    private void printTotalTasks() {
        System.out.println("Now you have " + taskList.size() + " tasks in the list.");
    }

    boolean parseInput(String input, boolean printText) throws DukeException, ParseException {
        String[] splitInput = input.split(" ");
        boolean dukeIsOn = true;
        switch (splitInput[0]) {
            case "list":
                this.listAllTasks();
                break;
            case "bye":
                System.out.println("Bye. Hope to see you again soon!");
                dukeIsOn = false;
                saveDuke(this.filePath, this.saveInfo());
                break;
            case "done":
                this.checkTask(Integer.parseInt(splitInput[1]) - 1);
                break;
            case "todo":
                if (input.length() <= 4) {
                    throw (new EmptyTodoDscDukeException("todo task has empty description."));
                }
                this.addTask(input.substring(5), TaskType.TODO, printText);
                break;
            case "deadline":
                if (input.length() <= 8) {
                    throw (new EmptyDeadlineDscDukeException("deadline task has empty description."));
                }
                this.addTask(input.substring(9), TaskType.DEADLINE, printText);
                break;
            case "event":
                if (input.length() <= 5) {
                    throw (new EmptyEventDscDukeException("event task has empty description."));
                }
                this.addTask(input.substring(6), TaskType.EVENT, printText);
                break;
            case "delete":
                if (input.length() <= 6) {
                    throw new InvalidTaskIndexDukeException("No task number was given.");
                }
                this.deleteTask(Integer.parseInt(splitInput[1]) - 1);
                break;
            default:
                throw(new UnknownCmdDukeException(splitInput[0] + " is not a known command."));
        }
        return dukeIsOn;
    }

    private void saveDuke(String filePath, String stringToSave){
        try {
//            FileWriter fw = new FileWriter(filePath);
            File file = new File(filePath);
            FileWriter fw = new FileWriter(file);
            fw.write(stringToSave);
//            fw.write("abc 123");
            fw.close();

        } catch (IOException e) {
            System.out.println("Something went wrong! " + e.getMessage());
        }
    }

    void loadDuke(String filePath) {
        try {
            File f = new File(filePath);
            Scanner fileSc = new Scanner(f);
            String line;
            while (fileSc.hasNext()) {
                line = fileSc.nextLine();
                this.parseInput(line, false);
                if (Boolean.parseBoolean(fileSc.nextLine())) {
                    taskList.get(taskList.size() - 1).taskDone();
                }
            }
            System.out.println("Loaded saved task list!");
        } catch (FileNotFoundException e) {
            System.out.println("Did not find saved task list!");
        } catch (DukeException e) {
            System.out.println("Something went wrong! " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Something went wrong! " + e.getMessage());
        }
    }
}
