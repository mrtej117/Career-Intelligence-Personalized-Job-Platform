import { useNavigate } from 'react-router-dom';
import { ArrowRight, BarChart3, Check, Compass, FileText, Sparkles, Users } from 'lucide-react';
import { motion as Motion } from 'framer-motion';
import Footer from '../components/Footer';

export default function Home() {
  const navigate = useNavigate();

  return (
    <div className="home-page"><main>
      <section className="hero-section content-shell">
        <Motion.div className="hero-copy" initial={{ opacity: 0, y: 18 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: .55 }}><span className="eyebrow"><Sparkles size={15} /> Your next chapter, intelligently found</span><h1>Work that moves <em>you</em> forward.</h1><p className="hero-lede">JobPort pairs your ambition with meaningful opportunities, using a clearer, more human way to find your next role.</p><div className="hero-actions"><button className="button button-primary button-large" onClick={() => navigate('/jobs')}>Explore open roles <ArrowRight size={17} /></button><button className="text-button" onClick={() => navigate('/jobs')}>See how it works <ArrowRight size={15} /></button></div><div className="hero-proof"><span><Check size={14} /> Curated opportunities</span><span><Check size={14} /> Built for your growth</span></div></Motion.div>
        <Motion.div className="hero-visual" initial={{ opacity: 0, scale: .96 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: .65, delay: .1 }}><div className="visual-orbit orbit-one" /><div className="visual-orbit orbit-two" /><div className="career-panel"><div className="panel-kicker">Your career signal</div><div className="signal-row"><span className="signal-dot" /><strong>Momentum is building</strong><span className="signal-value">+28%</span></div><div className="signal-chart"><i /><i /><i /><i /><i /><i /><i /></div><div className="panel-divider" /><div className="panel-kicker">A role worth exploring</div><div className="mini-job"><span className="mini-logo">N</span><div><strong>Product Designer</strong><small>Northstar · Remote</small></div><ArrowRight size={17} /></div></div><div className="floating-stat"><span className="stat-icon"><Users size={16} /></span><strong>12k+</strong><small>people growing</small></div></Motion.div>
      </section>
      <section className="trust-strip"><div className="content-shell trust-inner"><span>Designed for people who are ready for more</span><div><strong>DISCOVER</strong><strong>CREATE</strong><strong>LEAD</strong><strong>THRIVE</strong></div></div></section>
      <section className="feature-section content-shell"><div className="section-heading"><span className="eyebrow">A better way to move</span><h2>Find clarity in your career search.</h2><p>Everything you need to make a confident next move, in one focused place.</p></div><div className="feature-grid"><Feature icon={<Compass />} title="Discover with purpose" text="Search roles that match the way you want to work and grow." /><Feature icon={<BarChart3 />} title="See your potential" text="Understand the signal behind a role before you spend your time applying." /><Feature icon={<FileText />} title="Show your best work" text="Bring your experience forward with a profile that feels like you." /></div></section>
    </main><Footer /></div>
  );
}

function Feature({ icon, title, text }) { return <article className="feature-card"><span className="feature-icon">{icon}</span><h3>{title}</h3><p>{text}</p><ArrowRight size={17} className="feature-arrow" /></article>; }
