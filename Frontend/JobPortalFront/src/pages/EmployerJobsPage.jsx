import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaMapMarkerAlt, FaClock } from "react-icons/fa";
import apiClient from "../api/client";

export default function EmployerJobsPage() {
  const [jobs, setJobs] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const jobsPerPage = 5;
  const navigate = useNavigate();

  let user = null;
  try { user = JSON.parse(localStorage.getItem("user")); } catch { user = null; }
  const logoUrl = user?.profilePicture || "/default-logo.png";
  const companyName = user?.companyName || user?.name || "Your Company";

  useEffect(() => {
    async function fetchJobs() {
      try {
        const res = await apiClient.get("/jobs/my");
        const data = res.data;
        const sorted = [...data].sort(
          (a, b) => new Date(b.postedAt) - new Date(a.postedAt)
        );
        setJobs(sorted);
      } catch (err) {
        console.error("Error fetching jobs:", err);
      }
    }
    fetchJobs();
  }, []);

  const totalPages = Math.ceil(jobs.length / jobsPerPage);
  const startIdx = (currentPage - 1) * jobsPerPage;
  const currentJobs = jobs.slice(startIdx, startIdx + jobsPerPage);

  const isNew = (postedAt) => {
    const daysAgo = (Date.now() - new Date(postedAt)) / (1000 * 60 * 60 * 24);
    return daysAgo < 3;
  };

  const getCategoryFromTitle = (title) => {
    if (!title || typeof title !== "string") return "General";
    const lower = title.toLowerCase();
    if (lower.includes("backend")) return "Backend";
    if (lower.includes("frontend")) return "Frontend";
    if (lower.includes("fullstack")) return "Fullstack";
    if (lower.includes("qa")) return "QA";
    if (lower.includes("devops")) return "DevOps";
    return "General";
  };

  return (
    <div className="page-shell employer-page">
      {/* Header */}
      <div className="flex flex-wrap items-center gap-4 mb-10 border-b pb-4">
        <img
          src={logoUrl}
          alt="Company Logo"
          className="w-14 h-14 object-cover border rounded-md"
        />
        <div>
          <h1 className="text-2xl md:text-3xl font-bold text-gray-800">{companyName}</h1>
          <p className="text-sm text-gray-500 mt-1">All Posted Jobs</p>
        </div>
      </div>

      {/* Jobs List */}
      <div className="grid gap-5 mb-6">
        {currentJobs.map((job) => (
          <div
            key={job.id}
            onClick={() => navigate(`/employer/jobs/${job.id}`)}
            className="relative cursor-pointer bg-[#F9FAFB] border border-gray-200 rounded-lg p-6 pl-4 shadow-sm hover:shadow-md hover:scale-[1.01] transition-all duration-200"
            style={{ borderLeft: "6px solid #6B3F27" }}
          >
            {/* Job Category Tag */}
            <span className="absolute top-4 left-6 text-xs font-semibold text-white bg-[#6B3F27] px-2 py-1 rounded-full">
              {getCategoryFromTitle(job.title)}
            </span>

            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start mt-6 gap-3 sm:gap-0">
              <div className="min-w-0 flex-1">
                <h2 className="text-lg font-bold text-gray-800 break-words">
                  {job.title || "Untitled Job"}
                </h2>
                <div className="text-sm text-gray-600 flex gap-4 flex-wrap mt-2">
                  <span className="flex items-center">
                    <FaMapMarkerAlt className="mr-1 flex-shrink-0" /> {job.location || "N/A"}
                  </span>
                  <span className="flex items-center">
                    <FaClock className="mr-1 flex-shrink-0" />
                    {job.postedAt
                      ? new Date(job.postedAt).toLocaleDateString()
                      : "Unknown Date"}
                  </span>
                </div>
              </div>

              <div className="flex flex-row flex-wrap sm:flex-col sm:items-end gap-2 flex-shrink-0">
                <span className="px-3 py-1 rounded-full bg-green-100 text-green-700 text-sm font-medium whitespace-nowrap">
                  ● Open
                </span>
                {job.postedAt && isNew(job.postedAt) && (
                  <span className="px-2 py-1 text-xs font-semibold text-red-600 bg-red-100 rounded-full whitespace-nowrap">
                    New
                  </span>
                )}
                <div className="flex gap-2">
                  <span className="px-2 py-1 text-xs bg-blue-100 text-blue-700 rounded-full font-medium whitespace-nowrap">
                    {job.type ? job.type.replace("_", " ").toLowerCase() : "n/a"}
                  </span>
                  <span className="px-2 py-1 text-xs bg-purple-100 text-purple-700 rounded-full font-medium whitespace-nowrap">
                    {job.workMode ? job.workMode.toLowerCase() : "n/a"}
                  </span>
                </div>
              </div>
            </div>

            <p className="mt-3 text-sm text-gray-700 line-clamp-2">
              {job.description || "No description provided."}
            </p>
          </div>
        ))}
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-8">
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