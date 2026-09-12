import { useEffect, useState, useRef, useContext } from "react";
import { FaBell } from "react-icons/fa";
import { formatDistanceToNow } from "date-fns";
import { NotificationContext } from "../context/NotificationContext";
import { getLogoFallback, handleLogoError } from "../utils/companyLogo";

export default function NotificationBell() {
  const { notifications, markAsRead } = useContext(NotificationContext);
  const [showDropdown, setShowDropdown] = useState(false);
  const bellRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (bellRef.current && !bellRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const toggleDropdown = () => setShowDropdown(!showDropdown);

  return (
    <div className="relative" ref={bellRef}>
      <button
        className="relative cursor-pointer bg-transparent border-0 p-0 inline-flex"
        onClick={toggleDropdown}
        aria-label="Open notifications"
      >
        <FaBell className="text-lg hover:text-[#6F4E37] transition" title="Notifications" />
        {notifications.length > 0 && (
          <span className="absolute -top-2 -right-2 bg-red-600 text-white text-xs font-bold px-1.5 py-0.5 rounded-full animate-pulse">
            {notifications.length}
          </span>
        )}
      </button>

      {showDropdown && (
        <div style={{ position: 'absolute', right: 0, top: '100%', marginTop: '8px', width: '380px', maxHeight: '450px', overflowY: 'auto', backgroundColor: 'white', border: '1px solid #e5e7eb', borderRadius: '12px', boxShadow: '0 10px 25px rgba(0,0,0,0.15)', zIndex: 9999 }}>
          <div className="p-4 font-semibold text-gray-800 border-b text-base">
            Notifications
          </div>

          {notifications.length === 0 ? (
            <div className="p-4 text-sm text-gray-500 text-center">
              No new notifications
            </div>
          ) : (
            <div className="p-2 space-y-2">
              {notifications.map((n) => (
                <div
                  key={n.id}
                  className="flex gap-3 p-3 bg-white rounded-lg shadow-sm hover:shadow-md transition"
                >
                  <img
                    src={n.companyLogoUrl || getLogoFallback()}
                    onError={(e) => handleLogoError(e)}
                    alt="Company Logo"
                    className="w-10 h-10 rounded-full object-contain border"
                  />
                  <div className="flex-1">
                    <p className="text-sm text-gray-800 leading-snug">{n.message}</p>
                    <p className="text-xs text-gray-400 mt-1">
                      {n.createdAt
                        ? formatDistanceToNow(new Date(n.createdAt), { addSuffix: true })
                        : ""}
                    </p>
                  </div>
                  <button
                    className="text-xs text-[#6F4E37] hover:underline self-start"
                    onClick={async () => { try { await markAsRead(n.id); } catch {} }}
                  >
                    Mark read
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
