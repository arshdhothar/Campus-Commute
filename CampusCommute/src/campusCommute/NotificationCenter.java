package campusCommute;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * NotificationCenter – clean observer-pattern system for UI alerts.
 * Works with NEW backend-based Ride + RideRequest structure.
 */
public class NotificationCenter {

    private static final NotificationCenter INSTANCE = new NotificationCenter();
    public static NotificationCenter getInstance() { return INSTANCE; }

    private final List<Consumer<String>> listeners = new ArrayList<>();

    private NotificationCenter() {}

    // ------------------------------
    //   SUBSCRIPTIONS
    // ------------------------------
    public void subscribe(Consumer<String> listener) {
        listeners.add(listener);
    }

    public void subscribeUI(JFrame parent) {
        subscribe(msg -> {
            System.out.println("[Notification] " + msg);

            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(
                            parent,
                            msg,
                            "CampusCommute Notification",
                            JOptionPane.INFORMATION_MESSAGE
                    )
            );
        });
    }

    // ------------------------------
    //   INTERNAL EMITTER
    // ------------------------------
    private void emit(String msg) {
        for (Consumer<String> listener : listeners) {
            listener.accept(msg);
        }
    }

    // ------------------------------
    //   NOTIFICATION EVENTS
    // ------------------------------

    /** Passenger sends request to a ride */
    public void notifyDriverNewRequest(String driverName, String passengerName, Ride ride) {
        emit("New ride request: " + passengerName +
                " wants to join your ride from " +
                ride.getStartLocation() + " → " + ride.getDestination());
    }

    /** Driver approves request */
    public void notifyRideApproved(String passengerName, Ride ride) {
        emit("Request approved: " + passengerName +
                " has been accepted for your ride (" +
                ride.getStartLocation() + " → " + ride.getDestination() + ")");
    }

    /** Driver confirms to passenger */
    public void notifyDriverConfirmation(String driverName, String passengerName, Ride ride) {
        emit("Driver " + driverName + " has approved your request for the ride: " +
                ride.getStartLocation() + " → " + ride.getDestination());
    }

    /** Driver declines request */
    public void notifyRideDeclined(String passengerName, Ride ride) {
        emit("Ride request declined: " + passengerName +
                " was not approved for the ride (" +
                ride.getStartLocation() + " → " + ride.getDestination() + ")");
    }

    /** Passenger cancels their request */
    public void notifyRequestCancelled(String driverName, String passengerName, Ride ride) {
        emit("Request cancelled: " + passengerName +
                " cancelled their request for your ride (" +
                ride.getStartLocation() + " → " + ride.getDestination() + ")");
    }
}
