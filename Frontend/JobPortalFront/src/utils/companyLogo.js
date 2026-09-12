export function getCompanyInitials(name) {
  if (!name || typeof name !== 'string') return 'C';
  const cleanName = name.trim().replace(/[^a-zA-Z0-9 ]/g, '');
  if (!cleanName) return 'C';
  
  const words = cleanName.split(/\s+/);
  if (words.length >= 2) {
    return (words[0][0] + words[1][0]).toUpperCase();
  }
  return cleanName.substring(0, 2).toUpperCase();
}

export function getDeterministicColor(name) {
  if (!name || typeof name !== 'string') return '#6B3F27'; // Default brown

  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }

  // Pre-defined set of professional, readable background colors
  const colors = [
    '#2C3E50', '#34495E', '#16A085', '#27AE60', '#2980B9', 
    '#8E44AD', '#2C2C54', '#474787', '#30336B', '#535C68',
    '#6B3F27', '#8E44AD', '#D35400', '#C0392B', '#7F8C8D'
  ];
  
  const index = Math.abs(hash) % colors.length;
  return colors[index];
}

export function generateSvgLogo(name) {
  const initials = getCompanyInitials(name);
  const color = getDeterministicColor(name);
  
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 128">
      <rect width="128" height="128" fill="${color}" />
      <text 
        x="64" 
        y="64" 
        font-family="system-ui, -apple-system, sans-serif" 
        font-size="56" 
        font-weight="bold" 
        fill="#ffffff" 
        text-anchor="middle" 
        dominant-baseline="central"
      >
        ${initials}
      </text>
    </svg>
  `.trim().replace(/\s+/g, ' ');

  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}

export function handleLogoError(e, companyName) {
  // Prevent infinite loop if the fallback itself somehow fails
  if (e.target.dataset.fallbackApplied) return;
  e.target.dataset.fallbackApplied = 'true';
  e.target.src = generateSvgLogo(companyName || 'Company');
}

export function getLogoFallback(companyName) {
  return generateSvgLogo(companyName || 'Company');
}
