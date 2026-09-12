import { useEffect, useContext, useRef } from "react";
import { toast } from "react-toastify";
import { AuthContext } from "../context/AuthContext";
import { NotificationContext } from "../context/NotificationContext";
import CustomNotificationToast from "./CustomNotificationToast";

export default function NotificationToastManager() {
  const { user } = useContext(AuthContext);
  const { notifications } = useContext(NotificationContext);
  const hasShownToasts = useRef(false);

  useEffect(() => {
    if (!user) {
      hasShownToasts.current = false;
      return;
    }
    if (user.role !== "JOB_SEEKER") return;
    if (notifications.length === 0 || hasShownToasts.current) return;

    const unread = [...notifications];
    let index = 0;

    const showNext = () => {
      hasShownToasts.current = true;
      if (index >= unread.length) return;
      const n = unread[index++];
      const toastId = `notif-${n.id}`;

      toast(
        <CustomNotificationToast notification={n} toastId={toastId} />,
        {
          toastId,
          autoClose: 5000,
          closeButton: false,
          position: "top-right",
          hideProgressBar: true,
          style: {
            background: "transparent",
            boxShadow: "none",
            padding: 0,
            maxWidth: "100vw",
            width: "auto",
          },
        }
      );

      setTimeout(showNext, 5500);
    };

    const timer = setTimeout(showNext, 500);
    return () => clearTimeout(timer);
  }, [notifications, user]);

  return null;
}
