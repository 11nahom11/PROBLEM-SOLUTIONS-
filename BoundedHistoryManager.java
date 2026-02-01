import java.util.*;

public class BoundedHistoryManager {

    private static Deque<String> history = new ArrayDeque<>();
    private static int MAX_HISTORY = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            String input = sc.nextLine().trim();
            String[] parts = input.split(" ");

            String command = parts[0];

            switch (command) {

                case "SET_LIMIT":
                    MAX_HISTORY = Integer.parseInt(parts[1]);
                    history.clear();
                    System.out.println("History limit set to " + MAX_HISTORY);
                    break;

                case "ACTION":
                    String action = parts[1];

                    if (history.size() == MAX_HISTORY) {
                        String removed = history.removeFirst();
                        System.out.println("Full. Dropped " + removed + ".");
                    }

                    history.addLast(action);
                    break;

                case "UNDO":
                    if (history.isEmpty()) {
                        System.out.println("Nothing to undo.");
                    } else {
                        String undone = history.removeLast();
                        System.out.println("Reverted " + undone + ".");
                    }
                    break;

                case "SHOW_HISTORY":
                    System.out.println("Hist: " + history);
                    break;

                case "EXIT":
                    System.out.println("Exiting...");
                    return;

                default:
                    System.out.println("Invalid command");
            }
        }
    }
}
