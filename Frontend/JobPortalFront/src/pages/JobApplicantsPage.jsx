import { useParams, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { FaClock, FaEnvelope } from "react-icons/fa";
import API_URL from "../api/config";

const openResume = (url) => {
  if (!url) return;
  window.open(`https://docs.google.com/viewer?url=${encodeURIComponent(url)}`, '_blank');
};


export default function JobApplicantsPage() {
  const { jobId } = useParams();
  const navigate = useNavigate();
  const [applicants, setApplicants] = useState([]);
  const [jobTitle, setJobTitle] = useState("");
  let user = null;
  try { user = JSON.parse(localStorage.getItem("user")); } catch { user = null; }
  const token = localStorage.getItem("token");

  const logoUrl = user?.profilePicture || "/default-logo.png";
  const companyName = user?.companyName || user?.name || "Your Company";

  useEffect(() => {
    async function fetchApplicantsAndTitle() {
      try {
        const res = await fetch(`${API_URL}/applications/job/${jobId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = await res.json();
        setApplicants(data);

        if (data.length > 0) {
          setJobTitle(data[0].jobTitle);
        } else {
          const jobRes = await fetch(`${API_URL}/jobs/${jobId}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          const jobData = await jobRes.json();
          setJobTitle(jobData.title || "");
        }
      } catch (err) {
        console.error("Error fetching applicants or job title:", err);
      }
    }

    fetchApplicantsAndTitle();
  }, [jobId, token]);

  const handleViewResume = (resumeUrl) => {
    openResume(resumeUrl);
  };

  return (
    <div className="page-shell employer-page">
      {/* Header */}
      <div className="flex flex-wrap items-center gap-4 mb-8 border-b pb-4">
        <img
          src={logoUrl}
          alt="Company Logo"
          className="w-12 h-12 object-contain rounded border"
        />
        <div>
          <h1 className="text-2xl font-bold">{companyName}</h1>
          <p className="text-gray-700 text-sm mt-1">
            Applications for "{jobTitle}"
          </p>
        </div>
      </div>

      {/* No applicants message */}
      {applicants.length === 0 ? (
        <p className="text-gray-500 text-center text-md italic">
          Currently there are no applicants for "{jobTitle}"
        </p>
      ) : (
        <div className="grid gap-4">
          {applicants.map((app) => (
            <div
              key={app.id}
                onClick={() => navigate(`/employer/applications/${app.id}`)}
              className="bg-gray-100 border border-gray-200 rounded-xl p-4 shadow-md flex flex-col sm:flex-row sm:justify-between sm:items-center hover:bg-gray-50 transition gap-3 sm:gap-4"
              style={{ borderLeft: "6px solid #6B3F27" }}
            >
              {/* Left: profile and info */}
              <div className="flex items-start gap-4 min-w-0 flex-1">
                <img
                  src={app.applicantProfilePicture || "/default-avatar.png"}
                  alt="Applicant"
                  className="w-12 h-12 flex-shrink-0 rounded-full object-cover border"
                />
                <div className="leading-snug min-w-0">
                  <p
                    onClick={() => navigate(`/employer/applications/${app.id}`)}
                    className="font-semibold text-blue-700 hover:underline cursor-pointer truncate"
                  >
                    {app.applicantUsername}
                  </p>
                  <p className="text-sm text-gray-600 flex items-center gap-2">
                    <FaEnvelope className="text-gray-500 flex-shrink-0" />
                    <span className="truncate">{app.applicantEmail}</span>
                  </p>
                  <p className="text-sm text-gray-600">
                    <strong>DOB:</strong> {app.applicantDOB}
                  </p>
                  <p className="text-sm text-gray-600 flex items-center gap-2">
                    <FaClock className="text-gray-500 flex-shrink-0" /> Applied on:{" "}
                    {new Date(app.appliedAt).toLocaleDateString()}
                  </p>
                  <button
                    onClick={(e) => { e.stopPropagation(); handleViewResume(app.resumeUrl); }}
                    className="text-blue-600 hover:underline text-sm mt-2 min-h-[44px] flex items-center"
                  >
                    📎 View Resume
                  </button>
                </div>
              </div>

              {/* Right: status badge */}
              <span
                className={`self-start sm:self-center flex-shrink-0 whitespace-nowrap text-xs px-3 py-1 rounded-full font-semibold border shadow-sm ${
                  app.status === "PENDING"
                    ? "bg-yellow-100 text-yellow-800 border-yellow-300"
                    : app.status === "ACCEPTED"
                    ? "bg-green-100 text-green-800 border-green-300"
                    : "bg-red-100 text-red-700 border-red-300"
                }`}
              >
                {app.status}
              </span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
