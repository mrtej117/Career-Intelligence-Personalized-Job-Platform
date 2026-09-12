// src/pages/ApplyPage.jsx
import { useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import ScreeningQuestions from '../components/ScreeningQuestions';
import ApplicationDetails from '../components/ApplicationDetails';
import JobDetails from '../components/JobDetails';
import apiClient from '../api/client';
import { FileCheck, ShieldCheck } from 'lucide-react';
import { FadeIn } from '../components/common/Motion';

export default function ApplyPage() {
  const { jobId } = useParams();
  const [job, setJob] = useState(null);
  const [step, setStep] = useState(1);
  const [screeningAnswers, setScreeningAnswers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchJob = async () => {
      try {
        const res = await apiClient.get(`/jobs/${jobId}`);
        const data = res.data;
        setJob(data);

        if (!data.screeningQuestions || data.screeningQuestions.length === 0) {
          setStep(2);
        }
      } catch (err) {
        console.error("Error loading job:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchJob();
  }, [jobId]);

  const handleNextStep = (answers) => {
    setScreeningAnswers(answers);
    setStep(2);
  };

  if (loading) return <div className="page-shell"><div className="empty-state surface">Loading job details...</div></div>;
  if (!job) return <div className="page-shell"><div className="job-error">Job not found.</div></div>;

  return (
    <div className="page-shell"><div className="apply-layout">
      <FadeIn className="apply-form-column"><div className="page-heading"><div><span className="eyebrow"><FileCheck size={15} /> Application</span><h1>Apply with confidence.</h1><p>You're applying for <strong>{job.title}</strong> at {job.companyName}.</p></div></div>
        <div className="secure-note"><ShieldCheck size={15} /> Your information is secure</div>

        {/* Step indicator — only shown when there are screening questions */}
        {job.screeningQuestions?.length > 0 && (
          <div className="flex items-center mb-5">
            <div className={`flex items-center justify-center w-8 h-8 rounded-full text-sm font-bold flex-shrink-0 ${step >= 1 ? 'bg-[#6B3F27] text-white' : 'bg-gray-200 text-gray-500'}`}>1</div>
            <div className={`flex-1 h-0.5 mx-2 transition-colors ${step >= 2 ? 'bg-[#6B3F27]' : 'bg-gray-200'}`}></div>
            <div className={`flex items-center justify-center w-8 h-8 rounded-full text-sm font-bold flex-shrink-0 ${step >= 2 ? 'bg-[#6B3F27] text-white' : 'bg-gray-200 text-gray-500'}`}>2</div>
          </div>
        )}

        {step === 1 && (
          <ScreeningQuestions
            questions={job.screeningQuestions || []}
            onNext={handleNextStep}
          />
        )}

        {step === 2 && (
          <ApplicationDetails
            jobId={job.id}
            screeningAnswers={screeningAnswers}
          />
        )}
      </FadeIn><FadeIn className="apply-job-column">
        <JobDetails job={job} />
      </FadeIn></div></div>
  );
}