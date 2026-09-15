import React, { useState } from 'react';
import { apiClient } from '../api/client';
import { JobMatch } from '../types';
import { ScoreGauge } from '../components/ScoreGauge';
import {
  Target,
  Building2,
  Briefcase,
  CheckCircle,
  XCircle,
  Copy,
  Check,
  Zap,
  RefreshCw,
  Sparkles,
  AlertCircle
} from 'lucide-react';

const SAMPLE_JD = `Role: Senior Backend / Distributed Systems Engineer
Company: Stripe
Location: San Francisco, CA / Remote

About the Team:
We are building real-time global payment settlement infrastructure handling hundreds of billions of dollars.

Responsibilities:
- Design and operate high-scale, highly reliable backend services in Java / Go / Spring Boot.
- Implement robust distributed locking, caching with Redis, and event streaming pipelines with Apache Kafka.
- Containerize and orchestrate services using Docker and Kubernetes.
- Drive database query optimization and schema design across MySQL and PostgreSQL.
- Partner with security and compliance teams to ensure end-to-end data encryption and PCI standards.

Qualifications:
- 4+ years of professional backend engineering experience.
- Strong proficiency in Java, Spring Boot, Microservices, and RESTful architectures.
- Hands-on experience with Kafka, Redis, Docker, and AWS.
- Familiarity with TypeScript and React is a strong plus.
- Proven ability to optimize high-throughput distributed architectures under low latency constraints.`;

const DEFAULT_RESUME_SNIPPET = `Senior Software Engineer with 5 years experience architecting Spring Boot microservices, MySQL databases, Docker containers, and AWS cloud deployments. Proficient in Java, REST APIs, Redis caching, and CI/CD pipelines.`;

