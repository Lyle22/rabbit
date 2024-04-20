package org.rabbit.common.contains;

/**
 * License Configs
 *
 * @author nine
 */
public class LicenseConfig {

    private volatile static LicenseConfig singleton;
    /**
     * Definition: Total number of active users within the certificate
     */
    public volatile int totalUsers = 0;
    /**
     * The number of terminals that allow customers to login successfully
     */
    public volatile int currentSession = 1;

    private LicenseConfig() {
    }

    public static LicenseConfig getSingleton() {
        if (singleton == null) {
            synchronized (LicenseConfig.class) {
                if (singleton == null) {
                    singleton = new LicenseConfig();
                }
            }
        }
        return singleton;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setCurrentSession(int currentSession) {
        this.currentSession = currentSession;
    }
}
