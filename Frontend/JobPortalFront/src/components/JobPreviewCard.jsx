// src/components/JobPreviewCard.jsx
export default function JobPreviewCard({ job, companyName, logoUrl }) {
  if (!job.title) return null;

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div className="flex items-center gap-4 mb-4">
        <img src={logoUrl} alt="Logo" className="w-12 h-12 rounded border" />
        <h2 className="text-2xl font-bold">{job.title}</h2>
      </div>

      <p className="text-gray-600 mb-2">
        📍 {job.location} &nbsp;|&nbsp;
        🕒 {new Date().toLocaleString()} &nbsp;|&nbsp;
        {job.type?.replace("_", " ")} &nbsp;|&nbsp;
        {job.workMode}
      </p>

      <p className="mb-2">
        <strong>Status:</strong> Open
      </p>
      <p className="mb-2">
        <strong>Company:</strong> {companyName}
      </p>
      <p className="mb-2">
        <strong>Description:</strong> {job.description}
      </p>

      <div className="mb-2">
        <strong>Responsibilities:</strong>
        <ul className="list-disc pl-5">
          {job.responsibilities.map((r, i) => (
            <li key={i}>{r}</li>
          ))}
        </ul>
      </div>

      <div className="mb-2">
        <strong>Required Skills:</strong>
        <ul className="list-disc pl-5">
          {job.requiredSkills.map((s, i) => (
            <li key={i}>{s}</li>
          ))}
        </ul>
      </div>

      <div className="mb-4">
        <strong>Screening Questions:</strong>
        <ul className="list-disc pl-5">
          {job.screeningQuestions.map((q, i) => (
            <li key={i}>{q}</li>
          ))}
        </ul>
      </div>

      <div className="flex gap-4">
        <button className="bg-blue-600 text-white px-4 py-2 rounded">Update</button>
        <button className="bg-red-600 text-white px-4 py-2 rounded">Delete</button>
      </div>
    </div>
  );
}
