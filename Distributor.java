import java.util.ArrayList;
import java.util.List;

public class Distributor {
    private static List<String> availableDistributors = new ArrayList<>();
    
    static {
        // Distribuidoras predefinidas
        availableDistributors.add("Netflix");
        availableDistributors.add("Disney");
        availableDistributors.add("Warner Bros");
        availableDistributors.add("Universal");
        availableDistributors.add("Sony Pictures");
        availableDistributors.add("Paramount");
    }
    
    public static List<String> getAvailableDistributors() {
        return new ArrayList<>(availableDistributors);
    }
    
    public static void addDistributor(String distributor) {
        if (!availableDistributors.contains(distributor)) {
            availableDistributors.add(distributor);
        }
    }
    
    public static boolean isValidDistributor(String distributor) {
        return availableDistributors.contains(distributor);
    }
}
