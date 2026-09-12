import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  FaMapMarkerAlt,
  FaBriefcase,
  FaUsers,
  FaClock,
  FaCheckCircle,
} from "react-icons/fa";
import RecentJobCard from "../components/RecentJobCard";
import apiClient from "../api/client";
import { getLogoFallback, handleLogoError } from "../utils/companyLogo";

export default function EmployerDashboard() {
  const navigate = useNavigate();
  let user = null;
  try { user = JSON.parse(localStorage.getItem("user")); } catch { user = null; }
  const [allJobs, setAllJobs] = useState([]);
  const [recentJobs, setRecentJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [recentApplicants, setRecentApplicants] = useState([]);
  const [pending, setPending] = useState(0);
  const [accepted, setAccepted] = useState(0);
  const [rejected, setRejected] = useState(0);

  useEffect(() => {
    async function fetchApplications() {
      try {
        const { data } = await apiClient.get("/applications/employer");
        setApplications(data);

        const pendingCount = data.filter(app => app.status === "PENDING").length;
        const acceptedCount = data.filter(app => app.status === "OFFERED").length;
        const rejectedCount = data.filter(app => app.status === "REJECTED").length;

        setPending(pendingCount);
        setAccepted(acceptedCount);
        setRejected(rejectedCount);

        const sorted = [...data].sort(
          (a, b) => new Date(b.appliedAt) - new Date(a.appliedAt)
        );
        setRecentApplicants(sorted.slice(0, 3));
      } catch (err) {
        console.error("Failed to fetch applications:", err);
      }
    }
    fetchApplications();
  }, []);

  useEffect(() => {
    async function fetchJobs() {
      try {
        const { data } = await apiClient.get("/jobs/my");
        const sorted = [...data].sort(
          (a, b) => new Date(b.postedAt) - new Date(a.postedAt)
        );
        setAllJobs(data);
        setRecentJobs(sorted.slice(0, 3));
      } catch (err) {
        console.error("Failed to fetch jobs:", err);
      }
    }
    fetchJobs();
  }, []);

  const isNew = (appliedAt) => {
    const daysAgo = (Date.now() - new Date(appliedAt)) / (1000 * 60 * 60 * 24);
    return daysAgo < 2;
  };

  return (
    <div className="page-shell employer-page">
      {/* Header */}
      <div className="flex flex-wrap items-center gap-4 mb-8">
        <img
          src={user?.profilePicture || getLogoFallback(user?.companyName || user?.name)}
          onError={(e) => handleLogoError(e, user?.companyName || user?.name)}
          alt="Company Logo"
          className="w-12 h-12 flex-shrink-0 object-contain border rounded-md"
        />
        <h1 className="text-2xl md:text-3xl font-bold min-w-0">
          Welcome, {user?.name || "Employer"}!
        </h1>
      </div>

      {/* Stats Section */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-10">
        <StatCard icon={<FaBriefcase size={20} />} label="Jobs Posted" value={allJobs.length} />
        <StatCard icon={<FaUsers size={20} />} label="Applications" value={applications.length} />
        <StatCard icon={<FaClock size={20} />} label="Pending" value={pending} />
        <StatCard
          icon={<FaCheckCircle size={20} />}
          label="Offered / Rejected"
          value={<span className="font-semibold">{accepted} / {rejected}</span>}
        />
      </div>

      {/* Recent Jobs */}
      <div className="mb-10">
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-xl font-semibold">Recent Jobs</h2>
          <button
            onClick={() => navigate("/create-job")}
            className="bg-[#6B3F27] text-white px-4 py-2 min-h-[44px] rounded hover:bg-[#5C3421] text-sm transition whitespace-nowrap flex-shrink-0"
          >
            + Create Job
          </button>
        </div>
        <div className="grid gap-4 md:grid-cols-3">
          {recentJobs.map((job) => (
            <RecentJobCard
              key={job.id}
              job={{
                id: job.id,
                title: job.title,
                date: new Date(job.postedAt).toISOString().split("T")[0],
                status: "Open",
                location: job.location,
              }}
              onClick={() => navigate(`/employer/jobs/${job.id}`)}
            />
          ))}
        </div>
        <button
          onClick={() => navigate("/employer/jobs")}
          className="mt-4 text-blue-600 hover:underline text-sm"
        >
          View All Jobs
        </button>
      </div>

      {/* Recent Applicants */}
      <div>
        <h2 className="text-xl font-semibold mb-3">Recent Applicants</h2>
        <div className="bg-white border rounded-md shadow-sm divide-y">
          {recentApplicants.map((a, index) => (
            <div
              key={index}
              onClick={() => navigate(`/employer/applications/${a.id}`)}
              className="flex items-center justify-between gap-3 px-3 py-4 hover:bg-gray-50 transition cursor-pointer"
              style={{ borderLeft: "6px solid #6B3F27" }}
            >
              {/* Left Side */}
              <div className="flex items-center gap-3 min-w-0 flex-1">
                <img
                  src={a.applicantProfilePicture || getLogoFallback(a.applicantUsername)}
                  onError={(e) => handleLogoError(e, a.applicantUsername)}
                  alt="Applicant"
                  className="w-11 h-11 flex-shrink-0 rounded-full object-cover border"
                />
                <div className="leading-snug min-w-0">
                  <p className="font-semibold text-blue-700 hover:underline flex items-center gap-2 flex-wrap">
                    <span className="truncate">{a.applicantUsername}</span>
                    {isNew(a.appliedAt) && (
                      <span className="text-xs text-red-600 bg-red-100 px-2 py-0.5 rounded-full font-medium whitespace-nowrap">
                        New
                      </span>
                    )}
                  </p>
                  <p className="text-sm text-gray-600 truncate">
                    Applied for <strong>{a.jobTitle}</strong>
                  </p>
                  <p className="text-xs text-gray-400">
                    on{" "}
                    {new Date(a.appliedAt).toLocaleDateString("en-US", {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                    })}
                  </p>
                </div>
              </div>

              {/* Right Side - Status */}
              <span
                className={`flex-shrink-0 whitespace-nowrap px-3 py-1 rounded-full text-xs font-semibold border shadow-sm ${
                  a.status === "PENDING"
                    ? "bg-yellow-100 text-yellow-700 border-yellow-300"
                    : a.status === "OFFERED"
                    ? "bg-green-100 text-green-700 border-green-300"
                    : "bg-red-100 text-red-600 border-red-300"
                }`}
              >
                {a.status.charAt(0) + a.status.slice(1).toLowerCase()}
              </span>
            </div>
          ))}
        </div>
        <button
          onClick={() => navigate("/employer/applicants")}
          className="mt-4 text-blue-600 hover:underline text-sm"
        >
          View All Applicants
        </button>
      </div>
    </div>
  );
}

function StatCard({ icon, label, value }) {
  return (
    <div className="bg-white border border-gray-200 rounded-lg shadow-md p-4 text-center hover:shadow-lg transition-shadow">
      <div className="text-lg font-semibold text-gray-800 flex justify-center items-center gap-2 mb-1">
        {icon} {value}
      </div>
      <div className="text-sm text-gray-500">{label}</div>
    </div>
  );
}
