import { useContext, useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { AuthContext } from "../context/AuthContext";
import API_URL from "../api/config";
import apiClient from "../api/client";
import { FileText, ImagePlus, Save, UserRound } from "lucide-react";
import { FadeIn } from "../components/common/Motion";

export default function JobSeekerProfilePage() {
  const { user, updateUser } = useContext(AuthContext);
  const [formData, setFormData] = useState({
    name: user?.name || "",
    username: user?.username || "",
    email: user?.email || "",
    dob: user?.dob || "",
    profilePicture: user?.profilePicture || "",
    resume: user?.resume || "",
    resumeOriginalName: user?.resumeOriginalName || "",
  });

  const [profilePicFile, setProfilePicFile] = useState(null);
  const [resumeFile, setResumeFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem("token");

  useEffect(() => {
    apiClient
      .get("/user/me")
      .then((res) => {
        const data = res.data;
        updateUser(data);
        setFormData((prev) => ({
          ...prev,
          name: data.name ?? prev.name,
          username: data.username ?? prev.username,
          email: data.email ?? prev.email,
          dob: data.dob ?? prev.dob,
          profilePicture: data.profilePicture ?? prev.profilePicture,
          resume: data.resume !== undefined ? data.resume || "" : prev.resume,
          resumeOriginalName: data.resumeOriginalName !== undefined
            ? data.resumeOriginalName || ""
            : prev.resumeOriginalName,
        }));
      })
      .catch((err) => {
        console.error("Failed to fetch profile:", err);
        toast.error("Failed to load profile info.");
      });
  }, [token]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e, type) => {
    const file = e.target.files[0];
    if (type === "profilePic") {
      setProfilePicFile(file);
      const objectUrl = URL.createObjectURL(file);
      setPreviewUrl(objectUrl);
    } else {
      setResumeFile(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    let hasError = false;
    setLoading(true);

    try {
      await axios.put(
        `${API_URL}/user/jobseeker/update-profile`,
        {
          name: formData.name,
          username: formData.username,
          email: formData.email,
          dob: formData.dob,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
    } catch (err) {
      console.error("Error updating profile info:", err);
      toast.error(err.response?.data?.message || "Failed to update profile.");
      hasError = true;
    }

    if (profilePicFile) {
      try {
        const picForm = new FormData();
        picForm.append("file", profilePicFile);
        await axios.post(`${API_URL}/user/upload-profile-picture`, picForm, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const meRes = await apiClient.get("/user/me");
        updateUser(meRes.data);
      } catch (err) {
        console.error("Error uploading profile picture:", err);
        toast.error(err.response?.data?.message || "Failed to upload profile picture.");
        hasError = true;
      }
    }

    if (resumeFile) {
      try {
        const resumeForm = new FormData();
        resumeForm.append("file", resumeFile);
        await axios.post(`${API_URL}/user/jobseeker/upload-resume`, resumeForm, {
          headers: { Authorization: `Bearer ${token}` },
        });
        // Re-fetch /user/me so formData reflects the newly uploaded resume immediately
        const meRes = await axios.get(`${API_URL}/user/me`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        updateUser(meRes.data);
        setFormData((prev) => ({
          ...prev,
          resume: meRes.data.resume ?? prev.resume,
          resumeOriginalName: meRes.data.resumeOriginalName ?? prev.resumeOriginalName,
        }));
        setResumeFile(null);
      } catch (err) {
        console.error("Error uploading resume:", err);
        toast.error(err.response?.data?.message || "Failed to upload resume.");
        hasError = true;
      }
    }

    if (!hasError) toast.success("Profile updated successfully");
    setLoading(false);
  };

  const handleResumePreview = async () => {
    if (!formData.resume) return;

    const previewWindow = window.open("", "_blank");
    try {
      const res = await apiClient.get("/user/resume/preview", {
        responseType: "blob",
      });
      const pdfUrl = URL.createObjectURL(res.data);
      if (previewWindow) {
        previewWindow.location.href = pdfUrl;
        window.setTimeout(() => URL.revokeObjectURL(pdfUrl), 60000);
      } else {
        window.open(pdfUrl, "_blank");
        window.setTimeout(() => URL.revokeObjectURL(pdfUrl), 60000);
      }
    } catch (err) {
      previewWindow?.close();
      console.error("Resume preview failed:", err);
      toast.error(err.response?.data?.message || "Failed to preview resume.");
    }
  };

  return (
    <div className="page-shell"><FadeIn><div className="page-heading"><div><span className="eyebrow"><UserRound size={15} /> Your professional identity</span><h1>My profile</h1><p>Keep your details current so the right opportunities can find you.</p></div></div></FadeIn>

      <form onSubmit={handleSubmit} className="profile-layout">
        {/* Profile Picture */}
        <FadeIn className="profile-aside"><div className="profile-avatar">
            {(previewUrl || formData.profilePicture) ? (
              <img
                src={previewUrl || formData.profilePicture || "/default-avatar.png"}
                alt="Profile"
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="profile-avatar-empty">
                <UserRound size={30} />
              </div>
            )}
            <label className="avatar-edit"><ImagePlus size={14} /> Edit
              <input
                type="file"
                accept="image/*"
                onChange={(e) => handleFileChange(e, "profilePic")}
                className="hidden"
              />
            </label>
          </div><p className="profile-hint">A clear photo helps employers put a face to your experience.</p></FadeIn>

        {/* Form Fields */}
        <FadeIn className="surface surface-pad profile-form"><div className="form-grid">
          <label className="form-label">Full name
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="form-control"
              required
            />
          </label>
          <label className="form-label">Username
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              className="form-control"
              required
            />
          </label>
          <label className="form-label">Email
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="form-control"
              required
            />
          </label>
          <label className="form-label">Date of birth
            <input
              type="date"
              name="dob"
              value={formData.dob}
              onChange={handleChange}
              className="form-control"
              required
            />
          </label>
          <label className="form-label">Resume (PDF)
            <input
              type="file"
              accept=".pdf"
              onChange={(e) => handleFileChange(e, "resume")}
              className="form-control"
            />
            {formData.resume && (
              <div className="resume-current">
                <p className="mb-1">
                  <span className="font-medium text-gray-800">Current:</span>{" "}
                  {formData.resumeOriginalName ||
                    (formData.resume.startsWith("local://")
                      ? formData.resume.replace(/^local:\/\/\d*_?/, "")
                      : formData.resume.split("/").pop())}
                </p>
                  <button
                  type="button"
                  onClick={handleResumePreview}
                  className="text-button"
                >
                  Preview Resume
                </button>
              </div>
            )}
          </label>

          <button
            type="submit"
            disabled={loading}
            className="button button-primary profile-save"
          >
            {loading && (
              <Save size={16} />
            )}
            {loading ? "Saving..." : "Save Changes"}
          </button>
        </div></FadeIn>
      </form>
    </div>
  );
}
