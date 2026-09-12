import { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import API_URL from '../api/config';
import { motion as Motion } from 'framer-motion';
import { X, LoaderCircle } from 'lucide-react';

export default function EmployerModal({ isOpen, onClose }) {
  const [activeTab, setActiveTab] = useState('signin');
  const [profilePic, setProfilePic] = useState(null);
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const [signupData, setSignupData] = useState({
    name: '',
    username: '',
    password: '',
    companyName: '',
    industry: '',
    email: ''
  });

  const [signinData, setSigninData] = useState({
    username: '',
    password: ''
  });
  const [signingIn, setSigningIn] = useState(false);

  if (!isOpen) return null;

  const handleSignupChange = (e) => {
    setSignupData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSigninChange = (e) => {
    setSigninData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleProfileChange = (e) => {
    const file = e.target.files[0];
    if (file) setProfilePic(file);
  };

  const handleSignUp = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_URL}/auth/signup/employer`, {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(signupData)
      });

      if (!res.ok) {
        const errorData = await res.json().catch(() => null);
        const message = errorData?.message || 'Signup failed. Please try again.';
        throw new Error(message);
      }
      toast.success("Account created! Now sign in.");

      setSigninData({
        username: signupData.username,
        password: signupData.password
      });
      setActiveTab("signin");
    } catch (err) {
      toast.error("Signup failed: " + err.message);
    }
  };

  const handleSignIn = async (e) => {
    e.preventDefault();
    setSigningIn(true);
    try {
      const payload = {
        username: (signinData.username || "").trim(),
        password: signinData.password
      };
      const loginRes = await fetch(`${API_URL}/auth/signin`, {
        method: "POST",
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!loginRes.ok) throw new Error("Invalid credentials");

      const data = await loginRes.json();
      const token = data.token.replace(/\n/g, '');

      // ❌ Prevent job seekers from logging in via employer modal
      if (data.role !== "EMPLOYER") {
        toast.error("This account is not an employer!");
        return;
      }

      const warnings = [];

      if (profilePic) {
        const picForm = new FormData();
        picForm.append('file', profilePic);
        const picRes = await fetch(`${API_URL}/user/upload-profile-picture`, {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
          body: picForm
        });
        if (!picRes.ok) {
          const errData = await picRes.json().catch(() => null);
          const msg = errData?.message || "Profile picture upload failed.";
          warnings.push(msg + " You can upload it later from your profile.");
        }
      }

      const meRes = await fetch(`${API_URL}/user/me`, {
        method: "GET",
        headers: { Authorization: `Bearer ${token}` }
      });

      const updatedUser = await meRes.json();
      login(updatedUser, token);

      toast.success("Signed in!");
      warnings.forEach((w) => toast.warning(w));
      onClose();
      navigate("/dashboard");
    } catch (err) {
      toast.error("Sign in failed: " + err.message);
    } finally {
      setSigningIn(false);
    }
  };

  return (
    <Motion.div className="modal-backdrop" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
      <Motion.div className="auth-modal" initial={{ opacity: 0, y: 16, scale: .98 }} animate={{ opacity: 1, y: 0, scale: 1 }} transition={{ duration: .2 }}>
        <div className="modal-body">
          <button className="modal-close" onClick={onClose} aria-label="Close"><X size={19} /></button>
          <div className="modal-brand">JobPort</div>

          <div className="flex mb-4 border-b border-gray-200">
            <button
              className={`flex-1 py-2 text-sm font-medium ${activeTab === 'signin' ? 'border-b-2 border-black font-semibold' : 'text-gray-500'}`}
              onClick={() => setActiveTab('signin')}
            >
              Sign In
            </button>
            <button
              className={`flex-1 py-2 text-sm font-medium ${activeTab === 'signup' ? 'border-b-2 border-black font-semibold' : 'text-gray-500'}`}
              onClick={() => setActiveTab('signup')}
            >
              Sign Up
            </button>
          </div>

          {activeTab === 'signin' ? (
            <form className="space-y-4" onSubmit={handleSignIn}>
              <input name="username" onChange={handleSigninChange} value={signinData.username} type="text" placeholder="Username" className="w-full border px-3 py-3 rounded text-base" />
              <input name="password" onChange={handleSigninChange} value={signinData.password} type="password" placeholder="Password" className="w-full border px-3 py-3 rounded text-base" />
              <button type="submit" disabled={signingIn} className="button button-primary auth-submit">
                {signingIn ? (
                  <>
                    <LoaderCircle size={16} />
                    Signing in...
                  </>
                ) : 'Sign In'}
              </button>
            </form>
          ) : (
            <form className="space-y-4" onSubmit={handleSignUp}>
              <input name="name" value={signupData.name} onChange={handleSignupChange} type="text" placeholder="Name" className="w-full border px-3 py-3 rounded text-base" />
              <input name="username" value={signupData.username} onChange={handleSignupChange} type="text" placeholder="Username" className="w-full border px-3 py-3 rounded text-base" />
              <input name="password" value={signupData.password} onChange={handleSignupChange} type="password" placeholder="Password" className="w-full border px-3 py-3 rounded text-base" />
              <input name="companyName" value={signupData.companyName} onChange={handleSignupChange} type="text" placeholder="Company Name" className="w-full border px-3 py-3 rounded text-base" />
              <input name="industry" value={signupData.industry} onChange={handleSignupChange} type="text" placeholder="Industry" className="w-full border px-3 py-3 rounded text-base" />
              <input name="email" value={signupData.email} onChange={handleSignupChange} type="email" placeholder="Email" className="w-full border px-3 py-3 rounded text-base" />

              <div>
                <label className="block mb-1 font-medium text-sm">Upload Profile Picture:</label>
                {!profilePic ? (
                  <input type="file" name="profilePicture" accept="image/*" onChange={handleProfileChange} className="w-full border px-3 py-3 rounded bg-gray-50 text-sm" />
                ) : (
                  <p className="text-sm text-green-600">✅ {profilePic.name} uploaded</p>
                )}
              </div>

              <button type="submit" className="w-full bg-black text-white py-3 rounded text-base font-medium min-h-[44px]">Sign Up</button>
            </form>
          )}
        </div>
      </Motion.div>
    </Motion.div>
  );
}
