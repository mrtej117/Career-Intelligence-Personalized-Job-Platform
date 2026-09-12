import { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../../context/AuthContext';
import { FaBell, FaEnvelope, FaBookmark, FaBars, FaTimes } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import defaultAvatar from '../../pics/deafult-avatar.png';
import NotificationBell from "../NotificationBell";

export default function Navbar({ onOpenJobSeeker, onOpenEmployer }) {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    if (user && !user.profilePicture) {
      console.warn("⚠️ User has no profilePicture — using default avatar");
    }
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

        {/* LEFT - Logo + desktop nav links */}
        <div className="flex items-center gap-6 text-sm font-medium text-gray-800">
          <span
            className="text-xl font-bold text-black cursor-pointer"
            onClick={() => navigate('/')}
          >
            JobPort
          </span>
          {user ? (
            <div className="hidden md:flex items-center gap-6">
              <span className="cursor-pointer hover:text-[#6F4E37] transition" onClick={() => navigate('/')}>Home</span>
              <span className="cursor-pointer hover:text-[#6F4E37] transition" onClick={() => navigate('/jobs')}>Jobs</span>
              <span className="cursor-pointer hover:text-blue-600 font-semibold transition" onClick={() => navigate('/recommendations')}>Recommended</span>
            </div>
          ) : (
            <div className="hidden md:flex items-center gap-6">
              <span className="cursor-pointer hover:text-[#6F4E37] transition" onClick={() => navigate('/jobs')}>Jobs</span>
              <span className="cursor-pointer hover:text-[#6F4E37] transition">Companies</span>
              <span className="cursor-pointer hover:text-[#6F4E37] transition" onClick={onOpenEmployer}>For Employers</span>
              <span className="cursor-pointer hover:text-[#6F4E37] transition" onClick={onOpenJobSeeker}>For Professionals</span>
            </div>
          )}
        </div>

        {/* CENTER - Welcome message (desktop only) */}
        {user && (
          <div className="hidden md:flex text-sm font-medium text-gray-700">
            Welcome back, <span className="ml-1 font-semibold">{user.name}</span>
          </div>
        )}

        {/* RIGHT */}
        <div className="flex items-center gap-3 text-gray-700">
          {user ? (
            <>
              {/* Desktop-only icons */}
              <FaBookmark
                className="hidden md:block cursor-pointer text-lg hover:text-[#6F4E37] transition"
                title="My Jobs"
                onClick={() => navigate("/myjobs")}
              />
              <FaEnvelope
                className="hidden md:block cursor-pointer text-lg hover:text-[#6F4E37] transition"
                title="Messages"
              />

              {/* Always visible */}
              <NotificationBell />

              <img
                src={profileImageUrl}
                alt="Profile"
                className="w-8 h-8 rounded-full border object-cover cursor-pointer"
                title="Profile"
                onClick={() => navigate("/profile")}
                onError={(e) => {
                  console.warn("❌ Failed to load profile image, falling back to default.");
                  e.target.onerror = null;
                  e.target.src = defaultAvatar;
                }}
              />

              <button
                onClick={handleLogout}
                className="px-3 py-1.5 border border-[#6F4E37] text-[#6F4E37] rounded hover:bg-[#6F4E37] hover:text-white transition text-sm"
              >
                Logout
              </button>

              {/* Mobile hamburger */}
              <button
                className="md:hidden text-xl p-1 ml-1"
                onClick={() => setMenuOpen(!menuOpen)}
                aria-label="Toggle menu"
              >
                {menuOpen ? <FaTimes /> : <FaBars />}
              </button>
            </>
          ) : (
            <>
              <button onClick={onOpenJobSeeker} className="px-4 py-1.5 border rounded hover:bg-gray-100 text-sm">Login</button>
              <button onClick={onOpenJobSeeker} className="px-4 py-1.5 bg-black text-white rounded hover:opacity-90 text-sm">Sign Up</button>
            </>
          )}
        </div>
      </div>

      {/* Mobile dropdown */}
      {user && menuOpen && (
        <div className="md:hidden mt-3 border-t pt-3 flex flex-col gap-4 text-sm font-medium text-gray-800">
          <span className="cursor-pointer hover:text-[#6F4E37]" onClick={() => { navigate('/'); closeMenu(); }}>Home</span>
          <span className="cursor-pointer hover:text-[#6F4E37]" onClick={() => { navigate('/jobs'); closeMenu(); }}>Jobs</span>
          <span className="cursor-pointer hover:text-[#6F4E37]" onClick={() => { navigate('/recommendations'); closeMenu(); }}>Recommended</span>
          <span className="cursor-pointer hover:text-[#6F4E37]" onClick={() => { navigate('/myjobs'); closeMenu(); }}>My Jobs</span>
        </div>
      )}
    </nav>
  );
}
