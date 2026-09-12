import JobCard from "./JobCard";

export default function PaginatedJobList({ jobs, selectedJob, onSelect }) {
  if (jobs.length === 0) {
    return <p className="text-gray-600">No jobs found.</p>;
  }

  return (
    <>
      {jobs.map((job) => (
        <JobCard
          key={job.id}
          job={job}
          isSelected={selectedJob?.id === job.id}
          onSelect={onSelect}
        />
      ))}
    </>
  );
}
