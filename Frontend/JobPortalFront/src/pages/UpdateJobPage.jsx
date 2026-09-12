import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import Swal from "sweetalert2";
import { useNavigate, useParams } from "react-router-dom";
import API_URL from "../api/config";

export default function UpdateJobPage() {
  const { jobId } = useParams();
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
  const token = localStorage.getItem("token");

  const logoUrl = user?.profilePicture || "/default-logo.png";
  const companyName = user?.companyName || user?.name || "Your Company";

  useEffect(() => {
    fetch(`${API_URL}/jobs/${jobId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Unauthorized or failed to fetch");
        return res.json();
      })
      .then((data) => {
        setJob({
          title: data.title || "",
          description: data.description || "",
          location: data.location || "",
          type: data.type || "",
          workMode: data.workMode || "",
          responsibilities: data.responsibilities || [],
          requiredSkills: data.requiredSkills || [],
          screeningQuestions: data.screeningQuestions || [],
        });
      })
      .catch((err) => {
        console.error("❌ Failed to fetch job:", err);
        toast.error("🚫 Failed to load job. Please check your credentials.");
      });
  }, [jobId, token]);

  const handleChange = (e) => {
    setJob({ ...job, [e.target.name]: e.target.value });
  };

  const handleListInput = (key) => {
    if (inputField.trim()) {
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
          <p className="text-gray-600 text-sm sm:text-base mt-1">You're updating an existing job post.</p>
        </div>
      </div>

      <div className="w-full max-w-3xl mx-auto bg-white p-6 sm:p-8 rounded-xl shadow-lg border border-gray-200">
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
            <h2 className="text-xl sm:text-2xl font-semibold mb-4 text-[#6B3F27]">Step 1: Job Basics</h2>
            <label className="block mb-2 font-medium">Job Title</label>
            <input
              name="title"
              value={job.title}
              onChange={handleChange}
              placeholder="e.g. Frontend Developer"
              className="w-full border border-gray-300 p-2 rounded mb-4 focus:outline-[#6B3F27]"
            />
            <label className="block mb-2 font-medium">Description</label>
            <textarea
              name="description"
              value={job.description}
              onChange={handleChange}
              placeholder="Describe the job..."
              className="w-full border border-gray-300 p-2 rounded focus:outline-[#6B3F27]"
              rows={4}
            />
          </>
        )}

        {step === 2 && (
          <>
            <h2 className="text-xl font-semibold mb-4 text-[#6B3F27]">Step 2: Job Details</h2>
            <label className="block mb-2 font-medium">Location</label>
            <input
              name="location"
              value={job.location}
              onChange={handleChange}
              placeholder="e.g. New York, NY"
              className="w-full border border-gray-300 p-2 rounded mb-4"
            />
            <label className="block mb-2 font-medium">Job Type</label>
            <select
              name="type"
              value={job.type}
              onChange={handleChange}
              className="w-full border border-gray-300 p-2 rounded mb-4"
            >
              <option value="">Select type</option>
              <option value="FULL_TIME">Full Time</option>
              <option value="PART_TIME">Part Time</option>
              <option value="INTERNSHIP">Internship</option>
              <option value="CONTRACT">Contract</option>
            </select>
            <label className="block mb-2 font-medium">Work Mode</label>
            <select
              name="workMode"
              value={job.workMode}
              onChange={handleChange}
              className="w-full border border-gray-300 p-2 rounded"
            >
              <option value="">Select mode</option>
              <option value="ONSITE">Onsite</option>
              <option value="REMOTE">Remote</option>
              <option value="HYBRID">Hybrid</option>
            </select>
          </>
        )}

        {["responsibilities", "requiredSkills", "screeningQuestions"].map((key, idx) => {
          const stepNumber = idx + 3;
          const titles = {
            responsibilities: "Step 3: Responsibilities",
            requiredSkills: "Step 4: Required Skills",
            screeningQuestions: "Step 5: Screening Questions",
          };
          const placeholders = {
            responsibilities: "e.g. Build UI components",
            requiredSkills: "e.g. React, JavaScript",
            screeningQuestions: "e.g. What is your expected salary?",
          };

          return (
            step === stepNumber && (
              <div key={key}>
                <h2 className="text-xl font-semibold mb-4 text-[#6B3F27]">{titles[key]}</h2>
                <div className="flex flex-col sm:flex-row gap-2 mb-2">
                  <input
                    value={inputField}
                    onChange={(e) => setInputField(e.target.value)}
                    className="w-full flex-1 border p-2 rounded"
                    placeholder={placeholders[key]}
                  />
                  <button
                    onClick={() => handleListInput(key)}
                    className="w-full sm:w-auto bg-[#6B3F27] text-white px-4 py-2 rounded"
                  >
                    Add
                  </button>
                </div>
                <div className="grid gap-2 mt-4">
                  {job[key].map((item, i) => (
                    <div
                      key={i}
                      className="flex items-center justify-between bg-gray-100 px-4 py-2 rounded shadow-sm"
                    >
                      <span className="font-medium text-gray-700">
                        {i + 1}. {item}
                      </span>
                      <button
                        onClick={() => removeItem(key, i)}
                        className="text-red-600 hover:text-red-800 text-sm"
                        title="Remove"
                      >
                        ✕
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            )
          );
        })}

        <div className="mt-8 flex justify-between">
          {step > 1 && (
            <button
              onClick={() => setStep(step - 1)}
              className="px-4 py-2 bg-gray-300 text-gray-800 rounded hover:bg-gray-400"
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
                  const res = await fetch(`${API_URL}/jobs/${jobId}`, {
                    method: "PUT",
                    headers: {
                      "Content-Type": "application/json",
                      Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify(job),
                  });

                  if (!res.ok) throw new Error("Failed to update job");

                  const result = await Swal.fire({
                    title: "✅ Job Updated!",
                    text: "Do you want to preview the job now?",
                    icon: "success",
                    showCancelButton: true,
                    confirmButtonText: "Yes, show me",
                    cancelButtonText: "No, stay here",
                  });

                  if (result.isConfirmed) {
                    navigate(`/employer/jobs/${jobId}`);
                  }
                } catch (err) {
                  Swal.fire("Error", err.message, "error");
                }
              }}
              className="px-4 py-2 bg-[#6B3F27] text-white rounded hover:bg-[#5C3421]"
            >
              Update Job
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
