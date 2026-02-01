import java.util.*;

public class DeadlineTaskExecutorPresentation {

    // ==================== TASK CLASS ====================
    static class Task implements Comparable<Task> {
        String id;
        int duration;
        int deadline;
        int value;
        int timeWorked;

        public Task(String id, int duration, int deadline, int value) {
            this.id = id;
            this.duration = duration;
            this.deadline = deadline;
            this.value = value;
            this.timeWorked = 0;
        }

        // Check if task can finish by deadline
        public boolean canFinish(int currentTime) {
            return currentTime + duration <= deadline;
        }

        // For EDF: Compare by deadline (earliest first)
        @Override
        public int compareTo(Task other) {
            return Integer.compare(this.deadline, other.deadline);
        }

        @Override
        public String toString() {
            return String.format("📋 %s: Duration=%d, Deadline=%d, Value=%d",
                    id, duration, deadline, value);
        }
    }

    // ==================== TASK EXECUTOR ====================
    static class TaskExecutor {
        // REQUIRED DATA STRUCTURES
        PriorityQueue<Task> taskQueue;      // Min-Heap: Earliest Deadline First
        Queue<Task> expiredQueue;           // Queue: For expired tasks
        Stack<Task> undoStack;              // Stack: For undo operation
        List<Task> completedTasks;          // Array/List: Completed tasks

        private Task currentTask;
        private int currentTime;
        private int totalValue;

        public TaskExecutor() {
            System.out.println("🔄 Initializing Task Executor...");
            System.out.println("📊 Data Structures Created:");
            System.out.println("   ✓ Min-Heap (PriorityQueue) - For EDF scheduling");
            System.out.println("   ✓ Queue (LinkedList) - For expired tasks");
            System.out.println("   ✓ Stack - For undo functionality");
            System.out.println("   ✓ ArrayList - For completed tasks\n");

            taskQueue = new PriorityQueue<>();
            expiredQueue = new LinkedList<>();
            undoStack = new Stack<>();
            completedTasks = new ArrayList<>();
            currentTask = null;
            currentTime = 0;
            totalValue = 0;
        }

        // ==================== PUBLIC COMMANDS ====================

        public void addTask(String id, int duration, int deadline, int value) {
            Task task = new Task(id, duration, deadline, value);
            taskQueue.add(task);
            System.out.printf("✅ ADDED: Task %s | Duration: %d | Deadline: %d | Value: %d\n",
                    id, duration, deadline, value);
        }

        public void tick() {
            System.out.println("\n⏰ ===== TICK: Time Advances to " + currentTime + " =====");

            if (currentTask == null) {
                System.out.println("🤔 No current task. Looking for next task...");
                pickNextTask();
            }

            if (currentTask != null) {
                currentTask.timeWorked++;
                System.out.printf("⚡ WORKING: %s | Progress: %d/%d\n",
                        currentTask.id, currentTask.timeWorked, currentTask.duration);

                if (currentTask.timeWorked >= currentTask.duration) {
                    completeCurrentTask();
                }
            } else {
                System.out.println("😴 Idle - No tasks available");
            }

            currentTime++;
            printCurrentState();
        }

        public void runAll() {
            System.out.println("\n🚀 ===== RUNNING ALL TASKS =====");

            while (!taskQueue.isEmpty() || currentTask != null) {
                System.out.println("\n--- Time " + currentTime + " ---");

                if (currentTask == null) {
                    System.out.print("🔍 EDF Algorithm selecting... ");
                    pickNextTask();
                }

                if (currentTask != null) {
                    System.out.printf("⚡ Processing: %s (%d/%d)\n",
                            currentTask.id, currentTask.timeWorked, currentTask.duration);
                    currentTask.timeWorked++;

                    if (currentTask.timeWorked >= currentTask.duration) {
                        completeCurrentTask();
                    }
                }

                currentTime++;
            }

            System.out.println("\n🎯 ===== ALL TASKS COMPLETED =====");
        }

