import { createContext, useState, useEffect, useContext } from "react";
import { AuthContext } from "./AuthContext";
import apiClient from "../api/client";

export const NotificationContext = createContext();

export function NotificationProvider({ children }) {
  const { user } = useContext(AuthContext);
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    if (!user || user.role !== "JOB_SEEKER") {
      setNotifications([]);
      return;
    }
    apiClient
      .get("/notifications")
      .then((res) => setNotifications(res.data.filter((n) => !n.seen)))
      .catch((err) => console.error("Failed to fetch notifications:", err));
  }, [user]);

  const markAsRead = async (id) => {
    await apiClient.put(`/notifications/${id}/read`, {});
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  return (
    <NotificationContext.Provider value={{ notifications, markAsRead }}>
      {children}
    </NotificationContext.Provider>
  );
}
