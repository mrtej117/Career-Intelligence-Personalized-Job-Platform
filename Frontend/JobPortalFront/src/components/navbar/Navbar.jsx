import { useContext, useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { Bell, BriefcaseBusiness, ChevronDown, LogOut, Menu, Moon, Sun, UserRound, X } from "lucide-react";
import { toast } from "react-toastify";
import { AuthContext } from "../../context/AuthContext";
import NotificationBell from "../NotificationBell";
import defaultAvatar from "../../pics/deafult-avatar.png";

export default function Navbar({ onOpenJobSeeker, onOpenEmployer }) {
  const { user, logout } = useContext(AuthContext);
  const location = useLocation();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const [darkMode, setDarkMode] = useState(() => localStorage.getItem("jobport-theme") === "dark");

  useEffect(() => {
    document.documentElement.dataset.theme = darkMode ? "dark" : "light";
    localStorage.setItem("jobport-theme", darkMode ? "dark" : "light");
  }, [darkMode]);

  const roleIsEmployer = user?.role === "EMPLOYER";
  const links = roleIsEmployer
    ? [{ label: "Overview", to: "/dashboard" }, { label: "My jobs", to: "/employer/jobs" }, { label: "Post a job", to: "/create-job" }]
    : user
      ? [{ label: "Discover", to: "/jobs" }, { label: "For you", to: "/recommendations" }, { label: "Saved jobs", to: "/myjobs" }]
      : [{ label: "Find jobs", to: "/jobs" }, { label: "For employers", action: onOpenEmployer }];

  const closeMenu = () => setMenuOpen(false);
  const handleLogout = () => {
    logout();
    toast.success("Logged out successfully!");
    navigate("/");
  };

  return (
    <header className="site-header">
      <div className="nav-shell">
        <Link className="brand" to={roleIsEmployer ? "/dashboard" : "/"} onClick={closeMenu}><span className="brand-mark"><BriefcaseBusiness size={18} /></span><span>JobPort</span></Link>
        <nav className={`primary-nav ${menuOpen ? "is-open" : ""}`} aria-label="Primary navigation">
          {links.map((link) => link.to ? <Link key={link.label} className={location.pathname === link.to ? "active" : ""} to={link.to} onClick={closeMenu}>{link.label}</Link> : <button key={link.label} className="nav-link" onClick={() => { link.action(); closeMenu(); }}>{link.label}</button>)}
          {!user && <button className="nav-link" onClick={() => { onOpenJobSeeker(); closeMenu(); }}>For professionals</button>}
        </nav>
        <div className="nav-actions">
          <button className="icon-button" onClick={() => setDarkMode((value) => !value)} aria-label="Toggle color theme" title="Toggle color theme">{darkMode ? <Sun size={18} /> : <Moon size={18} />}</button>
          {user ? <><>{roleIsEmployer ? <button className="icon-button" aria-label="Notifications"><Bell size={18} /></button> : <NotificationBell />}</><button className="profile-chip" onClick={() => navigate(roleIsEmployer ? "/dashboard" : "/profile")}><img src={user.profilePicture || defaultAvatar} alt="" /><span>{roleIsEmployer ? user.companyName : user.name}</span><ChevronDown size={14} /></button><button className="button button-ghost logout-button" onClick={handleLogout}><LogOut size={16} /> Log out</button></> : <><button className="button button-ghost desktop-only" onClick={onOpenJobSeeker}>Sign in</button><button className="button button-primary" onClick={onOpenJobSeeker}><UserRound size={16} /> Join free</button></>}
          <button className="icon-button mobile-menu-button" onClick={() => setMenuOpen((value) => !value)} aria-label="Toggle menu">{menuOpen ? <X size={20} /> : <Menu size={20} />}</button>
        </div>
      </div>
    </header>
  );
}
