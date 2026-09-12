import PropTypes from "prop-types";
import { getLogoFallback, handleLogoError } from "../utils/companyLogo";

const openResume = (url) => {
  if (!url) return;
  window.open(`https://docs.google.com/viewer?url=${encodeURIComponent(url)}`, '_blank');
};

export default function ApplicationDetailsCard({ application }) {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString(undefined, {
      dateStyle: "medium",
      timeStyle: "short",
    });
  };

  const handleResumeClick = (e) => {
    e.preventDefault();
    openResume(application.resumeUrl);
  };

  const getStatusStyle = (status) => {
    switch (status.toUpperCase()) {
      case "ACCEPTED":
        return "bg-green-100 text-green-700 border-green-300";
      case "REJECTED":
        return "bg-red-100 text-red-700 border-red-300";
      default:
        return "bg-blue-100 text-blue-700 border-blue-300";
    }
  };

  return (
    <div className="max-w-3xl mx-auto mt-4 md:mt-10 bg-white p-4 md:p-8 rounded-lg shadow-lg border border-gray-200">
      {/* Header */}
      <div className="flex flex-wrap items-center gap-4 md:gap-5 mb-5 md:mb-6">
        <div className="w-14 h-14 md:w-16 md:h-16 flex-shrink-0 rounded border bg-white flex items-center justify-center overflow-hidden">
          <img
            src={application.companyLogoUrl || getLogoFallback(application.companyName)}
            onError={(e) => handleLogoError(e, application.companyName)}
            alt="Company Logo"
            className="object-contain w-full h-full"
          />
        </div>
        <div className="min-w-0 flex-1">
          <h1 className="text-xl md:text-2xl font-bold text-gray-800 break-words">{application.jobTitle}</h1>
          <p className="text-gray-600 text-sm break-words">{application.companyName} • {application.location}</p>
        </div>
      </div>

      {/* Status Badges */}
      <div className="flex flex-wrap items-center gap-3 mb-5 md:mb-6">
        <span className="px-3 py-1 text-xs font-medium rounded-full bg-gray-100 text-gray-500 border border-gray-300">
          Applied
        </span>
        <span
          className={`px-3 py-1 text-xs font-semibold uppercase rounded-full border ${getStatusStyle(application.status)}`}
        >
          {application.status}
        </span>
      </div>

      <hr className="my-4" />

      {/* Job Details */}
      <div className="space-y-2 text-sm text-gray-800 leading-relaxed">
        <div><strong>🕓 Applied At:</strong> {formatDate(application.appliedAt)}</div>
        <div><strong>📋 Job Type:</strong> {application.jobType.replace("_", " ")}</div>
        <div><strong>💼 Work Mode:</strong> {application.workMode.replace("_", " ")}</div>
        <div><strong>📍 Location:</strong> {application.location}</div>
        <div><strong>📝 Description:</strong> {application.jobDescription}</div>
      </div>

      <hr className="my-4" />

      {/* Applicant Details */}
      <div className="space-y-2 text-sm text-gray-800 leading-relaxed">
        <div><strong>🙍 Applicant:</strong> {application.username}</div>
        <div>
          <strong>📄 Resume:</strong>{" "}
          {application.resumeUrl ? (
            <button
              onClick={handleResumeClick}
              className="text-blue-600 underline hover:text-blue-800"
            >
              View Resume
            </button>
          ) : (
            <span className="text-gray-500">Not available</span>
          )}
        </div>
        <div><strong>🆔 Application ID:</strong> {application.applicationId}</div>
      </div>
    </div>
  );
}

ApplicationDetailsCard.propTypes = {
  application: PropTypes.shape({
    applicationId: PropTypes.number,
    username: PropTypes.string,
    resumeUrl: PropTypes.string,
    status: PropTypes.string,
    appliedAt: PropTypes.string,
    jobTitle: PropTypes.string,
    jobDescription: PropTypes.string,
    jobType: PropTypes.string,
    workMode: PropTypes.string,
    location: PropTypes.string,
    companyName: PropTypes.string,
    companyLogoUrl: PropTypes.string,
  }),
};
