import { createContext, useState, useEffect, useRef } from "react";
import { jwtDecode } from "jwt-decode";
import API_URL from "../api/config";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [initializing, setInitializing] = useState(true);
  const logoutTimerRef = useRef(null);

  const scheduleAutoLogout = (token) => {
    try {
      const { exp } = jwtDecode(token);
      const msUntilExpiry = exp * 1000 - Date.now();
      if (msUntilExpiry <= 0) return;
      logoutTimerRef.current = setTimeout(() => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setUser(null);
        window.location.href = "/login";
      }, msUntilExpiry);
    } catch {
      // malformed token — expiry check already handled at init
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    const storedUser = localStorage.getItem("user");
    if (token && storedUser) {
      try {
        const { exp } = jwtDecode(token);
        if (exp * 1000 < Date.now()) {
          localStorage.removeItem("token");
          localStorage.removeItem("user");
        } else {
          setUser(JSON.parse(storedUser));
          fetch(`${API_URL}/user/me`, {
            headers: { Authorization: `Bearer ${token}` },
          })
            .then((response) => (response.ok ? response.json() : null))
            .then((currentUser) => {
              if (currentUser) {
                localStorage.setItem("user", JSON.stringify(currentUser));
                setUser(currentUser);
              }
            })
            .catch(() => {});
          scheduleAutoLogout(token);
        }
      } catch {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
      }
    }
    setInitializing(false);
    return () => clearTimeout(logoutTimerRef.current);
  }, []);

  const login = (userObj, token) => {
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(userObj));
    setUser(userObj);
    scheduleAutoLogout(token);
  };

  const logout = () => {
    clearTimeout(logoutTimerRef.current);
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  };

  const updateUser = (updatedFields) => {
    const updatedUser = { ...user, ...updatedFields };
    localStorage.setItem("user", JSON.stringify(updatedUser));
    setUser(updatedUser);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, initializing, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
}