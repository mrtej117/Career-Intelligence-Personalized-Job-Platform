import { useNavigate } from "react-router-dom";
import axios from "axios";
import { toast } from "react-toastify";
import API_URL from "../api/config";
import { getLogoFallback, handleLogoError } from "../utils/companyLogo";

export default function AppliedJobCard({ application, onWithdraw, onRefresh }) {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const formatDate = (isoDate) => {
    const date = new Date(isoDate);
    return date.toLocaleDateString("en-US", {
      weekday: "short",
      month: "short",
      day: "numeric",
    });
  };

  const handleWithdraw = async () => {
    try {
      await axios.delete(
        `${API_URL}/applications/${application.applicationId}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      toast.success("Application withdrawn successfully");
      if (onWithdraw) onWithdraw(application.applicationId);
      if (onRefresh) onRefresh();
    } catch (err) {
      toast.error("Failed to withdraw application");
      console.error("Error withdrawing application:", err);
    }
  };

  return (
    <div
      key={application.applicationId}
      className="flex flex-col sm:flex-row sm:justify-between sm:items-center px-4 py-4 mb-4 rounded-lg border border-gray-200 bg-white hover:shadow-sm transition gap-3 sm:gap-0"
    >
      {/* Left: Logo + Info */}
      <div className="flex items-center gap-4 min-w-0 flex-1">
        <div className="w-12 h-12 flex-shrink-0 bg-gray-100 border rounded-full overflow-hidden flex items-center justify-center">
          <img
            src={application.companyLogoUrl || getLogoFallback(application.companyName)}
            onError={(e) => handleLogoError(e, application.companyName)}
            alt="Company Logo"
            className="w-full h-full object-contain"
          />
        </div>
        <div className="min-w-0">
          <h2 className="text-lg font-semibold text-[#000000] truncate">{application.jobTitle}</h2>
          <p className="text-sm text-gray-700 truncate">{application.companyName}</p>
          <p className="text-sm text-gray-600 truncate">{application.location}</p>
          <p className="text-xs text-gray-500">
            Applied on {formatDate(application.appliedAt)}
          </p>
        </div>
      </div>

      {/* Right: Status + Actions */}
      <div className="flex flex-wrap gap-2 items-center flex-shrink-0 sm:ml-4">
        <span className="text-sm border border-gray-400 text-gray-800 px-4 py-2 rounded-full font-medium whitespace-nowrap">
          {application.status}
        </span>

        <button
          className="border border-black text-black px-4 py-2 min-h-[44px] rounded hover:bg-black hover:text-white transition text-sm whitespace-nowrap"
          onClick={() =>
            navigate(`/applications/${application.applicationId}`)
          }
        >
          View Application
        </button>

        <button
          onClick={handleWithdraw}
          className="bg-[#6B3F27] hover:bg-[#5C3421] text-white px-4 py-2 min-h-[44px] rounded text-sm transition whitespace-nowrap"
        >
          Withdraw
        </button>
      </div>
    </div>
  );
}
