package edugrade.app;

import edugrade.model.MataKuliah;
import edugrade.model.User;

public final class AppSession {
    private static MataKuliah activeMataKuliah;
    private static User currentUser;

    private AppSession() {
    }

    public static MataKuliah getActiveMataKuliah() {
        return activeMataKuliah;
    }

    public static void setActiveMataKuliah(MataKuliah mataKuliah) {
        activeMataKuliah = mataKuliah;
    }

    public static void clearActiveMataKuliah() {
        activeMataKuliah = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