        public void report() {
            System.out.println("\n📈 ===== EXECUTION REPORT =====");
            System.out.println("⏰ Current Time: " + currentTime);
            System.out.println("💰 Total Value Earned: " + totalValue);

            System.out.println("\n✅ COMPLETED TASKS (" + completedTasks.size() + "):");
            if (completedTasks.isEmpty()) {
                System.out.println("   None");
            } else {
                for (Task task : completedTasks) {
                    System.out.println("   • " + task.id + " - Value: " + task.value);
                }
            }

            System.out.println("\n❌ EXPIRED TASKS (" + expiredQueue.size() + "):");
            if (expiredQueue.isEmpty()) {
                System.out.println("   None");
            } else {
                for (Task task : expiredQueue) {
                    System.out.println("   • " + task.id + " (Deadline: " + task.deadline + ")");
                }
            }

            System.out.println("\n⏳ PENDING TASKS (" + taskQueue.size() + "):");
            List<Task> pending = new ArrayList<>(taskQueue);
            Collections.sort(pending);
            for (Task task : pending) {
                System.out.println("   • " + task.id + " (Deadline: " + task.deadline + ")");
            }

            if (currentTask != null) {
                System.out.println("\n⚡ CURRENTLY EXECUTING:");
                System.out.println("   • " + currentTask.id + " - Progress: " +
                        currentTask.timeWorked + "/" + currentTask.duration);
            }

            System.out.println("================================\n");
        }

        public void undo() {
            if (!undoStack.isEmpty()) {
                Task last = undoStack.pop();
                System.out.println("↩️ UNDO: Reverting selection of " + last.id);
                if (currentTask != null && currentTask.id.equals(last.id)) {
                    currentTask.timeWorked = 0;
                    taskQueue.add(currentTask);
                    currentTask = null;
                }
            }
        }

        // ==================== PRIVATE HELPER METHODS ====================

        private void pickNextTask() {
            if (taskQueue.isEmpty()) {
                System.out.println("📭 Task queue is empty");
                currentTask = null;
                return;
            }

            Task candidate = taskQueue.poll();
            System.out.println("🎯 Candidate: " + candidate.id + " (Deadline: " + candidate.deadline + ")");

            // FEASIBILITY CHECK: Current Time + Duration <= Deadline ?
            if (candidate.canFinish(currentTime)) {
                System.out.println("✅ FEASIBLE: " + candidate.id + " can finish by deadline " + candidate.deadline);
                System.out.println("   Check: " + currentTime + " + " + candidate.duration + " = " +
                        (currentTime + candidate.duration) + " <= " + candidate.deadline);

                currentTask = candidate;
                undoStack.push(candidate);
                System.out.println("🚀 SELECTED: " + candidate.id + " for execution");
            } else {
                System.out.println("❌ EXPIRED: " + candidate.id + " cannot meet deadline!");
                System.out.println("   Check: " + currentTime + " + " + candidate.duration + " = " +
                        (currentTime + candidate.duration) + " > " + candidate.deadline);
                expiredQueue.add(candidate);
                pickNextTask(); // Try next task
            }
        }

        private void completeCurrentTask() {
            System.out.println("\n🎉 ===== TASK COMPLETED =====");
            System.out.println("✅ " + currentTask.id + " finished successfully!");
            System.out.println("💰 Value earned: +" + currentTask.value);

            completedTasks.add(currentTask);
            totalValue += currentTask.value;

            Task finished = currentTask;
            currentTask = null;

            if (!taskQueue.isEmpty()) {
                System.out.print("🔍 Looking for next task... ");
                pickNextTask();
            }
        }

        private void printCurrentState() {
            System.out.println("\n📊 CURRENT STATE:");
            System.out.println("   Time: " + currentTime);
            System.out.println("   Current Task: " + (currentTask != null ? currentTask.id : "None"));
            System.out.println("   Total Value: " + totalValue);
            System.out.println("   Queue Size: " + taskQueue.size());
            System.out.println("   Completed: " + completedTasks.size());
        }

        // ==================== DEMO METHODS ====================

        public void runDemo() {
            System.out.println("\n🎬 ===== STARTING DEMONSTRATION =====");
            System.out.println("📋 DEMO TASKS:");
            System.out.println("   1. T1: Duration=3, Deadline=5, Value=100");
            System.out.println("   2. T2: Duration=2, Deadline=4, Value=80");
            System.out.println("   3. T3: Duration=1, Deadline=10, Value=50");

            System.out.println("\n📥 Adding tasks to system...");
            addTask("T1", 3, 5, 100);
            addTask("T2", 2, 4, 80);
            addTask("T3", 1, 10, 50);

            System.out.println("\n📋 Initial Report:");
            report();

            System.out.println("\n🚀 Running simulation...");
            runAll();

            System.out.println("\n📊 Final Report:");
            report();
        }
    }

