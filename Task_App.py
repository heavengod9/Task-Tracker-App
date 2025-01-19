import json
import os
from datetime import datetime

class TaskManager:
    def __init__(self, file_name="tasks.json"):
        self.file_name = file_name
        if not os.path.exists(self.file_name):
            self.save_tasks([])

    def load_tasks(self):
        with open(self.file_name, "r") as file:
            return json.load(file)

    def save_tasks(self, tasks):
        with open(self.file_name, "w") as file:
            json.dump(tasks, file, indent=4)

    def generate_task_id(self, tasks):
        return max((task["id"] for task in tasks), default=0) + 1

    def add_task(self, description):
        tasks = self.load_tasks()
        task = {
            "id": self.generate_task_id(tasks),
            "description": description,
            "status": "todo",
            "createdAt": datetime.now().isoformat(),
            "updatedAt": datetime.now().isoformat()
        }
        tasks.append(task)
        self.save_tasks(tasks)
        return task

    def update_task(self, task_id, description):
        tasks = self.load_tasks()
        for task in tasks:
            if task["id"] == task_id:
                task["description"] = description
                task["updatedAt"] = datetime.now().isoformat()
                self.save_tasks(tasks)
                return task
        return None

    def delete_task(self, task_id):
        tasks = self.load_tasks()
        updated_tasks = [task for task in tasks if task["id"] != task_id]
        self.save_tasks(updated_tasks)
        return len(tasks) != len(updated_tasks)

    def mark_task(self, task_id, status):
        tasks = self.load_tasks()
        for task in tasks:
            if task["id"] == task_id:
                task["status"] = status
                task["updatedAt"] = datetime.now().isoformat()
                self.save_tasks(tasks)
                return task
        return None

    def list_tasks(self, status=None):
        tasks = self.load_tasks()
        if status:
            return [task for task in tasks if task["status"] == status]
        return tasks

class TaskCLI:
    def __init__(self):
        self.manager = TaskManager()

    def show_message(self, message):
        print(message)

    def display_tasks(self, tasks):
        if not tasks:
            print("No tasks found.")
        else:
            for task in tasks:
                print(f"ID: {task['id']} | Description: {task['description']} | Status: {task['status']} | "
                      f"Created At: {task['createdAt']} | Updated At: {task['updatedAt']}")

    def handle_command(self, command, args):
        if command == "add":
            if not args:
                self.show_message("Usage: add <description>")
                return
            task = self.manager.add_task(" ".join(args))
            self.show_message(f"Task added successfully (ID: {task['id']})")

        elif command == "update":
            if len(args) < 2:
                self.show_message("Usage: update <id> <description>")
                return
            task = self.manager.update_task(int(args[0]), " ".join(args[1:]))
            if task:
                self.show_message("Task updated successfully")
            else:
                self.show_message("Task not found")

        elif command == "delete":
            if not args:
                self.show_message("Usage: delete <id>")
                return
            success = self.manager.delete_task(int(args[0]))
            self.show_message("Task deleted successfully" if success else "Task not found")

        elif command in ["mark-in-progress", "mark-done"]:
            if not args:
                self.show_message(f"Usage: {command} <id>")
                return
            status = "in-progress" if command == "mark-in-progress" else "done"
            task = self.manager.mark_task(int(args[0]), status)
            if task:
                self.show_message(f"Task marked as {status}")
            else:
                self.show_message("Task not found")

        elif command == "list":
            status = args[0] if args else None
            if status not in [None, "todo", "in-progress", "done"]:
                self.show_message("Invalid status. Use 'todo', 'in-progress', or 'done'.")
                return
            tasks = self.manager.list_tasks(status)
            self.display_tasks(tasks)

        else:
            self.show_message("Unknown command")

def main():
    import sys
    cli = TaskCLI()

    if len(sys.argv) < 2:
        cli.show_message("Usage: task-cli <command> [options]")
        return

    command = sys.argv[1]
    args = sys.argv[2:]
    cli.handle_command(command, args)

if __name__ == "__main__":
    main()
