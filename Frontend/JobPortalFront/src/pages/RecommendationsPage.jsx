import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import JobDetails from "../components/JobDetails";
import RecommendationCard from "../components/RecommendationCard";
import apiClient from "../api/client";
import { Sparkles, Target, TrendingUp } from "lucide-react";
import { FadeIn, StaggerContainer, AnimatedCard } from "../components/common/Motion";

export default function RecommendationsPage() {
  const [recommendations, setRecommendations] = useState([]);
  const [selectedRecommendation, setSelectedRecommendation] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchRecommendations();
  }, []);

  const fetchRecommendations = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.get(`/recommendations?page=0&size=20`, { timeout: 120000 });
      const content = Array.isArray(res.data?.content) ? res.data.content : null;
      if (!content) {
        throw new Error("Unexpected recommendations response");
      }

      setRecommendations(content);
      if (content.length > 0) {
        setSelectedRecommendation(content[0]);
      }
    } catch (err) {
      console.error(err);
      const status = err.response?.status;
      if (status === 401 || status === 403) {
        setError("Your session has expired. Please sign in again.");
      } else if (status >= 500) {
        setError("The recommendation service is temporarily unavailable. Please try again later.");
      } else if (!err.response) {
        setError("Could not reach the recommendation service. Check your connection and try again.");
      } else if (status >= 400) {
        setError("The recommendation request was rejected. Please refresh your profile and try again.");
      } else {
        setError("The recommendation service returned an unexpected response.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-shell"><main>
        <div className="page-heading"><div><span className="eyebrow"><Sparkles size={15} /> Personalised career signal</span><h1>Roles made for your next move.</h1><p>AI-assisted matches based on your experience, goals, and direction.</p></div><div className="status-badge"><Target size={14} /> Live match engine</div></div>
        
        {error && <div className="job-error">{error}</div>}
        {loading && (
          <div className="empty-state surface"><TrendingUp size={28} /><p>Loading your personalised matches...</p><small>This may take a moment.</small>
          </div>
        )}
        
        {!loading && !error && recommendations.length === 0 && (
          <div className="empty-state surface"><h2>No recommendations yet</h2><p>Upload your resume to unlock personalised job matches.</p><button onClick={() => navigate('/profile')} className="button button-primary">Update profile</button>
          </div>
        )}

        {!loading && recommendations.length > 0 && (
          <div className="recommendation-layout">
            {/* List */}
            <StaggerContainer className="recommendation-list">
              {recommendations.map((rec) => (
                <AnimatedCard key={rec.matchResultId} className="recommendation-item"><RecommendationCard
                  key={rec.matchResultId}
                  recommendation={rec}
                  isSelected={selectedRecommendation?.matchResultId === rec.matchResultId}
                  onSelect={setSelectedRecommendation}
                /></AnimatedCard>
              ))}
            </StaggerContainer>

            {/* Details */}
            <FadeIn className="recommendation-detail">
              {selectedRecommendation ? (
                <>
                  <div className="match-summary surface surface-pad">
                    <div className="flex justify-between items-start mb-4">
                      <h3>Why this role matches you</h3>
                      {selectedRecommendation.hybridScore !== undefined && selectedRecommendation.hybridScore !== null && (
                        <div className="match-scores">
                          <div className="text-gray-600">Deterministic Match: <span className="font-bold text-gray-800">{Math.round(selectedRecommendation.deterministicScore)}%</span></div>
                          <div className="text-gray-600">Semantic Similarity: <span className="font-bold text-gray-800">{Math.round(selectedRecommendation.semanticScore)}%</span></div>
                          <div className="status-badge">Hybrid score: {Math.round(selectedRecommendation.hybridScore)}%</div>
                          {selectedRecommendation.preferenceScore !== undefined && selectedRecommendation.preferenceScore !== null && (
                            <div className="text-gray-600">Learned Preference Fit: <span className="font-bold text-gray-800">{Math.round(selectedRecommendation.preferenceScore)}%</span></div>
                          )}
                          {selectedRecommendation.adaptiveScore !== undefined && selectedRecommendation.adaptiveScore !== null && (
                            <div className="status-badge" style={{ background: '#e0f2fe', color: '#0369a1', marginTop: 2 }}>Adaptive score: {Math.round(selectedRecommendation.adaptiveScore)}%</div>
                          )}
                        </div>
                      )}
                    </div>
                    <div className="match-columns">
                      <div>
                        <h4 className="match-positive">Strengths & matches</h4><ul>
                          {selectedRecommendation.strengths?.map((s, idx) => (
                            <li key={`str-${idx}`}>{s}</li>
                          ))}
                          {selectedRecommendation.matchedSkills?.map((s, idx) => (
                            <li key={`ms-${idx}`}><span className="font-semibold">Skill:</span> {s}</li>
                          ))}
                        </ul>
                      </div>
                      <div>
                        <h4 className="match-warning">Gaps & missing</h4><ul>
                          {selectedRecommendation.gaps?.map((g, idx) => (
                            <li key={`gap-${idx}`}>{g}</li>
                          ))}
                          {selectedRecommendation.missingSkills?.map((s, idx) => (
                            <li key={`mis-${idx}`}><span className="font-semibold">Missing:</span> {s}</li>
                          ))}
                        </ul>
                      </div>
                    </div>
                  </div>
                  <JobDetails job={selectedRecommendation} />
                </>
              ) : (
                <p className="text-gray-500">Select a recommendation to view details.</p>
              )}
            </FadeIn>
          </div>
        )}
      </main>
    </div>
  );
}
