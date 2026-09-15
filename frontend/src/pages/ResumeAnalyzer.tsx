import React, { useState } from 'react';
import { apiClient } from '../api/client';
import { ResumeAnalysis } from '../types';
import { ScoreGauge } from '../components/ScoreGauge';
import {
  Upload,
  FileText,
  CheckCircle2,
  AlertTriangle,
  ArrowRight,
  Sparkles,
  Zap,
  RefreshCw,
  Copy,
  Check
} from 'lucide-react';

const SAMPLE_RESUME_TEXT = `ALEX MERCER
Email: alex.mercer@example.com | Phone: (555) 234-5678 | San Francisco, CA
LinkedIn: linkedin.com/in/alex-mercer | GitHub: github.com/alex-mercer

PROFESSIONAL SUMMARY
Results-driven Senior Full-Stack & AI Engineer with 5+ years of experience designing high-throughput microservices, distributed architectures, and AI-enabled web applications. Proven track record of reducing latency by 42% and scaling platforms to 200,000+ active users.

TECHNICAL SKILLS
Languages & Frameworks: Java 21, Spring Boot 3, Spring Security, Hibernate/JPA, React 18, TypeScript, Python
Cloud & DevOps: Docker, Kubernetes, AWS (EC2, S3, RDS, ECS), CI/CD (GitHub Actions), Terraform
Data & Messaging: MySQL, PostgreSQL, Redis, Apache Kafka, Vector Embeddings (RAG)
Architecture & Testing: Microservices, RESTful APIs, System Design, JUnit 5, Testcontainers

PROFESSIONAL EXPERIENCE
Senior Backend Engineer | CloudScale Innovations | 2022 - Present
- Architected and deployed 14 high-throughput Spring Boot microservices handling over 5,000 requests/sec with 99.99% uptime.
- Optimized database queries and introduced Redis distributed caching, slashing p99 API response latency by 42%.
- Spearheaded the integration of a Retrieval-Augmented Generation (RAG) vector search engine, improving customer support ticket resolution by 35%.
- Mentored a team of 6 junior and mid-level engineers, establishing automated CI/CD deployment pipelines with zero-downtime rolling updates.

Software Engineer | Apex FinTech Solutions | 2020 - 2022
- Developed secure RESTful financial transaction APIs adhering to PCI-DSS standards using Spring Boot and MySQL.
- Implemented stateless JWT authentication and role-based access control, mitigating critical security vulnerabilities.
- Automated end-to-end integration test suites with JUnit and Mockito, boosting overall code coverage from 62% to 91%.

EDUCATION
Bachelor of Science in Computer Science | University of California, Berkeley (2016 - 2020)`;

