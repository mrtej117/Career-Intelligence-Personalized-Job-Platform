import React from 'react';
import { BriefcaseBusiness, MapPin } from 'lucide-react';
import { getLogoFallback, handleLogoError } from '../utils/companyLogo';

export default function JobCard({ job, isSelected, onSelect }) {
  return (
    <div
      onClick={() => onSelect(job)}
      className={`job-card-new ${isSelected ? 'selected' : ''}`}
    >
      {/* Logo */}
      <div className="job-card-logo">
        <img
          src={job.profilePicture || getLogoFallback(job.companyName)}
          onError={(e) => handleLogoError(e, job.companyName)}
          alt={job.companyName}
          className="job-card-logo-image"
        />
      </div>

      {/* Job Info */}
      <div className="job-card-copy">
        <h2>{job.title}</h2>
        <p><BriefcaseBusiness size={13} /> {job.companyName}</p>
        <p><MapPin size={13} /> {job.location}</p>
        <div className="job-card-tags"><span>{job.type || job.employmentType || 'Full time'}</span><span>{job.workMode || 'On-site'}</span></div>
      </div>
    </div>
  );
}
