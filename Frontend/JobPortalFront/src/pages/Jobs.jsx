import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import JobDetails from "../components/JobDetails";
import JobSearchBar from "../components/JobSearchBar";
import PaginatedJobList from "../components/PaginatedJobList";
import API_URL from "../api/config";

export default function Jobs() {
  const [searchParams] = useSearchParams();
  const [jobs, setJobs] = useState([]);
  const [selectedJob, setSelectedJob] = useState(null);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const jobId = searchParams.get("jobId");
    fetchAllJobs(0, 10, jobId);
  }, []);

  const fetchAllJobs = async (page = 0, size = 10, targetJobId = null) => {
    setLoading(true);
    try {
      const res = await fetch(
        `${API_URL}/jobs?page=${page}&size=${size}`,
        { headers: { "Content-Type": "application/json" } }
      );

      if (!res.ok) {
        const errorText = await res.text();
        console.error("❌ Failed to fetch jobs:", res.status, errorText);
        setError("Failed to fetch jobs.");
        return;
      }

      const data = await res.json();
      setJobs(data.content);
      setCurrentPage(data.currentPage);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);

      if (targetJobId) {
        const found = data.content.find((j) => String(j.id) === String(targetJobId));
        if (found) {
          setSelectedJob(found);
        } else {
          const jobRes = await fetch(`${API_URL}/jobs/${targetJobId}`, {
            headers: { "Content-Type": "application/json" },
          });
          if (jobRes.ok) {
            setSelectedJob(await jobRes.json());
          } else {
            setSelectedJob(data.content[0] || null);
          }
        }
      } else {
        setSelectedJob(data.content[0] || null);
      }
    } catch (err) {
      console.error("🔥 Error:", err.message);
      setError("Something went wrong.");
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async ({ title, location, type }) => {
    if (!title.trim() && !location.trim() && !type.trim()) {
      fetchAllJobs();
      return;
    }

    setLoading(true);
    try {
      let titleResults = null;
      let locationResults = null;
      let typeResults = null;

      if (title.trim()) {
        const res = await fetch(
          `${API_URL}/jobs/search/title?keyword=${encodeURIComponent(title)}&page=0&size=100`,
          { headers: { "Content-Type": "application/json" } }
        );
        if (res.ok) {
          const data = await res.json();
          titleResults = data.content;
        }
      }

      if (location.trim()) {
        const res = await fetch(
          `${API_URL}/jobs/search/location?location=${encodeURIComponent(location)}&page=0&size=100`,
          { headers: { "Content-Type": "application/json" } }
        );
        if (res.ok) {
          const data = await res.json();
          locationResults = data.content;
        }
      }

      if (type.trim()) {
        const res = await fetch(
          `${API_URL}/jobs/search/type?type=${encodeURIComponent(type)}&page=0&size=100`,
          { headers: { "Content-Type": "application/json" } }
        );
        if (res.ok) {
          const data = await res.json();
          typeResults = data.content;
        }
      }

      const allResultSets = [titleResults, locationResults, typeResults].filter((r) => r !== null);
      const idSets = allResultSets.map((r) => new Set(r.map((j) => j.id)));
      const finalResults = allResultSets[0].filter((j) => idSets.every((s) => s.has(j.id)));

      setJobs(finalResults);
      setSelectedJob(finalResults[0] || null);
      
      // Fix pagination state for search results
      setCurrentPage(0);
      setTotalPages(1);
      setError(null);
    } catch (err) {
      console.error("🔴 Search error:", err.message);
      setError("Search failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="jobs-page"><main className="content-shell">
      <JobSearchBar onSearch={handleSearch} />
      <div className="jobs-header"><div><span className="eyebrow">Open opportunities</span><h1>Find work that fits.</h1><p>Explore roles from teams building what comes next.</p></div><span className="job-count">{totalElements ? `${totalElements} roles available` : 'Live job board'}</span></div>
      {error && <div className="job-error">{error}</div>}
      <div className="jobs-layout">
        <div className="job-list-panel">
          <div className="list-heading"><span>{loading ? 'Refreshing roles...' : 'Latest matches'}</span><span>{jobs.length} shown</span></div>
          {loading ? (
            <div className="job-list-empty">Loading opportunities...</div>
          ) : (
            <>
              <PaginatedJobList
                jobs={jobs}
                selectedJob={selectedJob}
                onSelect={setSelectedJob}
              />

              {totalPages > 1 && (
                <div className="pagination-new">
                  <button
                    onClick={() => fetchAllJobs(currentPage - 1)}
                    disabled={currentPage === 0}
                    className="button"
                  >
                    Previous
                  </button>
                  <span>
                    Page {currentPage + 1} of {totalPages}
                  </span>
                  <button
                    onClick={() => fetchAllJobs(currentPage + 1)}
                    disabled={currentPage === totalPages - 1}
                    className="button"
                  >
                    Next
                  </button>
                </div>
              )}
            </>
          )}
        </div>

        <div className="job-detail-sticky">
            {selectedJob ? (
              <JobDetails job={selectedJob} />
            ) : (
              <div className="job-list-empty">Select a role to see the full details.</div>
            )}
          </div>
      </div>
    </main></div>
  );
}
