import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static UserManager instance;
    private Set<String> registeredUsers;
    private String currentUser;
    private Map<String, Set<String>> userSubscriptions; // Usuario -> Set de distribuidoras
    
    private UserManager() {
        registeredUsers = new HashSet<>();
        userSubscriptions = new HashMap<>();
        currentUser = null;
    }
    
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    public boolean login(String username) {
        if (username != null && !username.trim().isEmpty()) {
            registeredUsers.add(username.trim());
            currentUser = username.trim();
            
            // Inicializar suscripciones para usuario nuevo
            if (!userSubscriptions.containsKey(currentUser)) {
                userSubscriptions.put(currentUser, new HashSet<>());
            }
            
            return true;
        }
        return false;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public Set<String> getRegisteredUsers() {
        return new HashSet<>(registeredUsers);
    }
    
    public Set<String> getCurrentUserSubscriptions() {
        if (currentUser == null) {
            return new HashSet<>();
        }
        return new HashSet<>(userSubscriptions.getOrDefault(currentUser, new HashSet<>()));
    }
    
    public void subscribeCurrentUserToDistributor(String distributor) {
        if (currentUser != null) {
            userSubscriptions.computeIfAbsent(currentUser, k -> new HashSet<>()).add(distributor);
        }
    }
    
    public void unsubscribeCurrentUserFromDistributor(String distributor) {
        if (currentUser != null) {
            Set<String> subscriptions = userSubscriptions.get(currentUser);
            if (subscriptions != null) {
                subscriptions.remove(distributor);
            }
        }
    }
    
    public boolean isCurrentUserSubscribedTo(String distributor) {
        if (currentUser == null) {
            return false;
        }
        return userSubscriptions.getOrDefault(currentUser, new HashSet<>()).contains(distributor);
    }
}
