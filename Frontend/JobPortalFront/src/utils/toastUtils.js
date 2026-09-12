import { toast } from "react-toastify";

export const successToast = (msg) =>
  toast.success(msg, {
    icon: "✅",
    style: {
      background: "#F0FDF4",
      color: "#15803D",
      borderLeft: "5px solid #22C55E",
      borderRadius: "8px",
      boxShadow: "0 4px 10px rgba(0, 0, 0, 0.08)",
    },
  });

  export const errorToast = (msg) =>
  toast.error(msg, {
    icon: "❌",
    style: {
      background: "#FEF2F2",
      color: "#991B1B",
      borderLeft: "5px solid #EF4444",
      borderRadius: "8px",
      boxShadow: "0 4px 10px rgba(0, 0, 0, 0.08)",
    },
  })
  
export const infoToast = (msg) =>
  toast.info(msg, {
    icon: "ℹ️",
    style: {
      background: "#FDF9F7",
      color: "#6B3F27",
      borderLeft: "5px solid #6B3F27",
      borderRadius: "8px",
      boxShadow: "0 4px 10px rgba(0, 0, 0, 0.08)",
    },
  });
