package org.rabbit.common.contains;

import java.util.Arrays;
import java.util.List;

/**
 * All constants.
 *
 * @author nine
 */
public class Constants {

    public static class Auth {

        public static final String AUTH_ENABLED = "authentication.enabled";
        public static final String AUTH_2FA_ENABLED = "authentication.2fa.enable";
        public static final String AUTH_2FA_EXPIRATION_MINUTES = "authentication.2fa.expiration.minutes";
        public static final String AUTH_ROOT_URL = "authentication.root.url";
        public static final String AUTH_LOGIN_URI = "authentication.login.uri";
        public static final String AUTH_PUBLIC_URL = "authentication.public.url";
        public static final String AUTH_REFRESH_TOKEN_URI = "authentication.refresh.token.uri";
    }

    public static class Jwt {

        public static final String SECRET = "jwt.secret";
        public static final String EXPIRATION_MINUTES = "jwt.expiration.minutes";
        public static final String REFRESH_EXPIRATION_MINUTES = "jwt.refresh.expiration.minutes";
    }


    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");

    public static class StatusValue {
        /** D: DELETE */
        public static final String DELETE = "D";
        /** D: Deactivated */
        public static final String DEACTIVATED = "D";
        /** PR: pending active */
        public static final String PENDING_ACTIVE = "P";
        /** A: Active */
        public static final String ACTIVE = "A";
        /** R: Removed */
        public static final String REMOVED = "R";
        /** PR: pending removed to leave */
        public static final String PENDING_REMOVED = "L";
    }


    /**
     * Verify the correctness of the status value
     *
     * @param status the status abbreviation
     * @return return true if correct else false
     */
    public static boolean isCorrectStatusValue(String status) {
        List<String> states = Arrays.asList(StatusValue.DEACTIVATED, StatusValue.PENDING_ACTIVE, StatusValue.ACTIVE,
                StatusValue.REMOVED, StatusValue.PENDING_REMOVED);
        return states.contains(status);
    }

    public static class Workflow {
        public static final String DOCUMENT_ID = "documentId";
        public static final String CREATOR_ID = "user_creator_id";
        public static final String APPROVER_ID = "user_approver_id";
        public static final String COPY_PERSONNEL_ID = "user_copy_personnel_id";

    }

    public static class TimeUnit {
        /** Y: Year */
        public static final String YEAR = "Y";
        /** M: Month */
        public static final String MONTH = "M";
        /** D : Day */
        public static final String DAY = "D";
    }

    public static class PolicyEvent {
        public static final String NOW = "now";
        public static final String EXTENT = "extent";
    }
}