export const ResumeAnalyzer: React.FC = () => {
  const [activeInputTab, setActiveInputTab] = useState<'upload' | 'paste'>('upload');
  const [file, setFile] = useState<File | null>(null);
  const [resumeText, setResumeText] = useState('');
  const [loading, setLoading] = useState(false);
  const [analysis, setAnalysis] = useState<ResumeAnalysis | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [copiedIndex, setCopiedIndex] = useState<number | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleAnalyze = async () => {
    setError(null);
    setLoading(true);

    try {
      let res;
      if (activeInputTab === 'upload') {
        if (!file) {
          setError('Please select a PDF or text file first.');
          setLoading(false);
          return;
        }
        res = await apiClient.uploadResume(file);
      } else {
        if (!resumeText.trim()) {
          setError('Please paste your resume content.');
          setLoading(false);
          return;
        }
        res = await apiClient.analyzeResumeText(resumeText, 'pasted-resume.txt');
      }
      setAnalysis(res.data);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to analyze resume. Please ensure you are signed in.');
    } finally {
      setLoading(false);
    }
  };

  const loadSample = () => {
    setActiveInputTab('paste');
    setResumeText(SAMPLE_RESUME_TEXT);
  };

  const handleCopyBullet = (text: string, index: number) => {
    navigator.clipboard.writeText(text);
    setCopiedIndex(index);
    setTimeout(() => setCopiedIndex(null), 2000);
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-5">
        <div>
          <div className="flex items-center space-x-2">
            <h1 className="text-2xl font-extrabold text-white">AI Resume & ATS Analyzer</h1>
            <span className="rounded-full bg-brand-500/10 px-2.5 py-0.5 text-xs font-semibold text-brand-400 border border-brand-500/30">
              PDFBox & RAG
            </span>
          </div>
          <p className="mt-1 text-xs text-slate-400">
            Evaluate your resume against industry ATS algorithms, extract competencies, and elevate bullet points with quantifiable impact.
          </p>
        </div>

        <button
          type="button"
          onClick={loadSample}
          className="inline-flex items-center space-x-1.5 rounded-xl border border-brand-500/40 bg-brand-500/10 px-3.5 py-2 text-xs font-bold text-brand-400 hover:bg-brand-500/20 transition self-start"
        >
          <Zap className="h-3.5 w-3.5" />
          <span>Load Sample Resume</span>
        </button>
      </div>

      {/* Input Section */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6">
        <div className="flex space-x-2 border-b border-slate-800 pb-3 mb-4">
          <button
            onClick={() => setActiveInputTab('upload')}
            className={`rounded-lg px-3 py-1.5 text-xs font-semibold transition ${
              activeInputTab === 'upload'
                ? 'bg-brand-500 text-slate-950 font-bold'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Upload Document (PDF / TXT)
          </button>
          <button
            onClick={() => setActiveInputTab('paste')}
            className={`rounded-lg px-3 py-1.5 text-xs font-semibold transition ${
              activeInputTab === 'paste'
                ? 'bg-brand-500 text-slate-950 font-bold'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            Paste Raw Resume Text
          </button>
        </div>

        {error && (
          <div className="mb-4 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-xs text-red-400 flex items-center space-x-2">
            <AlertTriangle className="h-4 w-4 flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {activeInputTab === 'upload' ? (
          <div className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-slate-700 bg-slate-950/40 p-8 text-center transition hover:border-brand-500">
            <Upload className="h-10 w-10 text-slate-500 mb-3" />
            <p className="text-sm font-medium text-white mb-1">
              {file ? file.name : 'Select or drag your PDF/Text resume here'}
            </p>
            <p className="text-xs text-slate-500 mb-4">Supports PDF, TXT, and Markdown files up to 15MB</p>
            <label className="cursor-pointer rounded-xl bg-slate-800 px-4 py-2 text-xs font-semibold text-white hover:bg-slate-700 transition">
              <span>Browse Files</span>
              <input
                type="file"
                accept=".pdf,.txt,.md"
                onChange={handleFileChange}
                className="hidden"
              />
            </label>
          </div>
        ) : (
          <div>
            <textarea
              rows={8}
              value={resumeText}
              onChange={(e) => setResumeText(e.target.value)}
              placeholder="Paste your resume markdown or plain text here..."
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 p-4 font-mono text-xs text-slate-200 placeholder-slate-600 focus:border-brand-500 focus:outline-none"
            />
          </div>
        )}

        <div className="mt-4 flex justify-end">
          <button
            onClick={handleAnalyze}
            disabled={loading}
            className="inline-flex items-center space-x-2 rounded-xl bg-brand-500 px-5 py-2.5 text-xs font-bold text-slate-950 hover:bg-brand-400 transition disabled:opacity-50 shadow-lg shadow-brand-500/20"
          >
            {loading ? (
              <>
                <RefreshCw className="h-4 w-4 animate-spin" />
                <span>Extracting & Evaluating...</span>
              </>
            ) : (
              <>
                <Sparkles className="h-4 w-4" />
                <span>Analyze with AI</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Analysis Results Display */}
      {analysis && (
        <div className="space-y-6 animate-fadeIn">
          {/* Score Gauges Row */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 flex items-center justify-between">
              <div>
                <span className="text-xs font-semibold text-slate-400">Overall ATS Score</span>
                <p className="text-lg font-bold text-white mt-1">Applicant Tracking</p>
                <p className="text-[11px] text-slate-500 mt-0.5">Weighted composite rating</p>
              </div>
              <ScoreGauge score={analysis.atsScore} size={95} strokeWidth={8} label="ATS" />
            </div>

            <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 flex items-center justify-between">
              <div>
                <span className="text-xs font-semibold text-slate-400">Impact & Metrics</span>
                <p className="text-lg font-bold text-white mt-1">{analysis.quantifiableMetricsCount} Data Points</p>
                <p className="text-[11px] text-slate-500 mt-0.5">{analysis.actionVerbsCount} Action verbs found</p>
              </div>
              <ScoreGauge score={analysis.impactScore} size={95} strokeWidth={8} label="Impact" />
            </div>

            <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 flex items-center justify-between">
              <div>
                <span className="text-xs font-semibold text-slate-400">Structure & Sections</span>
                <p className="text-lg font-bold text-white mt-1">{analysis.totalWords} Words</p>
                <p className="text-[11px] text-slate-500 mt-0.5">Section header hierarchy</p>
              </div>
              <ScoreGauge score={analysis.formattingScore} size={95} strokeWidth={8} label="Format" />
            </div>
          </div>

          {/* Extracted Skills Tag Cloud */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6">
            <h3 className="text-sm font-bold text-white mb-3">
              Identified Technical Competencies ({analysis.extractedSkills.length})
            </h3>
            <div className="flex flex-wrap gap-2">
              {analysis.extractedSkills.map((skill, idx) => (
                <span
                  key={idx}
                  className="rounded-lg border border-brand-500/20 bg-brand-500/10 px-3 py-1 text-xs font-medium text-brand-300"
                >
                  {skill}
                </span>
              ))}
            </div>
          </div>

          {/* Strengths & Improvements Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div className="rounded-2xl border border-emerald-900/30 bg-emerald-950/20 p-6">
              <div className="flex items-center space-x-2 text-emerald-400 font-bold text-sm mb-3">
                <CheckCircle2 className="h-4 w-4" />
                <span>Resume Strengths</span>
              </div>
              <ul className="space-y-2.5 text-xs text-slate-300">
                {analysis.strengths.map((item, idx) => (
                  <li key={idx} className="flex items-start space-x-2">
                    <span className="text-emerald-400 font-bold">•</span>
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="rounded-2xl border border-amber-900/30 bg-amber-950/20 p-6">
              <div className="flex items-center space-x-2 text-amber-400 font-bold text-sm mb-3">
                <AlertTriangle className="h-4 w-4" />
                <span>High-Impact Recommendations</span>
              </div>
              <ul className="space-y-2.5 text-xs text-slate-300">
                {analysis.criticalImprovements.map((item, idx) => (
                  <li key={idx} className="flex items-start space-x-2">
                    <span className="text-amber-400 font-bold">•</span>
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* Actionable Bullet Point Rewrites */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6">
            <div className="flex items-center space-x-2 text-sm font-bold text-white mb-4">
              <Sparkles className="h-4 w-4 text-brand-400" />
              <span>STAR Formula Bullet Point Transformations</span>
            </div>

            <div className="space-y-4">
              {analysis.bulletRewrites.map((rewrite, idx) => (
                <div key={idx} className="rounded-xl border border-slate-800 bg-slate-950 p-4 space-y-3">
                  <div className="space-y-1">
                    <span className="text-[10px] font-bold uppercase tracking-wider text-red-400">Before (Generic)</span>
                    <p className="text-xs text-slate-400 line-through">{rewrite.original}</p>
                  </div>

                  <div className="space-y-1">
                    <div className="flex items-center justify-between">
                      <span className="text-[10px] font-bold uppercase tracking-wider text-brand-400">After (High-Impact STAR)</span>
                      <button
                        onClick={() => handleCopyBullet(rewrite.improved, idx)}
                        className="inline-flex items-center space-x-1 text-[11px] text-slate-400 hover:text-white"
                      >
                        {copiedIndex === idx ? <Check className="h-3 w-3 text-brand-400" /> : <Copy className="h-3 w-3" />}
                        <span>{copiedIndex === idx ? 'Copied' : 'Copy'}</span>
                      </button>
                    </div>
                    <p className="text-xs font-medium text-white">{rewrite.improved}</p>
                  </div>

                  <div className="rounded-lg bg-slate-900/80 p-2.5 text-[11px] text-slate-400 border border-slate-800">
                    <span className="text-brand-400 font-semibold">Coach Note: </span>
                    {rewrite.rationale}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
