import { Star, Briefcase, MapPin } from 'lucide-react';
import { getLogoFallback, handleLogoError } from '../utils/companyLogo';

export default function RecommendationCard({ recommendation, isSelected, onSelect }) {
  const score = Math.round(recommendation.overallScore ?? recommendation.hybridScore ?? 0);
  const hybrid = recommendation.hybridScore != null ? Math.round(recommendation.hybridScore) : null;

  const confidenceBadgeClass =
    recommendation.confidence === 'LOW'
      ? 'status-badge danger'
      : recommendation.confidence === 'MEDIUM'
        ? 'status-badge pending'
        : 'status-badge';

  return (
    <div
      onClick={() => onSelect(recommendation)}
      className={`job-card-new${isSelected ? ' selected' : ''}`}
    >
      {/* Logo */}
      <div className="job-card-logo">
        <img
          src={recommendation.profilePicture || getLogoFallback(recommendation.companyName)}
          onError={(e) => handleLogoError(e, recommendation.companyName)}
          alt={recommendation.companyName}
          className="job-card-logo-image"
        />
      </div>

      {/* Info */}
      <div className="job-card-copy" style={{ flex: 1, minWidth: 0 }}>
        <h2>{recommendation.title}</h2>
        <p><Briefcase size={12} /> {recommendation.companyName}</p>
        <p><MapPin size={12} /> {recommendation.location}</p>

        <div className="job-card-tags" style={{ flexWrap: 'wrap', rowGap: 4 }}>
          {recommendation.confidence && (
            <span className={confidenceBadgeClass}>{recommendation.confidence}</span>
          )}
          <span>{score}% match</span>
          {hybrid != null && <span style={{ background: '#f0e8ff', color: '#6d3fce' }}>{hybrid}% hybrid</span>}
          {recommendation.adaptiveScore != null && (
            <span style={{ background: '#e0f2fe', color: '#0369a1' }}>{Math.round(recommendation.adaptiveScore)}% adaptive</span>
          )}
          {(recommendation.employmentType || recommendation.workMode) && (
            <span>{[recommendation.employmentType, recommendation.workMode].filter(Boolean).join(' · ')}</span>
          )}
        </div>

        {recommendation.shortExplanation && (
          <p style={{ marginTop: 8, fontSize: '.74rem', color: 'var(--muted)', lineHeight: 1.5, overflow: 'hidden', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical' }}>
            {recommendation.shortExplanation}
          </p>
        )}
      </div>
    </div>
  );
}
