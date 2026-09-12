import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaClock, FaEnvelope, FaFileAlt } from "react-icons/fa";
import API_URL from "../api/config";

export default function EmployerApplicantsPage() {
  const [applicants, setApplicants] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const applicantsPerPage = 5;
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  let user = null;
  try { user = JSON.parse(localStorage.getItem("user")); } catch { user = null; }
  const logoUrl = user?.profilePicture || "/default-logo.png";
  const companyName = user?.companyName || user?.name || "Your Company";

  useEffect(() => {
    async function fetchApplicants() {
      try {
        const res = await fetch(`${API_URL}/applications/employer`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = await res.json();
        const sorted = [...data].sort(
          (a, b) => new Date(b.appliedAt) - new Date(a.appliedAt)
        );
        setApplicants(sorted);
      } catch (err) {
        console.error("Error fetching applicants:", err);
      }
    }
    fetchApplicants();
  }, [token]);

  const totalPages = Math.ceil(applicants.length / applicantsPerPage);
  const startIdx = (currentPage - 1) * applicantsPerPage;
  const currentApplicants = applicants.slice(startIdx, startIdx + applicantsPerPage);

  const isNew = (appliedAt) => {
    const daysAgo = (Date.now() - new Date(appliedAt)) / (1000 * 60 * 60 * 24);
    return daysAgo < 2;
  };

  return (
    <div className="page-shell employer-page">
      {/* Company Header */}
      <div className="flex flex-wrap items-center gap-4 mb-8 border-b pb-4">
        <img
          src={logoUrl}
          alt="Company Logo"
          className="w-12 h-12 object-contain rounded border"
        />
        <div>
          <h1 className="text-2xl font-bold">{companyName}</h1>
          <p className="text-gray-700 text-sm mt-1">All Applicants</p>
        </div>
      </div>

      {/* Applicants List */}
      <div className="grid gap-4 mb-6">
        {currentApplicants.map((a, index) => (
          <div
            key={index}
            onClick={() => navigate(`/employer/applications/${a.id}`)}
            className="relative flex flex-col gap-2 bg-gray-50 border border-gray-200 rounded-lg shadow-sm p-5 pl-4 hover:bg-white transition-all cursor-pointer"
            style={{ borderLeft: "6px solid #6B3F27" }}
          >
            {/* Top Row: Avatar + Name + Status */}
            <div className="flex justify-between items-center gap-3">
              <div className="flex items-center gap-3 min-w-0 flex-1">
                <img
                  src={a.applicantProfilePicture || "/default-avatar.png"}
                  alt="Applicant"
                  className="w-10 h-10 flex-shrink-0 rounded-full object-cover border"
                />
                <h2 className="text-base font-semibold text-gray-800 truncate">{a.applicantUsername}</h2>
                {isNew(a.appliedAt) && (
                  <span className="text-xs font-medium text-red-500 bg-red-100 px-2 py-0.5 rounded-full whitespace-nowrap flex-shrink-0">
                    New
                  </span>
                )}
              </div>

              <span
                className={`flex-shrink-0 whitespace-nowrap text-xs px-3 py-1 rounded-full font-semibold shadow-sm ${
                  a.status === "PENDING"
                    ? "bg-yellow-100 text-yellow-800"
                    : a.status === "ACCEPTED"
                    ? "bg-green-100 text-green-700"
                    : "bg-red-100 text-red-600"
                }`}
              >
                {a.status.charAt(0) + a.status.slice(1).toLowerCase()}
              </span>
            </div>

            {/* Middle Row: Applied for + Date */}
            <div className="text-sm text-gray-700 flex items-center gap-2 flex-wrap">
              <FaFileAlt className="text-gray-400" />
              <span>
                Applied for <strong>{a.jobTitle}</strong>
              </span>
              <span className="flex items-center text-gray-500">
                <FaClock className="mr-1" />
                {new Date(a.appliedAt).toLocaleDateString()}
              </span>
            </div>

            {/* Bottom Row: Email */}
            <div className="text-sm text-gray-600 flex items-center gap-2">
              <FaEnvelope className="text-gray-400" />
              <span>{a.applicantEmail}</span>
            </div>
          </div>
        ))}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-4">
          {[...Array(totalPages)].map((_, i) => {
            const page = i + 1;
            return (
              <button
                key={page}
                onClick={() => setCurrentPage(page)}
                className={`px-3 py-2 min-h-[44px] rounded border text-sm font-medium ${
                  currentPage === page
                    ? "bg-[#6B3F27] text-white border-[#6B3F27]"
                    : "bg-white text-black border-gray-300"
                } hover:bg-[#6B3F27] hover:text-white transition`}
              >
                {page}
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
