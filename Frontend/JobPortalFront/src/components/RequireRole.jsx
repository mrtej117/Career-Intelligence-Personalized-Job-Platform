import { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";

export default function RequireRole({ role, children }) {
  const { user, initializing } = useContext(AuthContext);

  if (initializing) return null;
  if (!user) return <Navigate to="/" />;
  if (user.role !== role) return <Navigate to="/" />;

  return children;
}
