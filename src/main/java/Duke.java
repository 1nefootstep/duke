import java.text.ParseException;
import java.util.Scanner;

public class Duke {
    public static void main(String[] args) {
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
//        System.out.println("Hello from\n" + logo);
        Scanner sc = new Scanner(System.in);
        String input;
        boolean dukeIsOn = true;
        String filePath = "../data/savedList.txt";
        ToDoList myTasks = new ToDoList(filePath);
        myTasks.loadDuke(filePath);
        System.out.println("Hello! I'm Duke");
        System.out.println("What can I do for you?");

        while(dukeIsOn){
            input = sc.nextLine().trim();
            try {
                dukeIsOn = myTasks.parseInput(input, true);
            } catch (UnknownCmdDukeException e){
                System.out.println("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
            } catch (EmptyTodoDscDukeException e) {
                System.out.println("☹ OOPS!!! The description of a todo cannot be empty.");
            } catch (EmptyDeadlineDscDukeException e) {
                System.out.println("☹ OOPS!!! The description of a deadline cannot be empty.");
            } catch (EmptyEventDscDukeException e) {
                System.out.println("☹ OOPS!!! The description of a event cannot be empty.");
            } catch (NoDateDukeException e) {
                System.out.println("☹ OOPS!!! You need to provide a date, with / to indicate it:-(");
            } catch (InvalidTaskIndexDukeException e) {
                System.out.println("☹ OOPS!!! You need to provide a valid task number :-(");
            } catch (NumberFormatException e) {
                System.out.println("☹ OOPS!!! You need to provide a valid number :-(");
            } catch (DukeException e) {
                System.out.println("☹ OOPS!!! Something went wrong" + e.getMessage());
            } catch (ParseException e) {
                System.out.println("Something went wrong! " + e.getMessage());
            }
        }
    }

}
