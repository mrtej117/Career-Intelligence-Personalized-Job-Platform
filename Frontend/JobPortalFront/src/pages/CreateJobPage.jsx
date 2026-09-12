import { useState } from "react";
import { toast } from "react-toastify";
import Swal from "sweetalert2";
import { useNavigate } from "react-router-dom";
import API_URL from "../api/config";

export default function CreateJobPage() {
  const [step, setStep] = useState(1);
  const [job, setJob] = useState({
    title: "",
    description: "",
    location: "",
    type: "",
    workMode: "",
    responsibilities: [],
    requiredSkills: [],
    screeningQuestions: [],
  });

  const [inputField, setInputField] = useState("");
  const navigate = useNavigate();
  let user = null;
  try { user = JSON.parse(localStorage.getItem("user")); } catch { user = null; }
  const logoUrl = user?.profilePicture || "/default-logo.png";
  const companyName = user?.companyName || user?.name || "Your Company";

  const handleChange = (e) => {
    setJob({ ...job, [e.target.name]: e.target.value });
  };

  const handleListInput = (key) => {
    if (inputField.trim()) {
      if (inputField.trim().length > 255) {
        toast.error("Item cannot exceed 255 characters.");
        return;
      }
      setJob({ ...job, [key]: [...job[key], inputField.trim()] });
      setInputField("");
    }
  };

  const removeItem = (key, index) => {
    const updated = [...job[key]];
    updated.splice(index, 1);
    setJob({ ...job, [key]: updated });
  };

  return (
    <div className="page-shell employer-page job-form-page">
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4 mb-6">
        <img
          src={logoUrl}
          alt="Company Logo"
          className="w-14 h-14 object-contain rounded border"
        />
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-[#6B3F27]">{companyName}</h1>
          <p className="text-gray-500 text-sm sm:text-base">You're now creating a new job post.</p>
        </div>
      </div>

      <div className="w-full max-w-3xl mx-auto bg-white border border-gray-200 rounded-2xl shadow-md p-6 sm:p-8">
        <div className="mb-6">
          <div className="flex items-center justify-between gap-3 text-sm sm:text-base text-gray-600 mb-2">
            <span>Step {step} of 5</span>
            <span className="font-medium">Progress</span>
          </div>
          <div className="h-2 rounded-full bg-gray-200 overflow-hidden">
            <div
              className="h-full rounded-full bg-[#6B3F27] transition-all duration-300"
              style={{ width: `${(step / 5) * 100}%` }}
            />
          </div>
        </div>

        {step === 1 && (
          <>
            <h2 className="text-xl sm:text-2xl font-bold text-[#6B3F27] mb-4">Step 1: Job Basics</h2>
            <label className="block mb-1 font-medium">Job Title</label>
            <input
              name="title"
              value={job.title}
              onChange={handleChange}
              placeholder="e.g. Frontend Developer"
              className="w-full border rounded px-3 py-2 mb-4 focus:ring-2 focus:ring-[#6B3F27]"
            />
            <label className="block mb-1 font-medium">Description</label>
            <textarea
              name="description"
              value={job.description}
              onChange={handleChange}
              placeholder="Describe the job..."
              className="w-full border rounded px-3 py-2 focus:ring-2 focus:ring-[#6B3F27]"
              rows={5}
            />
          </>
        )}

        {step === 2 && (
          <>
            <h2 className="text-xl font-bold text-[#6B3F27] mb-4">Step 2: Job Details</h2>
            <label className="block mb-1 font-medium">Location</label>
            <input
              name="location"
              value={job.location}
              onChange={handleChange}
              placeholder="e.g. New York, NY"
              className="w-full border rounded px-3 py-2 mb-4 focus:ring-2 focus:ring-[#6B3F27]"
            />
            <label className="block mb-1 font-medium">Job Type</label>
            <select
              name="type"
              value={job.type}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2 mb-4 focus:ring-2 focus:ring-[#6B3F27]"
            >
              <option value="">Select type</option>
              <option value="FULL_TIME">Full Time</option>
              <option value="PART_TIME">Part Time</option>
              <option value="INTERNSHIP">Internship</option>
              <option value="CONTRACT">Contract</option>
            </select>
            <label className="block mb-1 font-medium">Work Mode</label>
            <select
              name="workMode"
              value={job.workMode}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2 focus:ring-2 focus:ring-[#6B3F27]"
            >
              <option value="">Select mode</option>
              <option value="ONSITE">Onsite</option>
              <option value="REMOTE">Remote</option>
              <option value="HYBRID">Hybrid</option>
            </select>
          </>
        )}

        {step === 3 && (
          <>
            <h2 className="text-xl sm:text-2xl font-bold text-[#6B3F27] mb-4">Step 3: Responsibilities</h2>
            <div className="flex flex-col sm:flex-row gap-2 mb-2">
              <input
                value={inputField}
                onChange={(e) => setInputField(e.target.value)}
                className="w-full flex-1 border p-2 rounded"
                placeholder="e.g. Build UI components"
              />
              <button
                onClick={() => handleListInput("responsibilities")}
                className="w-full sm:w-auto bg-[#6B3F27] text-white px-4 py-2 rounded"
              >
                Add
              </button>
            </div>
            <div className="grid gap-2 mt-4">
              {job.responsibilities.map((item, i) => (
                <div
                  key={i}
                  className="flex justify-between items-center bg-gray-100 px-4 py-2 rounded shadow-sm"
                >
                  <span className="text-gray-800 font-medium">{i + 1}. {item}</span>
                  <button
                    onClick={() => removeItem("responsibilities", i)}
                    className="text-red-600 hover:text-red-800"
                  >✕</button>
                </div>
              ))}
            </div>
          </>
        )}

        {step === 4 && (
          <>
            <h2 className="text-xl sm:text-2xl font-bold text-[#6B3F27] mb-4">Step 4: Required Skills</h2>
            <div className="flex flex-col sm:flex-row gap-2 mb-2">
              <input
                value={inputField}
                onChange={(e) => setInputField(e.target.value)}
                className="w-full flex-1 border p-2 rounded"
                placeholder="e.g. React, JavaScript"
              />
              <button
                onClick={() => handleListInput("requiredSkills")}
                className="w-full sm:w-auto bg-[#6B3F27] text-white px-4 py-2 rounded"
              >
                Add
              </button>
            </div>
            <div className="grid gap-2 mt-4">
              {job.requiredSkills.map((item, i) => (
                <div
                  key={i}
                  className="flex justify-between items-center bg-gray-100 px-4 py-2 rounded shadow-sm"
                >
                  <span className="text-gray-800 font-medium">{i + 1}. {item}</span>
                  <button
                    onClick={() => removeItem("requiredSkills", i)}
                    className="text-red-600 hover:text-red-800"
                  >✕</button>
                </div>
              ))}
            </div>
          </>
        )}

        {step === 5 && (
          <>
            <h2 className="text-xl sm:text-2xl font-bold text-[#6B3F27] mb-4">Step 5: Screening Questions</h2>
            <div className="flex flex-col sm:flex-row gap-2 mb-2">
              <input
                value={inputField}
                onChange={(e) => setInputField(e.target.value)}
                className="w-full flex-1 border p-2 rounded"
                placeholder="e.g. What is your expected salary?"
              />
              <button
                onClick={() => handleListInput("screeningQuestions")}
                className="w-full sm:w-auto bg-[#6B3F27] text-white px-4 py-2 rounded"
              >
                Add
              </button>
            </div>
            <div className="grid gap-2 mt-4">
              {job.screeningQuestions.map((item, i) => (
                <div
                  key={i}
                  className="flex justify-between items-center bg-gray-100 px-4 py-2 rounded shadow-sm"
                >
                  <span className="text-gray-800 font-medium">{i + 1}. {item}</span>
                  <button
                    onClick={() => removeItem("screeningQuestions", i)}
                    className="text-red-600 hover:text-red-800"
                  >✕</button>
                </div>
              ))}
            </div>
          </>
        )}

        <div className="mt-8 flex justify-between">
          {step > 1 && (
            <button
              onClick={() => setStep(step - 1)}
              className="px-4 py-2 bg-gray-300 text-gray-800 rounded"
            >
              Back
            </button>
          )}
          {step < 5 ? (
            <button
              onClick={() => {
                if (
                  (step === 1 && (!job.title.trim() || !job.description.trim())) ||
                  (step === 2 && (!job.location.trim() || !job.type || !job.workMode)) ||
                  (step === 3 && job.responsibilities.length === 0) ||
                  (step === 4 && job.requiredSkills.length === 0)
                ) {
                  toast.error("🚫 Please complete all required fields before proceeding.");
                  return;
                }
                setStep(step + 1);
              }}

              className="px-4 py-2 bg-[#6B3F27] text-white rounded hover:bg-[#5C3421]"
            >
              Next
            </button>
          ) : (
            <button
              onClick={async () => {
                try {
                  const token = localStorage.getItem("token");
                  const res = await fetch(`${API_URL}/jobs`, {
                    method: "POST",
                    headers: {
                      "Content-Type": "application/json",
                      Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify(job),
                  });

                  if (!res.ok) {
                    const errData = await res.json().catch(() => ({}));
                    throw new Error(errData.message || "Failed to create job");
                  }

                  const data = await res.json();
                  const jobId = data.jobId;

                  const result = await Swal.fire({
                    title: "🎉 Job Created!",
                    text: "Do you want to preview the job now?",
                    icon: "success",
                    showCancelButton: true,
                    confirmButtonText: "Yes, show me",
                    cancelButtonText: "No, stay here",
                  });

                  if (result.isConfirmed && jobId) {
                    navigate(`/employer/jobs/${jobId}`);
                  } else if (!jobId) {
                    Swal.fire("⚠️ Job created, but preview ID not available.");
                  }
                } catch (err) {
                  Swal.fire("Error", err.message, "error");
                }
              }}
              className="px-4 py-2 bg-[#6B3F27] text-white rounded"
            >
              Submit Job
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
