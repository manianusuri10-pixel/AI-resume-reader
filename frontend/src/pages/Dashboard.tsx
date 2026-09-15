import React from 'react';
import { useAuth } from '../context/AuthContext';
import { ScoreGauge } from '../components/ScoreGauge';
import {
  FileText,
  Target,
  Map,
  MessageSquareCode,
  Database,
  CheckCircle2,
  ArrowUpRight,
  Sparkles,
  Zap,
  TrendingUp,
  Cpu
} from 'lucide-react';

interface DashboardProps {
  setActiveTab: (tab: string) => void;
  onOpenAuth: () => void;
}

export const Dashboard: React.FC<DashboardProps> = ({ setActiveTab, onOpenAuth }) => {
  const { user, isAuthenticated, demoLogin } = useAuth();

  const quickActions = [
    {
      id: 'resume',
      title: 'ATS Resume Analyzer',
      description: 'Upload your PDF or text resume for deep ATS scoring, keyword extraction, and bullet rewrites.',
      icon: FileText,
      color: 'from-blue-500/20 to-cyan-500/20 text-cyan-400 border-cyan-500/30',
      badge: 'PDFBox & NLP',
    },
    {
      id: 'job-match',
      title: 'Job Description Matcher',
      description: 'Compare your resume against any target Job Description to identify missing skills and tailored pitches.',
      icon: Target,
      color: 'from-purple-500/20 to-pink-500/20 text-purple-400 border-purple-500/30',
      badge: 'Keyword Alignment',
    },
    {
      id: 'roadmap',
      title: 'Personalized Roadmap',
      description: 'Generate multi-phased learning milestones, checklists, and resources to transition into target roles.',
      icon: Map,
      color: 'from-emerald-500/20 to-green-500/20 text-brand-400 border-brand-500/30',
      badge: 'Phased Milestones',
    },
    {
      id: 'interview',
      title: 'Technical Mock Interview',
      description: 'Interactive real-time interview simulator evaluating your answers with granular AI rubrics.',
      icon: MessageSquareCode,
      color: 'from-amber-500/20 to-orange-500/20 text-amber-400 border-amber-500/30',
      badge: 'Live AI Grading',
    },
    {
      id: 'rag',
      title: 'RAG Knowledge Explorer',
      description: 'Inspect the embedded vector knowledge base with Cosine Similarity search and contextual augmentation.',
      icon: Database,
      color: 'from-indigo-500/20 to-blue-500/20 text-indigo-400 border-indigo-500/30',
      badge: 'Cosine Vectors',
    },
  ];

  return (
    <div className="space-y-8 pb-12">
      {/* Hero Welcome Banner */}
      <div className="relative overflow-hidden rounded-3xl border border-slate-800 bg-gradient-to-r from-slate-900 via-slate-850 to-slate-900 p-8 shadow-xl">
        <div className="absolute right-0 top-0 -mr-16 -mt-16 h-64 w-64 rounded-full bg-brand-500/10 blur-3xl pointer-events-none" />
        
        <div className="relative z-10 flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
          <div className="max-w-2xl space-y-3">
            <div className="inline-flex items-center space-x-2 rounded-full border border-brand-500/30 bg-brand-500/10 px-3 py-1 text-xs font-semibold text-brand-400">
              <Sparkles className="h-3.5 w-3.5" />
              <span>Full-Stack AI Engineering Platform</span>
            </div>
            <h1 className="text-3xl font-extrabold tracking-tight text-white sm:text-4xl">
              Elevate Your Career with <span className="bg-gradient-to-r from-brand-400 to-emerald-300 bg-clip-text text-transparent">AI Intelligence</span>
            </h1>
            <p className="text-sm text-slate-300 leading-relaxed">
              Powered by Spring Boot 3, React TypeScript, Apache PDFBox, Cosine Vector RAG, and JWT Security. Analyze resumes, match target requisitions, generate phased career trajectories, and master technical interviews.
            </p>

            {!isAuthenticated && (
              <div className="flex items-center space-x-3 pt-2">
                <button
                  onClick={demoLogin}
                  className="inline-flex items-center space-x-2 rounded-xl bg-brand-500 px-4 py-2.5 text-xs font-bold text-slate-950 hover:bg-brand-400 transition shadow-lg shadow-brand-500/20"
                >
                  <Zap className="h-4 w-4" />
                  <span>Launch 1-Click Demo</span>
                </button>
                <button
                  onClick={onOpenAuth}
                  className="rounded-xl border border-slate-700 bg-slate-800/80 px-4 py-2.5 text-xs font-semibold text-white hover:bg-slate-700 transition"
                >
                  Sign In / Register
                </button>
              </div>
            )}
          </div>

          {/* Readiness Index Metric Card */}
          <div className="flex items-center space-x-6 rounded-2xl border border-slate-800 bg-slate-950/60 p-5 backdrop-blur-sm">
            <ScoreGauge score={84} size={110} strokeWidth={9} label="Readiness" />
            <div className="space-y-1">
              <div className="flex items-center space-x-1.5 text-xs font-bold text-brand-400">
                <TrendingUp className="h-3.5 w-3.5" />
                <span>Target Alignment</span>
              </div>
              <p className="text-sm font-bold text-white">
                {user ? user.targetRole : 'Full Stack & AI Engineer'}
              </p>
              <p className="text-xs text-slate-400">
                {user ? `${user.yearsOfExperience} Years Experience` : '5 Years Experience'}
              </p>
              <div className="flex items-center space-x-1 text-[11px] text-slate-500 pt-1">
                <CheckCircle2 className="h-3.5 w-3.5 text-brand-500" />
                <span>RAG Grounded</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Launchpad Grid */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-bold text-white">Platform Capabilities</h2>
          <span className="text-xs text-slate-400">Select a tool to begin</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {quickActions.map((action) => {
            const Icon = action.icon;
            return (
              <div
                key={action.id}
                onClick={() => setActiveTab(action.id)}
                className="group cursor-pointer rounded-2xl border border-slate-800 bg-slate-900/60 p-5 transition-all duration-200 hover:-translate-y-1 hover:border-slate-700 hover:bg-slate-850 hover:shadow-xl"
              >
                <div className="flex items-start justify-between">
                  <div className={`rounded-xl border p-2.5 bg-gradient-to-br ${action.color}`}>
                    <Icon className="h-5 w-5" />
                  </div>
                  <span className="rounded-full bg-slate-800 px-2 py-0.5 text-[10px] font-semibold text-slate-300">
                    {action.badge}
                  </span>
                </div>

                <div className="mt-4 space-y-1.5">
                  <div className="flex items-center justify-between">
                    <h3 className="text-sm font-bold text-white group-hover:text-brand-400 transition">
                      {action.title}
                    </h3>
                    <ArrowUpRight className="h-4 w-4 text-slate-500 group-hover:text-white transition" />
                  </div>
                  <p className="text-xs text-slate-400 leading-relaxed">
                    {action.description}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Architecture Highlights */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900/40 p-6">
        <div className="flex items-center space-x-2 text-xs font-bold uppercase tracking-wider text-slate-400 mb-4">
          <Cpu className="h-4 w-4 text-brand-400" />
          <span>Production Stack Architecture</span>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-xs">
          <div className="rounded-xl border border-slate-800/80 bg-slate-950 p-3.5 space-y-1">
            <span className="text-slate-500 font-medium">Backend</span>
            <p className="font-bold text-white">Spring Boot 3.3</p>
            <p className="text-[11px] text-slate-400">Java 21, Security 6, JPA</p>
          </div>
          <div className="rounded-xl border border-slate-800/80 bg-slate-950 p-3.5 space-y-1">
            <span className="text-slate-500 font-medium">Frontend</span>
            <p className="font-bold text-white">React 18 + TS</p>
            <p className="text-[11px] text-slate-400">Vite, Tailwind, Lucide</p>
          </div>
          <div className="rounded-xl border border-slate-800/80 bg-slate-950 p-3.5 space-y-1">
            <span className="text-slate-500 font-medium">AI & RAG</span>
            <p className="font-bold text-white">Cosine Vector Engine</p>
            <p className="text-[11px] text-slate-400">Dual-Mode LLM + Fallback</p>
          </div>
          <div className="rounded-xl border border-slate-800/80 bg-slate-950 p-3.5 space-y-1">
            <span className="text-slate-500 font-medium">DevOps</span>
            <p className="font-bold text-white">Docker Compose</p>
            <p className="text-[11px] text-slate-400">MySQL 8 & Nginx Multi-stage</p>
          </div>
        </div>
      </div>
    </div>
  );
};
