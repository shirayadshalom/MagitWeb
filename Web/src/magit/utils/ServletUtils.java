package magit.utils;

import engine.*;
import users.UserManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;


public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
    private static final String MAGIT_MANAGER_ATTRIBUTE_NAME = "magitManager";
    private static final String NOTIFICATIONS_MANAGER_ATTRIBUTE_NAME = "notificationsManager";
    private static final String PR_MANAGER_ATTRIBUTE_NAME = "prManager";
    //private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";

    private static final Object userManagerLock = new Object();
    private static final Object magitManagerLock = new Object();
    private static final Object chatManagerLock = new Object();
    private static final Object notificationManagerLock = new Object();
    private static final Object prManagerLock = new Object();
   // private static final Object chatManagerLock = new Object();



    public static ChatManager getChatManager(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
            }
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }

    public static PRManager getPRManager(ServletContext servletContext) {
        synchronized (prManagerLock) {
            if (servletContext.getAttribute(PR_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(PR_MANAGER_ATTRIBUTE_NAME, new PRManager());
            }
        }
        return (PRManager) servletContext.getAttribute(PR_MANAGER_ATTRIBUTE_NAME);
    }

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static NotificationsManager getNotificationsManager(ServletContext servletContext) {

        synchronized (notificationManagerLock) {
            if (servletContext.getAttribute(NOTIFICATIONS_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(NOTIFICATIONS_MANAGER_ATTRIBUTE_NAME, new NotificationsManager());
            }
        }
        return (NotificationsManager) servletContext.getAttribute(NOTIFICATIONS_MANAGER_ATTRIBUTE_NAME);
    }

    public static MagitManager getMagitManager(ServletContext servletContext) {

        synchronized (magitManagerLock) {
            if (servletContext.getAttribute(MAGIT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(MAGIT_MANAGER_ATTRIBUTE_NAME, new MagitManager());
            }
        }
        return (MagitManager) servletContext.getAttribute(MAGIT_MANAGER_ATTRIBUTE_NAME);
    }

   /* public static ChatManager getChatManager(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
            }
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }

    */

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return Integer.MIN_VALUE;
    }
}