// src/components/InterviewCard.jsx
import { FaBuilding, FaCalendarAlt, FaMapMarkerAlt, FaUserTie } from "react-icons/fa";

export default function InterviewCard({ interview }) {
  return (
    <div className="border-b py-4 px-2 hover:bg-gray-50 transition flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3 sm:gap-0">
      {/* Left */}
      <div className="flex items-start gap-4 min-w-0 flex-1">
        <div className="w-12 h-12 flex-shrink-0 bg-gray-200 rounded flex items-center justify-center text-gray-500 text-xl">
          🏢
        </div>
        <div className="min-w-0">
          <h2 className="text-lg font-semibold truncate">{interview.jobTitle}</h2>
          <p className="text-sm text-gray-600 truncate">{interview.company}</p>
          <div className="text-sm text-gray-600 flex items-center gap-2 mt-1">
            <FaCalendarAlt className="flex-shrink-0" /> {interview.date} at {interview.time}
          </div>
          <div className="text-sm text-gray-600 flex items-center gap-2 mt-1">
            <FaMapMarkerAlt className="flex-shrink-0" /> {interview.mode}
          </div>
          {interview.contact && (
            <div className="text-sm text-gray-600 flex items-center gap-2 mt-1">
              <FaUserTie className="flex-shrink-0" /> {interview.contact}
            </div>
          )}
        </div>
      </div>

      {/* Right */}
      <div className="flex gap-3 items-center flex-shrink-0 sm:ml-4">
        {interview.link && (
          <a
            href={interview.link}
            target="_blank"
            rel="noopener noreferrer"
            className="text-sm bg-green-600 text-white px-4 py-2 min-h-[44px] flex items-center rounded hover:bg-green-700 whitespace-nowrap"
          >
            Join Meeting
          </a>
        )}
        <button className="text-sm text-red-600 hover:underline px-2 min-h-[44px]">Cancel</button>
      </div>
    </div>
  );
}
