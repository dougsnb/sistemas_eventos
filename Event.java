import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Event implements Serializable {
    private String name;
    private LocalDateTime dateTime;
    private String location;
    private String description;
    private Set<User> participants = new HashSet<>();
    private static final int MAX_PARTICIPANTS = 100;

    public Event(String name, LocalDateTime dateTime, String location, String description) {
        this.name = name;
        this.dateTime = dateTime;
        this.location = location;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public boolean addParticipant(User user) {
        if (participants.size() < MAX_PARTICIPANTS) {
            return participants.add(user);
        }
        return false;
    }

    public boolean removeParticipant(User user) {
        return participants.remove(user);
    }

    public static Event getEventByName(List<Event> events, String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return event;
            }
        }
        return null;
    }
}
