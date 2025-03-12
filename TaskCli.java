import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Scanner;

public class TaskTrackerCLI {

    private static final String FILE_NAME = "tasks.json";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a command.");
            return;
        }

        String command = args[0];
        switch (command) {
            case "add":
                if (args.length < 2) {
                    System.out.println("Usage: task-cli add \"Task Description\"");
                } else {
                    addTask(args[1]);
                }
                break;
            case "update":
                if (args.length < 3) {
                    System.out.println("Usage: task-cli update <ID> \"New Description\"");
                } else {
                    updateTask(Integer.parseInt(args[1]), args[2]);
                }
                break;
            case "delete":
                if (args.length < 2) {
                    System.out.println("Usage: task-cli delete <ID>");
                } else {
                    deleteTask(Integer.parseInt(args[1]));
                }
                break;
            case "mark-in-progress":
                if (args.length < 2) {
                    System.out.println("Usage: task-cli mark-in-progress <ID>");
                } else {
                    updateStatus(Integer.parseInt(args[1]), "in-progress");
                }
                break;
            case "mark-done":
                if (args.length < 2) {
                    System.out.println("Usage: task-cli mark-done <ID>");
                } else {
                    updateStatus(Integer.parseInt(args[1]), "done");
                }
                break;
            case "list":
                if (args.length == 1) {
                    listTasks(null);
                } else {
                    listTasks(args[1]);
                }
                break;
            default:
                System.out.println("Invalid command.");
        }
    }

    private static void addTask(String description) {
        JSONArray tasks = loadTasks();
        JSONObject task = new JSONObject();
        int id = tasks.length() + 1;
        task.put("id", id);
        task.put("description", description);
        task.put("status", "todo");
        task.put("createdAt", LocalDateTime.now().toString());
        task.put("updatedAt", LocalDateTime.now().toString());
        tasks.put(task);
        saveTasks(tasks);
        System.out.println("Task added successfully (ID: " + id + ")");
    }

    private static void updateTask(int id, String newDescription) {
        JSONArray tasks = loadTasks();
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            if (task.getInt("id") == id) {
                task.put("description", newDescription);
                task.put("updatedAt", LocalDateTime.now().toString());
                saveTasks(tasks);
                System.out.println("Task updated successfully.");
                return;
            }
        }
        System.out.println("Task with ID " + id + " not found.");
    }

    private static void deleteTask(int id) {
        JSONArray tasks = loadTasks();
        for (int i = 0; i < tasks.length(); i++) {
            if (tasks.getJSONObject(i).getInt("id") == id) {
                tasks.remove(i);
                saveTasks(tasks);
                System.out.println("Task deleted successfully.");
                return;
            }
        }
        System.out.println("Task with ID " + id + " not found.");
    }

    private static void updateStatus(int id, String status) {
        JSONArray tasks = loadTasks();
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            if (task.getInt("id") == id) {
                task.put("status", status);
                task.put("updatedAt", LocalDateTime.now().toString());
                saveTasks(tasks);
                System.out.println("Task status updated to " + status + ".");
                return;
            }
        }
        System.out.println("Task with ID " + id + " not found.");
    }

    private static void listTasks(String status) {
        JSONArray tasks = loadTasks();
        for (int i = 0; i < tasks.length(); i++) {
            JSONObject task = tasks.getJSONObject(i);
            if (status == null || task.getString("status").equalsIgnoreCase(status)) {
                System.out.println(task);
            }
        }
    }

    private static JSONArray loadTasks() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                return new JSONArray();
            }
            String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
            return new JSONArray(content);
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
            return new JSONArray();
        }
    }

    private static void saveTasks(JSONArray tasks) {
        try (FileWriter file = new FileWriter(FILE_NAME)) {
            file.write(tasks.toString(4));
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }
}
