import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Distributor {
    private static List<String> availableDistributors = new ArrayList<>();
    private static Set<DistributorListener> listeners = new HashSet<>();

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
            // Notificar a los listeners
            for (DistributorListener l : listeners) {
                l.onDistributorsChanged();
            }
        }
    }

    public static boolean isValidDistributor(String distributor) {
        return availableDistributors.contains(distributor);
    }

    // Métodos para registrar y quitar listeners
    public static void addListener(DistributorListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(DistributorListener listener) {
        listeners.remove(listener);
    }
}
