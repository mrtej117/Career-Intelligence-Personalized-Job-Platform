import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaBars, FaTimes } from "react-icons/fa";

export default function NavbarGuest({ onOpenJobSeeker, onOpenEmployer }) {
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const closeMenu = () => setMenuOpen(false);

  return (
    <nav className="w-full px-6 py-4 bg-white shadow-sm border-b sticky top-0 z-50">
      <div className="flex justify-between items-center">

        {/* LEFT - Logo + desktop nav links */}
        <div className="flex items-center gap-6 text-sm font-medium text-gray-800">
          <span
            className="text-xl font-bold text-black cursor-pointer"
            onClick={() => navigate("/")}
          >
            JobPort
          </span>
          <div className="hidden md:flex items-center gap-6">
            <span className="hover:text-black cursor-pointer" onClick={() => navigate('/jobs')}>Jobs</span>
            <span className="hover:text-black cursor-pointer">Companies</span>
            <span className="hover:text-black cursor-pointer" onClick={onOpenEmployer}>For Employers</span>
            <span className="hover:text-black cursor-pointer" onClick={onOpenJobSeeker}>For Professionals</span>
          </div>
        </div>

        {/* RIGHT */}
        <div className="flex items-center gap-3 text-gray-700">

          {/* Desktop login / sign-up dropdowns */}
          <div className="hidden md:flex items-center gap-3">
            <div className="relative group">
              <button className="px-4 py-1.5 border rounded hover:bg-gray-100 text-sm">
                Login
              </button>
              <div className="absolute hidden group-hover:flex flex-col bg-white border shadow-md right-0 top-[110%] z-20 min-w-[140px] rounded">
                <button onClick={onOpenJobSeeker} className="px-4 py-2 hover:bg-gray-100 text-left text-sm">As Job Seeker</button>
                <button onClick={onOpenEmployer} className="px-4 py-2 hover:bg-gray-100 text-left text-sm">As Employer</button>
              </div>
            </div>
            <div className="relative group">
              <button className="px-4 py-1.5 bg-black text-white rounded hover:opacity-90 text-sm">
                Sign Up
              </button>
              <div className="absolute hidden group-hover:flex flex-col bg-white border shadow-md right-0 top-[110%] z-20 min-w-[140px] rounded">
                <button onClick={onOpenJobSeeker} className="px-4 py-2 hover:bg-gray-100 text-left text-sm">As Job Seeker</button>
                <button onClick={onOpenEmployer} className="px-4 py-2 hover:bg-gray-100 text-left text-sm">As Employer</button>
              </div>
            </div>
          </div>

          {/* Mobile: plain buttons + hamburger */}
          <div className="flex md:hidden items-center gap-2">
            <button onClick={onOpenJobSeeker} className="px-3 py-1.5 border rounded text-sm">Login</button>
            <button onClick={onOpenJobSeeker} className="px-3 py-1.5 bg-black text-white rounded text-sm">Sign Up</button>
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              className="text-xl p-1 ml-1"
              aria-label="Toggle menu"
            >
              {menuOpen ? <FaTimes /> : <FaBars />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile dropdown */}
      {menuOpen && (
        <div className="md:hidden mt-3 border-t pt-3 flex flex-col gap-4 text-sm font-medium text-gray-800">
          <span className="cursor-pointer hover:text-black" onClick={() => { navigate('/jobs'); closeMenu(); }}>Jobs</span>
          <span className="cursor-pointer hover:text-black" onClick={closeMenu}>Companies</span>
          <span className="cursor-pointer hover:text-black" onClick={() => { onOpenEmployer(); closeMenu(); }}>For Employers</span>
          <span className="cursor-pointer hover:text-black" onClick={() => { onOpenJobSeeker(); closeMenu(); }}>For Professionals</span>
        </div>
      )}
    </nav>
  );
}
