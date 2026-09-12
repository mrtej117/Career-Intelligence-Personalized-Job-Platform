// src/components/RecentJobCard.jsx
import { FaMapMarkerAlt } from "react-icons/fa";

export default function RecentJobCard({ job, onClick }) {
  return (
    <div
      onClick={onClick}
      className="cursor-pointer bg-white rounded-lg p-4 shadow-sm hover:shadow-md transition"
      style={{ borderLeft: "6px solid #6B3F27" }}
    >
      <div className="font-semibold text-lg truncate">{job.title}</div>
      <div className="text-sm text-gray-500 mt-1">Posted on {job.date}</div>
      <div className="text-sm text-gray-600 flex items-center mt-1">
        <FaMapMarkerAlt className="mr-1 flex-shrink-0" /> <span className="truncate">{job.location}</span>
      </div>
      <div className="mt-2">
        <span
          className={`text-xs px-2 py-1 rounded font-medium ${
            job.status === "Open"
              ? "bg-green-100 text-green-700"
              : "bg-gray-200 text-gray-600"
          }`}
        >
          {job.status}
        </span>
      </div>
    </div>
  );
}
