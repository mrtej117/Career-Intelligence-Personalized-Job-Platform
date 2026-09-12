import { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import axios from "axios";
import MyJobCard from "../components/MyJobCard";
import AppliedJobCard from "../components/AppliedJobCard";
import InterviewCard from "../components/InterviewCard";
import API_URL from "../api/config";
import { Bookmark, BriefcaseBusiness, CalendarDays } from "lucide-react";
import { FadeIn, StaggerContainer } from "../components/common/Motion";

export default function MyJobsPage() {
  const [searchParams] = useSearchParams();
  const defaultTab = searchParams.get("tab") || "saved";
  const [activeTab, setActiveTab] = useState(defaultTab);

  const [savedJobs, setSavedJobs] = useState([]);
  const [appliedJobs, setAppliedJobs] = useState([]);

  const [currentSavedPage, setCurrentSavedPage] = useState(1);
  const [currentAppliedPage, setCurrentAppliedPage] = useState(1);

  const jobsPerPage = 5;
  const token = localStorage.getItem("token");

  const refreshAllTabs = async () => {
    try {
      const [savedRes, appliedRes] = await Promise.all([
        axios.get(`${API_URL}/user/saved-jobs`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
        axios.get(`${API_URL}/applications/my`, {
          headers: { Authorization: `Bearer ${token}` },
        }),
      ]);
      setSavedJobs(savedRes.data);
      setAppliedJobs(appliedRes.data.content);
    } catch (err) {
      console.error("Error refreshing tabs:", err);
    }
  };

  useEffect(() => {
    const tabParam = searchParams.get("tab");
    if (tabParam && ["saved", "applied", "interviews"].includes(tabParam)) {
      setActiveTab(tabParam);
    }
  }, [searchParams]);

  useEffect(() => {
    refreshAllTabs();
  }, [activeTab]);

  const handleUnsave = async (jobId) => {
    try {
      await axios.delete(`${API_URL}/user/unsave-job/${jobId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setSavedJobs((prev) => prev.filter((job) => job.id !== jobId));
    } catch (err) {
      console.error("Error unsaving job:", err);
    }
  };

  const handleWithdraw = async (applicationId) => {
    try {
      await axios.delete(`${API_URL}/applications/${applicationId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setAppliedJobs((prev) =>
        prev.filter((app) => app.applicationId !== applicationId)
      );
    } catch (err) {
      console.error("Error withdrawing application:", err);
    }
  };

  const indexOfLastSaved = currentSavedPage * jobsPerPage;
  const indexOfFirstSaved = indexOfLastSaved - jobsPerPage;
  const currentSavedJobs = savedJobs.slice(indexOfFirstSaved, indexOfLastSaved);
  const totalSavedPages = Math.ceil(savedJobs.length / jobsPerPage);

  const indexOfLastApplied = currentAppliedPage * jobsPerPage;
  const indexOfFirstApplied = indexOfLastApplied - jobsPerPage;
  const currentAppliedJobs = appliedJobs.slice(indexOfFirstApplied, indexOfLastApplied);
  const totalAppliedPages = Math.ceil(appliedJobs.length / jobsPerPage);

  return (
    <div className="page-shell"><FadeIn><div className="page-heading"><div><span className="eyebrow"><BriefcaseBusiness size={15} /> Your career workspace</span><h1>My jobs</h1><p>Keep the opportunities you care about close.</p></div></div></FadeIn>

      <div className="workspace-tabs">
        {["saved", "applied", "interviews"].map((tab) => (
          <button
            key={tab}
            className={`workspace-tab ${
              activeTab === tab
                ? "border-black font-semibold text-black"
                : "border-transparent text-gray-500 hover:text-black"
            }`}
            onClick={() => setActiveTab(tab)}
          >
            {tab.charAt(0).toUpperCase() + tab.slice(1)} (
              {tab === "saved"
                ? savedJobs.length
                : tab === "applied"
                ? appliedJobs.length
                : 0}
              )
          </button>
        ))}
      </div>

      <div>
        {activeTab === "saved" && (
          <div className="surface surface-pad workspace-panel">
            {currentSavedJobs.length > 0 ? (
              <StaggerContainer>{currentSavedJobs.map((job) => (
                <MyJobCard
                  key={job.id}
                  job={job}
                  tab="saved"
                  onUnsave={handleUnsave}
                  onRefresh={refreshAllTabs}
                />
              ))}</StaggerContainer>
            ) : (
              <p className="text-gray-600 text-sm">No saved jobs found.</p>
            )}

            {savedJobs.length > 5 && (
              <div className="flex justify-center items-center gap-4 mt-6">
                <button
                  className="px-4 py-2 min-h-[44px] border border-gray-300 rounded hover:bg-gray-100 transition disabled:opacity-40"
                  onClick={() => setCurrentSavedPage((prev) => Math.max(prev - 1, 1))}
                  disabled={currentSavedPage === 1}
                >
                  Previous
                </button>
                <span className="text-sm text-gray-700">
                  Page {currentSavedPage} of {totalSavedPages}
                </span>
                <button
                  className="px-4 py-2 min-h-[44px] border border-gray-300 rounded hover:bg-gray-100 transition disabled:opacity-40"
                  onClick={() => setCurrentSavedPage((prev) => Math.min(prev + 1, totalSavedPages))}
                  disabled={currentSavedPage === totalSavedPages}
                >
                  Next
                </button>
              </div>
            )}
          </div>
        )}

        {activeTab === "applied" && (
          <div className="surface surface-pad workspace-panel">
            {currentAppliedJobs.length > 0 ? (
              <StaggerContainer>{currentAppliedJobs.map((application) => (
                <AppliedJobCard
                  key={application.applicationId}
                  application={application}
                  onWithdraw={handleWithdraw}
                  onRefresh={refreshAllTabs}
                />
              ))}</StaggerContainer>
            ) : (
              <p className="text-gray-600 text-sm">No applied jobs yet.</p>
            )}

            {appliedJobs.length > 5 && (
              <div className="flex justify-center items-center gap-4 mt-6">
                <button
                  className="px-4 py-2 min-h-[44px] border border-gray-300 rounded hover:bg-gray-100 transition disabled:opacity-40"
                  onClick={() => setCurrentAppliedPage((prev) => Math.max(prev - 1, 1))}
                  disabled={currentAppliedPage === 1}
                >
                  Previous
                </button>
                <span className="text-sm text-gray-700">
                  Page {currentAppliedPage} of {totalAppliedPages}
                </span>
                <button
                  className="px-4 py-2 min-h-[44px] border border-gray-300 rounded hover:bg-gray-100 transition disabled:opacity-40"
                  onClick={() => setCurrentAppliedPage((prev) => Math.min(prev + 1, totalAppliedPages))}
                  disabled={currentAppliedPage === totalAppliedPages}
                >
                  Next
                </button>
              </div>
            )}
          </div>
        )}

        {activeTab === "interviews" && (
          <div className="surface surface-pad workspace-panel empty-state"><CalendarDays size={26} /><h2>No interviews scheduled yet</h2><p>Upcoming interviews will appear here.</p>
          </div>
        )}
      </div>

      <div className="mt-10 text-center text-sm text-blue-600 hover:underline cursor-pointer">
        Not seeing a job?
      </div>
    </div>
  );
}
