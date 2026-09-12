import { useState } from 'react';
import { Search, MapPin, SlidersHorizontal, X } from 'lucide-react';

const jobTypeOptions = [
  { value: '', label: 'All types' },
  { value: 'FULL_TIME', label: 'Full Time' },
  { value: 'PART_TIME', label: 'Part Time' },
  { value: 'CONTRACT', label: 'Contract' },
  { value: 'INTERNSHIP', label: 'Internship' },
];

export default function JobSearchBar({ onSearch }) {
  const [title, setTitle] = useState('');
  const [location, setLocation] = useState('');
  const [type, setType] = useState(jobTypeOptions[0]);

  const handleClick = () => {
    onSearch({ title, location, type: type.value });
  };

  const handleClear = () => {
    setTitle('');
    setLocation('');
    setType(jobTypeOptions[0]);
    onSearch({ title: '', location: '', type: '' });
  };

  const hasValue = title || location || type.value;

  return (
    <div className="job-search-wrap"><div className="job-search">

        {/* Job title input */}
        <label className="search-field"><Search size={18} />
          <input
            type="text"
            placeholder="Find your perfect job"
            className="search-input"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </label>

        {/* Location input */}
        <label className="search-field"><MapPin size={18} />
          <input
            type="text"
            placeholder="Enter city or country"
            className="search-input"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
          />
        </label>

        {/* Job type dropdown using react-select */}
        <label className="search-select"><SlidersHorizontal size={17} /><select value={type.value} onChange={(event) => setType(jobTypeOptions.find((option) => option.value === event.target.value) || jobTypeOptions[0])}>{jobTypeOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}</select></label>

        {/* Clear button */}
        {hasValue && (
          <button
            onClick={handleClear}
            className="icon-button search-clear"
            title="Clear search"
          >
            <X size={17} />
          </button>
        )}

        {/* Search button */}
        <button
          onClick={handleClick}
          className="button button-primary search-submit"
        >
          Search
        </button>
      </div></div>
  );
}
