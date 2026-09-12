import { motion as Motion } from "framer-motion";

const reducedMotion = { duration: 0 };

export function PageTransition({ children }) {
  return <Motion.div className="page-transition" initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -8 }} transition={{ duration: 0.22, ease: "easeOut" }}>{children}</Motion.div>;
}

export function FadeIn({ children, delay = 0, className = "" }) {
  return <Motion.div className={className} initial={{ opacity: 0, y: 8 }} whileInView={{ opacity: 1, y: 0 }} viewport={{ once: true, amount: 0.12 }} transition={{ duration: 0.32, delay, ease: "easeOut" }} style={{ "--reduced-motion-duration": `${reducedMotion.duration}s` }}>{children}</Motion.div>;
}

export function StaggerContainer({ children, className = "" }) {
  return <Motion.div className={className} initial="hidden" animate="show" variants={{ hidden: {}, show: { transition: { staggerChildren: 0.045 } } }}>{children}</Motion.div>;
}

export function AnimatedCard({ children, className = "", onClick }) {
  return <Motion.div className={className} onClick={onClick} whileHover={{ y: -3 }} whileTap={{ scale: 0.99 }} transition={{ duration: 0.16, ease: "easeOut" }}>{children}</Motion.div>;
}