    // ==================== MAIN METHOD ====================
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskExecutor executor = new TaskExecutor();

        System.out.println("🎯 =======================================");
        System.out.println("   DEADLINE-AWARE TASK EXECUTOR");
        System.out.println("   Earliest Deadline First (EDF) Scheduler");
        System.out.println("========================================");
        System.out.println();

        System.out.println("Choose mode:");
        System.out.println("1. 📺 DEMO - Run example from problem");
        System.out.println("2. 🎮 INTERACTIVE - Enter commands manually");
        System.out.println("3. 🚀 AUTO - Run complete presentation");
        System.out.print("\nSelect (1-3): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                executor.runDemo();
                break;

            case "2":
                interactiveMode(executor, scanner);
                break;

            case "3":
                autoPresentation();
                break;

            default:
                System.out.println("Running demo mode...");
                executor.runDemo();
        }

        scanner.close();
    }

    private static void interactiveMode(TaskExecutor executor, Scanner scanner) {
        System.out.println("\n🎮 ===== INTERACTIVE MODE =====");
        System.out.println("Available Commands:");
        System.out.println("  ADD_TASK <id> <duration> <deadline> <value>");
        System.out.println("  TICK      - Advance time by 1 unit");
        System.out.println("  RUN_ALL   - Run until all tasks done");
        System.out.println("  REPORT    - Show current status");
        System.out.println("  UNDO      - Undo last selection");
        System.out.println("  EXIT      - Quit program");
        System.out.println();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("EXIT")) {
                System.out.println("👋 Goodbye!");
                break;
            }

            String[] parts = input.split("\\s+");
            String command = parts[0].toUpperCase();

            try {
                switch (command) {
                    case "ADD_TASK":
                        if (parts.length != 5) throw new Exception("Wrong format");
                        executor.addTask(parts[1],
                                Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3]),
                                Integer.parseInt(parts[4]));
                        break;

                    case "TICK":
                        executor.tick();
                        break;

                    case "RUN_ALL":
                        executor.runAll();
                        break;

                    case "REPORT":
                        executor.report();
                        break;

                    case "UNDO":
                        executor.undo();
                        break;

                    default:
                        System.out.println("❌ Unknown command");
                }
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    private static void autoPresentation() {
        System.out.println("\n🎬 ===== AUTO PRESENTATION =====");
        TaskExecutor executor = new TaskExecutor();

        // Slide 1: System Introduction
        System.out.println("\n📖 SLIDE 1: SYSTEM OVERVIEW");
        System.out.println("• Earliest Deadline First (EDF) scheduling");
        System.out.println("• Tasks discarded if cannot meet deadline");
        System.out.println("• Maximizes total value of completed tasks\n");

        waitForEnter();

        // Slide 2: Data Structures
        System.out.println("\n📖 SLIDE 2: DATA STRUCTURES USED");
        System.out.println("• Min-Heap (PriorityQueue) - For EDF ordering");
        System.out.println("• Queue - For expired tasks");
        System.out.println("• Stack - For undo functionality");
        System.out.println("• Array/List - For completed tasks\n");

        waitForEnter();

        // Slide 3: Example Tasks
        System.out.println("\n📖 SLIDE 3: EXAMPLE TASKS");
        System.out.println("Task T1: Duration=3, Deadline=5, Value=100");
        System.out.println("Task T2: Duration=2, Deadline=4, Value=80");
        System.out.println("Task T3: Duration=1, Deadline=10, Value=50\n");

        waitForEnter();

        // Add tasks
        System.out.println("\n📥 ADDING TASKS TO SYSTEM...");
        executor.addTask("T1", 3, 5, 100);
        executor.addTask("T2", 2, 4, 80);
        executor.addTask("T3", 1, 10, 50);

        waitForEnter();

        // Initial state
        System.out.println("\n📊 INITIAL STATE:");
        executor.report();

        waitForEnter();

        // Run simulation step by step
        System.out.println("\n🚀 SIMULATION STEP-BY-STEP:");
        for (int i = 0; i < 10; i++) {
            executor.tick();
            waitForEnter();
        }

        // Final results
        System.out.println("\n🎯 FINAL RESULTS:");
        executor.report();

        System.out.println("🎉 PRESENTATION COMPLETE!");
    }

    private static void waitForEnter() {
        try {
            System.out.print("Press Enter to continue...");
            System.in.read();
        } catch (Exception e) {
            // Continue anyway
        }
    }
}