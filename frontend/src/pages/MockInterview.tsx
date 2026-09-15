import React, { useState } from 'react';
import { apiClient } from '../api/client';
import { InterviewSession, InterviewQuestion } from '../types';
import { ScoreGauge } from '../components/ScoreGauge';
import {
  MessageSquareCode,
  CheckCircle2,
  AlertCircle,
  HelpCircle,
  Send,
  RefreshCw,
  Sparkles,
  Trophy,
  ChevronRight,
  Code2,
  Zap
} from 'lucide-react';

export const MockInterviewView: React.FC = () => {
  const [targetRole, setTargetRole] = useState('Senior Java & Spring Boot Engineer');
  const [difficulty, setDifficulty] = useState('SENIOR');
  const [questionCount, setQuestionCount] = useState(3);
  const [session, setSession] = useState<InterviewSession | null>(null);
  const [activeQuestionIdx, setActiveQuestionIdx] = useState(0);
  const [answerInput, setAnswerInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [evaluating, setEvaluating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleStartSession = async () => {
    setError(null);
    setLoading(true);
    try {
      const res = await apiClient.startInterview({
        targetRole,
        difficulty,
        questionCount,
      });
      setSession(res.data);
      setActiveQuestionIdx(0);
      setAnswerInput('');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to initialize mock interview. Please sign in to start.');
    } finally {
      setLoading(false);
    }
  };

  const handlePreloadSampleAnswer = () => {
    if (!session || !currentQuestion) return;
    if (currentQuestion.questionText.toLowerCase().includes("transactional") || currentQuestion.questionText.toLowerCase().includes("spring")) {
      setAnswerInput("Spring uses dynamic CGLIB or JDK proxies to intercept methods with @Transactional. When called externally, the proxy intercepts, initiates the transaction with DataSourceTransactionManager, and commits or rolls back on runtime exception. In self-invocation (calling a transactional method from within the same bean), it bypasses the proxy and executes on the raw target object, so no transaction is opened.");
    } else if (currentQuestion.questionText.toLowerCase().includes("n+1") || currentQuestion.questionText.toLowerCase().includes("hibernate")) {
      setAnswerInput("The N+1 problem occurs when Hibernate executes 1 query to fetch a parent list, and then N additional queries for each child relation. We fix this by using JOIN FETCH in JPQL queries, @EntityGraph to fetch relations in a single SQL query, or @BatchSize to fetch child collections in chunks.");
    } else {
      setAnswerInput("We implement distributed caching with Redis using distributed locks (Redisson) to prevent Cache Stampede by allowing only 1 thread to query the underlying database on cache miss. To prevent Cache Penetration, we store null values with short TTL or use a Bloom filter in front of the cache.");
    }
  };

  const handleSubmitAnswer = async () => {
    if (!session || !currentQuestion) return;
    if (!answerInput.trim()) {
      setError('Please provide an answer before submitting for evaluation.');
      return;
    }

    setError(null);
    setEvaluating(true);
    try {
      const res = await apiClient.submitAnswer({
        sessionId: session.id,
        questionId: currentQuestion.id,
        answer: answerInput,
      });
      setSession(res.data);
      setAnswerInput('');
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to submit answer for evaluation.');
    } finally {
      setEvaluating(false);
    }
  };

  const currentQuestion: InterviewQuestion | undefined = session?.questions[activeQuestionIdx];

  return (
    <div className="space-y-8 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-5">
        <div>
          <div className="flex items-center space-x-2">
            <h1 className="text-2xl font-extrabold text-white">Interactive Technical Mock Interview</h1>
            <span className="rounded-full bg-amber-500/10 px-2.5 py-0.5 text-xs font-semibold text-amber-400 border border-amber-500/30">
              Live AI Rubric
            </span>
          </div>
          <p className="mt-1 text-xs text-slate-400">
            Practice real-time technical & architectural interview questions with immediate AI scoring, missing concepts feedback, and staff-level model solutions.
          </p>
        </div>
      </div>

      {error && (
        <div className="flex items-center space-x-2 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-xs text-red-400">
          <AlertCircle className="h-4 w-4 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Setup View (when no active session) */}
      {!session ? (
        <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 space-y-5">
          <h2 className="text-base font-bold text-white">Configure Interview Session</h2>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Target Engineering Role</label>
              <input
                type="text"
                value={targetRole}
                onChange={(e) => setTargetRole(e.target.value)}
                placeholder="e.g. Senior Java & Spring Boot Engineer"
                className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Seniority Level</label>
              <select
                value={difficulty}
                onChange={(e) => setDifficulty(e.target.value)}
                className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-amber-500 focus:outline-none"
              >
                <option value="ENTRY">Entry / Associate (L3)</option>
                <option value="MID">Mid-Level (L4)</option>
                <option value="SENIOR">Senior Engineer (L5)</option>
                <option value="LEAD">Staff / Principal Architect (L6+)</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Questions Count</label>
              <select
                value={questionCount}
                onChange={(e) => setQuestionCount(parseInt(e.target.value) || 3)}
                className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-amber-500 focus:outline-none"
              >
                <option value={1}>1 Question (Quick Check)</option>
                <option value={3}>3 Questions (Standard Mock)</option>
                <option value={5}>5 Questions (Comprehensive Deep Dive)</option>
              </select>
            </div>
          </div>

          <div className="flex justify-end pt-2">
            <button
              onClick={handleStartSession}
              disabled={loading}
              className="inline-flex items-center space-x-2 rounded-xl bg-amber-500 px-6 py-2.5 text-xs font-bold text-slate-950 hover:bg-amber-400 transition disabled:opacity-50 shadow-lg shadow-amber-500/20"
            >
              {loading ? (
                <>
                  <RefreshCw className="h-4 w-4 animate-spin" />
                  <span>Preparing Question Bank...</span>
                </>
              ) : (
                <>
                  <Sparkles className="h-4 w-4" />
                  <span>Begin Mock Interview Room</span>
                </>
              )}
            </button>
          </div>
        </div>
      ) : (
        /* Active Interview Session Room */
        <div className="space-y-6">
          {/* Top Session Progress Bar */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
            <div className="space-y-1">
              <div className="flex items-center space-x-2">
                <span className="rounded-full bg-amber-500/10 px-2.5 py-0.5 text-xs font-bold text-amber-400 border border-amber-500/30">
                  {session.difficulty} • {session.targetRole}
                </span>
                <span className={`text-xs font-semibold px-2 py-0.5 rounded ${session.status === 'COMPLETED' ? 'bg-emerald-500/20 text-emerald-300' : 'bg-blue-500/20 text-blue-300'}`}>
                  {session.status === 'COMPLETED' ? 'Session Completed' : 'In Progress'}
                </span>
              </div>
              <p className="text-xs text-slate-400">
                {session.questions.filter((q) => q.userResponse).length} of {session.questions.length} Questions Answered
              </p>
            </div>

            <div className="flex items-center space-x-4">
              {session.totalScore > 0 && (
                <div className="flex items-center space-x-2 bg-slate-950 px-3 py-1.5 rounded-xl border border-slate-800 text-xs">
                  <Trophy className="h-4 w-4 text-amber-400" />
                  <span className="text-slate-400">Session Score:</span>
                  <span className="font-bold text-white">{session.totalScore} / 10</span>
                </div>
              )}
              <button
                onClick={() => setSession(null)}
                className="rounded-xl border border-slate-700 bg-slate-800 px-3 py-1.5 text-xs text-slate-300 hover:text-white transition"
              >
                New Session
              </button>
            </div>
          </div>

          {/* Question Tabs Selector */}
          <div className="flex space-x-2 overflow-x-auto pb-1">
            {session.questions.map((q, idx) => {
              const isAnswered = !!q.userResponse;
              const isSelected = activeQuestionIdx === idx;
              return (
                <button
                  key={idx}
                  onClick={() => {
                    setActiveQuestionIdx(idx);
                    setAnswerInput(q.userResponse || '');
                  }}
                  className={`flex items-center space-x-2 rounded-xl px-4 py-2 text-xs font-semibold transition whitespace-nowrap ${
                    isSelected
                      ? 'bg-amber-500 text-slate-950 font-bold shadow-md'
                      : isAnswered
                      ? 'bg-emerald-950/40 text-emerald-300 border border-emerald-800/40 hover:bg-emerald-900/40'
                      : 'bg-slate-900 text-slate-400 border border-slate-800 hover:text-white'
                  }`}
                >
                  <span>Question {q.questionNumber}</span>
                  {isAnswered && <CheckCircle2 className="h-3.5 w-3.5 text-emerald-400" />}
                </button>
              );
            })}
          </div>

          {/* Current Active Question Display */}
          {currentQuestion && (
            <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 space-y-6">
              <div className="space-y-2">
                <div className="flex items-center space-x-2 text-xs font-bold uppercase tracking-wider text-amber-400">
                  <Code2 className="h-4 w-4" />
                  <span>Category: {currentQuestion.category}</span>
                </div>
                <h3 className="text-base font-bold text-white leading-relaxed">
                  {currentQuestion.questionText}
                </h3>
              </div>

              {/* Answer Box or Evaluated Answer Display */}
              {!currentQuestion.userResponse ? (
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <label className="block text-xs font-medium text-slate-300">
                      Your Technical Answer (Explain mechanisms, architecture, edge cases)
                    </label>
                    <button
                      type="button"
                      onClick={handlePreloadSampleAnswer}
                      className="inline-flex items-center space-x-1 text-[11px] text-amber-400 hover:text-amber-300"
                    >
                      <Zap className="h-3 w-3" />
                      <span>Pre-fill Sample Answer</span>
                    </button>
                  </div>

                  <textarea
                    rows={6}
                    value={answerInput}
                    onChange={(e) => setAnswerInput(e.target.value)}
                    placeholder="Provide your structured architectural answer here..."
                    className="w-full rounded-xl border border-slate-700 bg-slate-950/60 p-4 font-mono text-xs text-slate-200 placeholder-slate-600 focus:border-amber-500 focus:outline-none leading-relaxed"
                  />

                  <div className="flex justify-end">
                    <button
                      onClick={handleSubmitAnswer}
                      disabled={evaluating}
                      className="inline-flex items-center space-x-2 rounded-xl bg-amber-500 px-5 py-2 text-xs font-bold text-slate-950 hover:bg-amber-400 transition disabled:opacity-50 shadow-lg shadow-amber-500/20"
                    >
                      {evaluating ? (
                        <>
                          <RefreshCw className="h-4 w-4 animate-spin" />
                          <span>Evaluating Response with AI Rubric...</span>
                        </>
                      ) : (
                        <>
                          <Send className="h-3.5 w-3.5" />
                          <span>Submit Answer for Evaluation</span>
                        </>
                      )}
                    </button>
                  </div>
                </div>
              ) : (
                /* Evaluated Feedback Card */
                <div className="space-y-5 animate-fadeIn">
                  <div className="rounded-xl border border-slate-800 bg-slate-950 p-4 space-y-2">
                    <span className="text-[10px] font-bold uppercase tracking-wider text-slate-500">Your Submitted Response</span>
                    <p className="text-xs text-slate-200 whitespace-pre-wrap">{currentQuestion.userResponse}</p>
                  </div>

                  {/* AI Evaluation Card */}
                  <div className="rounded-xl border border-slate-800 bg-slate-900/80 p-5 space-y-4">
                    <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                      <div className="flex items-center space-x-2">
                        <Sparkles className="h-4 w-4 text-amber-400" />
                        <span className="text-sm font-bold text-white">AI Rubric Evaluation</span>
                      </div>
                      <div className="flex items-center space-x-1.5 bg-amber-500/10 border border-amber-500/30 px-3 py-1 rounded-xl text-amber-400 font-bold text-xs">
                        <span>Score:</span>
                        <span className="text-sm text-white">{currentQuestion.aiScore}</span>
                        <span>/ 10</span>
                      </div>
                    </div>

                    <div className="space-y-1">
                      <span className="text-[11px] font-bold text-slate-400">Critique:</span>
                      <p className="text-xs text-slate-200">{currentQuestion.aiCritique}</p>
                    </div>

                    {currentQuestion.suggestedImprovement && (
                      <div className="rounded-lg bg-amber-950/20 border border-amber-800/30 p-3 text-xs text-amber-300">
                        <span className="font-bold">Suggested Enhancement: </span>
                        {currentQuestion.suggestedImprovement}
                      </div>
                    )}

                    {currentQuestion.sampleIdealAnswer && (
                      <div className="space-y-1.5 pt-2">
                        <span className="text-[11px] font-bold uppercase tracking-wider text-brand-400">
                          Staff-Level Reference Answer
                        </span>
                        <div className="rounded-xl border border-emerald-900/40 bg-emerald-950/20 p-4 text-xs text-emerald-200 leading-relaxed">
                          {currentQuestion.sampleIdealAnswer}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Session Completed Summary Banner */}
          {session.status === 'COMPLETED' && (
            <div className="rounded-2xl border border-emerald-800/40 bg-gradient-to-r from-emerald-950/40 via-slate-900 to-emerald-950/40 p-6 flex flex-col md:flex-row items-center justify-between gap-6">
              <div className="space-y-1 text-center md:text-left">
                <div className="inline-flex items-center space-x-1.5 text-emerald-400 font-bold text-xs">
                  <Trophy className="h-4 w-4" />
                  <span>Interview Session Completed</span>
                </div>
                <h3 className="text-lg font-bold text-white">Overall Readiness Review</h3>
                <p className="text-xs text-slate-300 max-w-xl">{session.feedbackSummary}</p>
              </div>

              <div className="flex items-center space-x-4">
                <ScoreGauge score={session.totalScore * 10} size={100} strokeWidth={8} label="Average" />
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
