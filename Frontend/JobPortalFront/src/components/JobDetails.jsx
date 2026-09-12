import { Building2, MapPin, Bookmark, BookmarkCheck, ArrowLeft, Ban } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import { getLogoFallback, handleLogoError } from "../utils/companyLogo";
import { successToast, errorToast, infoToast } from "../utils/toastUtils";
import apiClient from "../api/client";

export default function JobDetails({ job }) {
  const navigate = useNavigate();
  const [saved, setSaved] = useState(false);
  const [hasApplied, setHasApplied] = useState(false);
  const [saving, setSaving] = useState(false);
  const [rejecting, setRejecting] = useState(false);
  const [isRejected, setIsRejected] = useState(false);
  const viewedJobsRef = useRef(new Set());

  // Reset rejected state when viewed job changes
  useEffect(() => {
    setIsRejected(false);
  }, [job?.id, job?.jobEnrichmentId]);

  // Track VIEW action only when candidate actually opens/views a specific job (Constraint 1 & 4)
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token || !job) return;

    const jobKey = String(job.id || job.jobEnrichmentId || job.rawObservationId || "");
    if (!jobKey || viewedJobsRef.current.has(jobKey)) {
      return; // Deduplicated: only fires once per job per session
    }

    viewedJobsRef.current.add(jobKey);

    apiClient.post("/api/career-intelligence/actions", {
      jobId: job.id || null,
      jobEnrichmentId: job.jobEnrichmentId || null,
      actionType: "VIEW",
      metadata: "job_details_view"
    }).catch((err) => {
      console.debug("Adaptive VIEW signal not recorded:", err.message);
    });
  }, [job?.id, job?.jobEnrichmentId]);

  useEffect(() => {
    const fetchStatuses = async () => {
      const token = localStorage.getItem("token");
      if (!token || !job) return;

      try {
        const { data: savedJobs } = await apiClient.get("/user/saved-jobs");
        setSaved(savedJobs.some((savedJob) => savedJob.id === job.id));

        const { data: applied } = await apiClient.get(`/applications/has-applied/${job.id}`);
        setHasApplied(applied);
      } catch (err) {
        console.error("Error fetching status:", err);
      }
    };

    fetchStatuses();
  }, [job]);

  const formatDate = (dateStr) => {
    const options = { year: "numeric", month: "long", day: "numeric" };
    return new Date(dateStr).toLocaleDateString(undefined, options);
  };

  const handleApply = () => {
    if (job.id) {
      navigate(`/apply/${job.id}`);
    } else if (job.sourceJobUrl) {
      window.open(job.sourceJobUrl, '_blank');
    } else {
      infoToast("External application link not available.");
    }
  };

  const toggleSave = async () => {
    if (!localStorage.getItem("token")) {
      infoToast("You need to be logged in to save jobs.");
      return;
    }
    
    if (!job.id) {
      infoToast("This is an external job recommendation and cannot be saved directly yet.");
      return;
    }

    setSaving(true);
    try {
      saved
        ? await apiClient.delete(`/user/unsave-job/${job.id}`)
        : await apiClient.post(`/user/save-job/${job.id}`);
      setSaved(!saved);
      successToast(saved ? "Job removed from saved list." : "Job saved successfully!");
    } catch (err) {
      console.error(err);
      const errorMsg = err.response?.data?.message ?? `Failed to ${saved ? "unsave" : "save"} job.`;
      errorToast(errorMsg);
    } finally {
      setSaving(false);
    }
  };

  const handleReject = async () => {
    if (!localStorage.getItem("token")) {
      infoToast("You need to be logged in.");
      return;
    }

    setRejecting(true);
    try {
      await apiClient.post("/api/career-intelligence/actions", {
        jobId: job.id || null,
        jobEnrichmentId: job.jobEnrichmentId || null,
        actionType: "REJECT",
        metadata: "not_interested_button"
      });
      setIsRejected(true);
      infoToast("Marked as Not Interested. We'll adjust your recommendations.");
    } catch (err) {
      console.error("Failed to record REJECT action:", err);
      errorToast("Could not record preference.");
    } finally {
      setRejecting(false);
    }
  };

  if (!job)
    return <div className="text-gray-500">Select a job to view details</div>;

  return (
    <div className="job-detail-new">
      {/* Back Button */}
      <button
        onClick={() => navigate(-1)}
        className="text-button"
      >
        <ArrowLeft size={14} /> Back
      </button>

      {/* Action Buttons */}
      <div className="detail-actions">
        <button
          onClick={handleReject}
          disabled={rejecting || isRejected}
          className={`button button-ghost ${isRejected ? 'opacity-60' : ''}`}
          style={{ minHeight: "38px", padding: "0 12px", fontSize: ".8rem" }}
          title="Not interested in this role"
        >
          <Ban size={15} />
          {isRejected ? "Not Interested" : "Not Interested"}
        </button>

        <button
          onClick={toggleSave}
          disabled={saving || !job.id}
          className="icon-button"
          title={saved ? "Unsave job" : "Save job"}
        >
          {saving ? (
            <div className="w-4 h-4 border-2 border-[#6B3F27] border-t-transparent rounded-full animate-spin" />
          ) : saved ? (
            <BookmarkCheck size={19} />
          ) : (
            <Bookmark size={19} />
          )}
        </button>

        <button
          onClick={
            hasApplied ? () => navigate("/myjobs?tab=applied") : handleApply
          }
          className="button button-primary"
        >
          {hasApplied ? "Applied – View Application" : (!job.id && job.sourceJobUrl ? "Apply on Company Site" : "Apply")}
        </button>
      </div>

      {/* Job Title & Logo */}
      <div className="job-detail-top">
        <div className="job-card-logo">
          <img
            src={job.profilePicture || getLogoFallback(job.companyName)}
            onError={(e) => handleLogoError(e, job.companyName)}
            alt={job.companyName || "Company Logo"}
            className="job-card-logo-image"
          />
        </div>
        <div><h2>{job.title}</h2><p className="detail-company"><Building2 size={14} /> {job.companyName}</p><p className="detail-location"><MapPin size={14} /> {job.location}</p></div>
      </div>

      {/* Job Meta Tags */}
      <div className="detail-tags">
        <span className="detail-tag">{job.employmentType || "Full-Time"}</span>
        <span className="detail-tag">{job.workMode || "On-site"}</span>
        {job.postedAt && (
          <span className="detail-tag">
            Posted: {formatDate(job.postedAt)}
          </span>
        )}
      </div>

      {/* Description */}
      {job.description?.trim() && (
        <div className="detail-content">
          <h3>
            Job Description
            </h3>
          <p>
            {job.description}
          </p>
        </div>
      )}

      {/* Responsibilities */}
      {Array.isArray(job.responsibilities) && job.responsibilities.length > 0 && (
        <div className="detail-content">
          <h3>
            Responsibilities
            </h3>
          <ul>
            {job.responsibilities.map((item, idx) => (
              <li key={idx}>{item}</li>
            ))}
          </ul>
        </div>
      )}

      {/* Skills */}
      {Array.isArray(job.requiredSkills) && job.requiredSkills.length > 0 && (
        <div className="detail-content">
          <h3>
            Required Skills
            </h3>
          <ul>
            {job.requiredSkills.map((item, idx) => (
              <li key={idx}>{item}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
