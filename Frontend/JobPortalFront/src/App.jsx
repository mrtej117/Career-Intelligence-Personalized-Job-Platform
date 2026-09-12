import { BrowserRouter as Router, Routes, Route, useLocation } from "react-router-dom";
import { useState } from "react";
import "react-toastify/dist/ReactToastify.css";
import { ToastContainer } from "react-toastify";
import Home from "./pages/Home";
import Jobs from "./pages/Jobs";
import ApplyPage from "./pages/ApplyPage";
import MyJobsPage from "./pages/MyJobsPage";
import JobSeekerProfilePage from "./pages/JobSeekerProfilePage";
import ApplicationDetailsPage from "./pages/ApplicationDetailsPage";
import RecommendationsPage from "./pages/RecommendationsPage";
import EmployerDashboard from "./pages/EmployerDashboard";
import EmployerJobsPage from "./pages/EmployerJobsPage";
import EmployerJobDetailsPage from "./pages/EmployerJobDetailsPage";
import CreateJobPage from "./pages/CreateJobPage";
import UpdateJobPage from "./pages/UpdateJobPage";
import EmployerApplicantsPage from "./pages/EmployerApplicantsPage";
import JobApplicantsPage from "./pages/JobApplicantsPage";
import ApplicationDetailsEmployerPage from "./pages/ApplicationDetailsEmployerPage";
import Navbar from "./components/navbar/Navbar";
import JobSeekerModal from "./modals/JobSeekerModal";
import EmployerModal from "./modals/EmployerModal";
import NotificationToastManager from "./components/NotificationToastManager";
import RequireRole from "./components/RequireRole";
import ErrorBoundary from "./components/ErrorBoundary";
import { PageTransition } from "./components/common/Motion";

function App() {
  const [showJobSeekerModal, setShowJobSeekerModal] = useState(false);
  const [showEmployerModal, setShowEmployerModal] = useState(false);

  return (
    <Router>
      <Navbar onOpenJobSeeker={() => setShowJobSeekerModal(true)} onOpenEmployer={() => setShowEmployerModal(true)} />
      <ToastContainer position="top-center" autoClose={2000} pauseOnHover />
      <NotificationToastManager />
      <ErrorBoundary><AnimatedRoutes /></ErrorBoundary>
      <JobSeekerModal isOpen={showJobSeekerModal} onClose={() => setShowJobSeekerModal(false)} />
      <EmployerModal isOpen={showEmployerModal} onClose={() => setShowEmployerModal(false)} />
    </Router>
  );
}

function AnimatedRoutes() {
  const location = useLocation();
  return <PageTransition key={location.pathname}><Routes location={location}>
    <Route path="/" element={<Home />} />
    <Route path="/jobs" element={<Jobs />} />
    <Route path="/recommendations" element={<RequireRole role="JOB_SEEKER"><RecommendationsPage /></RequireRole>} />
    <Route path="/myjobs" element={<RequireRole role="JOB_SEEKER"><MyJobsPage /></RequireRole>} />
    <Route path="/apply/:jobId" element={<RequireRole role="JOB_SEEKER"><ApplyPage /></RequireRole>} />
    <Route path="/profile" element={<RequireRole role="JOB_SEEKER"><JobSeekerProfilePage /></RequireRole>} />
    <Route path="/applications/:id" element={<RequireRole role="JOB_SEEKER"><ApplicationDetailsPage /></RequireRole>} />
    <Route path="/dashboard" element={<RequireRole role="EMPLOYER"><EmployerDashboard /></RequireRole>} />
    <Route path="/employer/jobs" element={<RequireRole role="EMPLOYER"><EmployerJobsPage /></RequireRole>} />
    <Route path="/employer/jobs/:jobId" element={<RequireRole role="EMPLOYER"><EmployerJobDetailsPage /></RequireRole>} />
    <Route path="/create-job" element={<RequireRole role="EMPLOYER"><CreateJobPage /></RequireRole>} />
    <Route path="/employer/jobs/update/:jobId" element={<RequireRole role="EMPLOYER"><UpdateJobPage /></RequireRole>} />
    <Route path="/employer/applicants" element={<RequireRole role="EMPLOYER"><EmployerApplicantsPage /></RequireRole>} />
    <Route path="/employer/jobs/:jobId/applicants" element={<RequireRole role="EMPLOYER"><JobApplicantsPage /></RequireRole>} />
    <Route path="/employer/applications/:id" element={<RequireRole role="EMPLOYER"><ApplicationDetailsEmployerPage /></RequireRole>} />
  </Routes></PageTransition>;
}

export default App;
