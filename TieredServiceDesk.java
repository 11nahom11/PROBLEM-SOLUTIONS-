import java.util.*;

public class TieredServiceDesk {

    enum Tier {
        PLATINUM(0), GOLD(1), SILVER(2);

        int index;
        Tier(int i) { index = i; }

        public static Tier fromString(String s) {
            return Tier.valueOf(s.toUpperCase());
        }
    }

    private Queue<String>[] queues;
    private int[] quotas = {3, 2, 1};      
    private int[] servedCount = {0, 0, 0}; 
    private int currentTier = 0;           

    @SuppressWarnings("unchecked")
    public TieredServiceDesk() {
        queues = new Queue[3];
        for (int i = 0; i < 3; i++) {
            queues[i] = new LinkedList<>();
        }
    }


    public void arrive(String user, Tier tier) {
        queues[tier.index].offer(user);
        System.out.println(user + " added to " + tier);
    }


    public void processNext() {
        for (int attempts = 0; attempts < 3; attempts++) {

            if (servedCount[currentTier] >= quotas[currentTier] ||
                queues[currentTier].isEmpty()) {

                moveToNextTier();
                continue;
            }

            String user = queues[currentTier].poll();
            servedCount[currentTier]++;
            System.out.println(user + " (" + tierName(currentTier) +
                               " #" + servedCount[currentTier] + ")");
            return;
        }

        System.out.println("No customers to process.");
    }

    private void moveToNextTier() {
        currentTier = (currentTier + 1) % 3;

        if (currentTier == 0) {
            Arrays.fill(servedCount, 0);
        }
    }

    public void status() {
        System.out.println("---- Queue Status ----");
        System.out.println("Platinum: " + queues[0]);
        System.out.println("Gold    : " + queues[1]);
        System.out.println("Silver  : " + queues[2]);
    }

    private String tierName(int index) {
        return Tier.values()[index].name();
    }


    public static void main(String[] args) {
        TieredServiceDesk desk = new TieredServiceDesk();
        Scanner sc = new Scanner(System.in);

        while (true) {
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("EXIT")) break;

            String[] parts = line.split(" ");

            switch (parts[0].toUpperCase()) {
                case "ARRIVE":
                    desk.arrive(parts[1], Tier.fromString(parts[2]));
                    break;

                case "PROCESS_NEXT":
                    desk.processNext();
                    break;

                case "STATUS":
                    desk.status();
                    break;

                default:
                    System.out.println("Invalid command");
            }
        }

        sc.close();
    }
}
