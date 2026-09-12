import { useContext, useEffect, useState } from "react";
import { AuthContext } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import defaultAvatar from "../../pics/deafult-avatar.png";
import { FaBell, FaBars, FaTimes } from "react-icons/fa";

export default function NavbarEmployer() {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
  }, [user]);

  const handleLogout = () => {
    logout();
    toast.success("Logged out successfully!");
    navigate("/");
  };

  const profileImageUrl = user?.profilePicture || defaultAvatar;

  const closeMenu = () => setMenuOpen(false);

  return (
    <nav className="w-full px-6 py-4 bg-white shadow-sm border-b sticky top-0 z-50">
      <div className="flex justify-between items-center">

        {/* LEFT - Logo */}
        <span
          className="text-xl font-bold text-black cursor-pointer"
          onClick={() => navigate("/dashboard")}
        >
          JobPort
        </span>

        {/* CENTER - Desktop navigation */}
        <div className="hidden md:flex gap-6 text-sm font-medium text-gray-800 items-center">
          <span>Welcome back, <strong>{user.companyName}</strong></span>
          <span className="cursor-pointer hover:text-black" onClick={() => navigate("/dashboard")}>Dashboard</span>
          <span className="cursor-pointer hover:text-black" onClick={() => navigate("/employer/jobs")}>My Jobs</span>
          <span className="cursor-pointer hover:text-black" onClick={() => navigate("/create-job")}>Post Job</span>
        </div>

        {/* RIGHT - Bell, Avatar, Logout, Hamburger */}
        <div className="flex items-center gap-3 text-gray-700">
          <FaBell className="cursor-pointer text-lg hover:text-black" title="Notifications" />
          <img
            src={profileImageUrl}
            alt="Profile"
            className="w-8 h-8 rounded-full border object-contain bg-white p-0.5"
            title="Company Profile"
            onClick={() => navigate("/employer/profile")}
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = defaultAvatar;
            }}
          />
          <button
            onClick={handleLogout}
            className="px-3 py-1.5 border rounded hover:bg-gray-100 text-sm"
          >
            Logout
          </button>
          <button
            className="md:hidden text-xl p-1 ml-1"
            onClick={() => setMenuOpen(!menuOpen)}
            aria-label="Toggle menu"
          >
            {menuOpen ? <FaTimes /> : <FaBars />}
          </button>
        </div>
      </div>

      {/* Mobile dropdown */}
      {menuOpen && (
        <div className="md:hidden mt-3 border-t pt-3 flex flex-col gap-4 text-sm font-medium text-gray-800">
          <span className="text-gray-500 text-xs">Welcome, <strong>{user.companyName}</strong></span>
          <span className="cursor-pointer hover:text-black" onClick={() => { navigate("/dashboard"); closeMenu(); }}>Dashboard</span>
          <span className="cursor-pointer hover:text-black" onClick={() => { navigate("/employer/jobs"); closeMenu(); }}>My Jobs</span>
          <span className="cursor-pointer hover:text-black" onClick={() => { navigate("/create-job"); closeMenu(); }}>Post Job</span>
        </div>
      )}
    </nav>
  );
}