export const JobMatcher: React.FC = () => {
  const [targetRole, setTargetRole] = useState('Senior Backend Engineer');
  const [targetCompany, setTargetCompany] = useState('Stripe');
  const [resumeText, setResumeText] = useState(DEFAULT_RESUME_SNIPPET);
  const [jobDescription, setJobDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [matchResult, setMatchResult] = useState<JobMatch | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  const handleLoadSampleJD = () => {
    setTargetRole('Senior Backend Engineer');
    setTargetCompany('Stripe');
    setJobDescription(SAMPLE_JD);
  };

  const handleMatch = async () => {
    setError(null);
    if (!jobDescription.trim()) {
      setError('Please provide a Job Description to match against.');
      return;
    }

    setLoading(true);
    try {
      const res = await apiClient.analyzeJobMatch({
        targetRole,
        targetCompany,
        resumeText,
        jobDescription,
      });
      setMatchResult(res.data);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to match job description. Please ensure you are logged in.');
    } finally {
      setLoading(false);
    }
  };

  const handleCopyPitch = () => {
    if (matchResult?.tailoredPitch) {
      navigator.clipboard.writeText(matchResult.tailoredPitch);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-5">
        <div>
          <div className="flex items-center space-x-2">
            <h1 className="text-2xl font-extrabold text-white">Job Description Matcher & Gap Analysis</h1>
            <span className="rounded-full bg-purple-500/10 px-2.5 py-0.5 text-xs font-semibold text-purple-400 border border-purple-500/30">
              AI Alignment
            </span>
          </div>
          <p className="mt-1 text-xs text-slate-400">
            Compare candidate competencies directly against job requisitions, uncover critical missing skills, and generate tailored cover pitches.
          </p>
        </div>

        <button
          type="button"
          onClick={handleLoadSampleJD}
          className="inline-flex items-center space-x-1.5 rounded-xl border border-purple-500/40 bg-purple-500/10 px-3.5 py-2 text-xs font-bold text-purple-400 hover:bg-purple-500/20 transition self-start"
        >
          <Zap className="h-3.5 w-3.5" />
          <span>Load Sample Job Description</span>
        </button>
      </div>

      {error && (
        <div className="flex items-center space-x-2 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-xs text-red-400">
          <AlertCircle className="h-4 w-4 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Dual Column Input Form */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left: Candidate Profile */}
        <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 space-y-4">
          <div className="flex items-center space-x-2 text-xs font-bold uppercase tracking-wider text-slate-400">
            <Briefcase className="h-4 w-4 text-brand-400" />
            <span>Candidate Profile / Resume Summary</span>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Candidate Summary & Tech Stack</label>
            <textarea
              rows={8}
              value={resumeText}
              onChange={(e) => setResumeText(e.target.value)}
              placeholder="Paste candidate background, skills, or resume summary..."
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 p-3.5 font-mono text-xs text-slate-200 placeholder-slate-600 focus:border-brand-500 focus:outline-none"
            />
          </div>
        </div>

        {/* Right: Job Description */}
        <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 space-y-4">
          <div className="flex items-center space-x-2 text-xs font-bold uppercase tracking-wider text-slate-400">
            <Target className="h-4 w-4 text-purple-400" />
            <span>Target Job Requisition</span>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Target Role</label>
              <input
                type="text"
                value={targetRole}
                onChange={(e) => setTargetRole(e.target.value)}
                placeholder="e.g. Senior Backend Engineer"
                className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-purple-500 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Company</label>
              <input
                type="text"
                value={targetCompany}
                onChange={(e) => setTargetCompany(e.target.value)}
                placeholder="e.g. Stripe"
                className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-purple-500 focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Job Description Content</label>
            <textarea
              rows={5}
              value={jobDescription}
              onChange={(e) => setJobDescription(e.target.value)}
              placeholder="Paste the target job description here..."
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 p-3.5 font-mono text-xs text-slate-200 placeholder-slate-600 focus:border-purple-500 focus:outline-none"
            />
          </div>
        </div>
      </div>

      <div className="flex justify-end">
        <button
          onClick={handleMatch}
          disabled={loading}
          className="inline-flex items-center space-x-2 rounded-xl bg-purple-500 px-6 py-2.5 text-xs font-bold text-white hover:bg-purple-400 transition disabled:opacity-50 shadow-lg shadow-purple-500/20"
        >
          {loading ? (
            <>
              <RefreshCw className="h-4 w-4 animate-spin" />
              <span>Calculating Match Alignment...</span>
            </>
          ) : (
            <>
              <Sparkles className="h-4 w-4" />
              <span>Calculate Requisition Match</span>
            </>
          )}
        </button>
      </div>

      {/* Match Results */}
      {matchResult && (
        <div className="space-y-6 animate-fadeIn">
          {/* Top Score Banner */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 flex flex-col md:flex-row items-center justify-between gap-6">
            <div className="space-y-2 text-center md:text-left">
              <span className="rounded-full bg-purple-500/10 px-3 py-1 text-xs font-semibold text-purple-400 border border-purple-500/30">
                Alignment for {matchResult.targetRole} at {matchResult.targetCompany}
              </span>
              <h2 className="text-xl font-bold text-white">Requisition Match Summary</h2>
              <p className="text-xs text-slate-400 max-w-xl">
                Analysis combines keyword density, prerequisite competency overlap, and architectural experience depth.
              </p>
            </div>

            <div className="flex items-center space-x-6">
              <ScoreGauge score={matchResult.matchScore} size={110} strokeWidth={9} label="Overall Match" />
            </div>
          </div>

          {/* Skill Gap Breakdown Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {/* Matched Skills */}
            <div className="rounded-2xl border border-emerald-900/30 bg-emerald-950/20 p-6">
              <div className="flex items-center space-x-2 text-emerald-400 font-bold text-sm mb-3">
                <CheckCircle className="h-4 w-4" />
                <span>Matched Required Skills ({matchResult.matchedSkills.length})</span>
              </div>
              <div className="flex flex-wrap gap-2">
                {matchResult.matchedSkills.map((s, i) => (
                  <span
                    key={i}
                    className="rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-300"
                  >
                    ✓ {s}
                  </span>
                ))}
              </div>
            </div>

            {/* Missing Skills */}
            <div className="rounded-2xl border border-amber-900/30 bg-amber-950/20 p-6">
              <div className="flex items-center space-x-2 text-amber-400 font-bold text-sm mb-3">
                <XCircle className="h-4 w-4" />
                <span>Identified Skill Gaps ({matchResult.missingSkills.length})</span>
              </div>
              <div className="flex flex-wrap gap-2">
                {matchResult.missingSkills.map((s, i) => (
                  <span
                    key={i}
                    className="rounded-lg border border-amber-500/30 bg-amber-500/10 px-3 py-1 text-xs font-medium text-amber-300"
                  >
                    ! {s}
                  </span>
                ))}
              </div>
            </div>
          </div>

          {/* Actionable Recommendations */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6">
            <h3 className="text-sm font-bold text-white mb-3">AI Strategic Application Coaching</h3>
            <ul className="space-y-2 text-xs text-slate-300">
              {matchResult.recommendations.map((rec, idx) => (
                <li key={idx} className="flex items-start space-x-2">
                  <span className="text-purple-400 font-bold">•</span>
                  <span>{rec}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Tailored Cover Pitch Snippet */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <Sparkles className="h-4 w-4 text-purple-400" />
                <h3 className="text-sm font-bold text-white">AI-Generated Tailored Elevator Pitch</h3>
              </div>
              <button
                onClick={handleCopyPitch}
                className="inline-flex items-center space-x-1.5 rounded-lg border border-slate-700 bg-slate-800 px-3 py-1 text-xs text-slate-300 hover:text-white transition"
              >
                {copied ? <Check className="h-3.5 w-3.5 text-brand-400" /> : <Copy className="h-3.5 w-3.5" />}
                <span>{copied ? 'Copied!' : 'Copy to Clipboard'}</span>
              </button>
            </div>
            <div className="rounded-xl border border-slate-800 bg-slate-950 p-4 font-mono text-xs text-slate-200 whitespace-pre-wrap leading-relaxed">
              {matchResult.tailoredPitch}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
