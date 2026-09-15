import React, { useState } from 'react';
import { apiClient } from '../api/client';
import { CareerRoadmap, Milestone } from '../types';
import {
  Map,
  Compass,
  CheckSquare,
  Square,
  Clock,
  BookOpen,
  Calendar,
  Sparkles,
  RefreshCw,
  AlertCircle
} from 'lucide-react';

export const CareerRoadmapView: React.FC = () => {
  const [currentRole, setCurrentRole] = useState('Software Engineer');
  const [targetRole, setTargetRole] = useState('Senior Full-Stack & AI Architect');
  const [timelineMonths, setTimelineMonths] = useState(6);
  const [loading, setLoading] = useState(false);
  const [roadmap, setRoadmap] = useState<CareerRoadmap | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [checkedItems, setCheckedItems] = useState<Record<string, boolean>>({});

  const handleGenerate = async () => {
    setError(null);
    setLoading(true);

    try {
      const res = await apiClient.generateRoadmap({
        currentRole,
        targetRole,
        timelineMonths,
      });
      setRoadmap(res.data);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to generate roadmap. Please sign in to save your career plan.');
    } finally {
      setLoading(false);
    }
  };

  const toggleCheck = (key: string) => {
    setCheckedItems((prev) => ({
      ...prev,
      [key]: !prev[key],
    }));
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-5">
        <div>
          <div className="flex items-center space-x-2">
            <h1 className="text-2xl font-extrabold text-white">Personalized Career Roadmap</h1>
            <span className="rounded-full bg-brand-500/10 px-2.5 py-0.5 text-xs font-semibold text-brand-400 border border-brand-500/30">
              Phased Trajectory
            </span>
          </div>
          <p className="mt-1 text-xs text-slate-400">
            AI-synthesized milestone checkpoints, learning checklists, and engineering reading lists tailored to your target position.
          </p>
        </div>
      </div>

      {error && (
        <div className="flex items-center space-x-2 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-xs text-red-400">
          <AlertCircle className="h-4 w-4 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Configuration Box */}
      <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Current Role</label>
            <input
              type="text"
              value={currentRole}
              onChange={(e) => setCurrentRole(e.target.value)}
              placeholder="e.g. Mid-Level Developer"
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-brand-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Target Desired Role</label>
            <input
              type="text"
              value={targetRole}
              onChange={(e) => setTargetRole(e.target.value)}
              placeholder="e.g. Senior Java Architect / AI Engineer"
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-brand-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Timeline Target</label>
            <select
              value={timelineMonths}
              onChange={(e) => setTimelineMonths(parseInt(e.target.value) || 6)}
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-brand-500 focus:outline-none"
            >
              <option value={3}>Accelerated (3 Months)</option>
              <option value={6}>Standard (6 Months)</option>
              <option value={12}>Comprehensive (12 Months)</option>
            </select>
          </div>
        </div>

        <div className="flex justify-end pt-2">
          <button
            onClick={handleGenerate}
            disabled={loading}
            className="inline-flex items-center space-x-2 rounded-xl bg-brand-500 px-6 py-2.5 text-xs font-bold text-slate-950 hover:bg-brand-400 transition disabled:opacity-50 shadow-lg shadow-brand-500/20"
          >
            {loading ? (
              <>
                <RefreshCw className="h-4 w-4 animate-spin" />
                <span>Synthesizing Progression Path...</span>
              </>
            ) : (
              <>
                <Sparkles className="h-4 w-4" />
                <span>Generate Phased Roadmap</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Generated Roadmap Display */}
      {roadmap && (
        <div className="space-y-6 animate-fadeIn">
          {/* Top Overview Card */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
            <div className="space-y-2">
              <div className="inline-flex items-center space-x-2 rounded-full border border-brand-500/30 bg-brand-500/10 px-3 py-1 text-xs font-bold text-brand-400">
                <Compass className="h-3.5 w-3.5" />
                <span>{roadmap.currentRole} ➔ {roadmap.targetRole}</span>
              </div>
              <h2 className="text-xl font-bold text-white">Curated Engineering Growth Track</h2>
              <p className="text-xs text-slate-400">
                Structured into {roadmap.milestones.length} sequential phases with measurable checkpoints.
              </p>
            </div>

            <div className="flex items-center space-x-6 text-xs text-slate-300">
              <div className="rounded-xl border border-slate-800 bg-slate-950 p-3 flex items-center space-x-2">
                <Calendar className="h-4 w-4 text-brand-400" />
                <div>
                  <span className="text-slate-500 text-[10px] block">Duration</span>
                  <span className="font-bold text-white">{roadmap.timelineMonths} Months</span>
                </div>
              </div>
              <div className="rounded-xl border border-slate-800 bg-slate-950 p-3 flex items-center space-x-2">
                <Clock className="h-4 w-4 text-brand-400" />
                <div>
                  <span className="text-slate-500 text-[10px] block">Commitment</span>
                  <span className="font-bold text-white">{roadmap.estimatedWeeklyHours} hrs / week</span>
                </div>
              </div>
            </div>
          </div>

          {/* High-Leverage Skills Tag Cloud */}
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6">
            <h3 className="text-sm font-bold text-white mb-3">Key Skills to Master</h3>
            <div className="flex flex-wrap gap-2">
              {roadmap.skillsToAcquire.map((skill, idx) => (
                <span
                  key={idx}
                  className="rounded-lg border border-brand-500/30 bg-brand-500/10 px-3 py-1 text-xs font-medium text-brand-300"
                >
                  ⚡ {skill}
                </span>
              ))}
            </div>
          </div>

          {/* Phased Milestones Timeline */}
          <div className="space-y-6">
            {roadmap.milestones.map((milestone: Milestone, mIdx: number) => (
              <div
                key={mIdx}
                className="relative rounded-2xl border border-slate-800 bg-slate-900/60 p-6 transition hover:border-slate-700"
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-3 mb-4">
                  <div className="flex items-center space-x-3">
                    <span className="flex h-8 w-8 items-center justify-center rounded-xl bg-brand-500 font-extrabold text-slate-950 text-sm">
                      {milestone.phaseNumber}
                    </span>
                    <div>
                      <h3 className="text-base font-bold text-white">{milestone.title}</h3>
                      <p className="text-xs text-slate-400">{milestone.description}</p>
                    </div>
                  </div>
                  <span className="rounded-full bg-slate-800 px-3 py-1 text-[11px] font-semibold text-brand-400 self-start">
                    {milestone.durationWeeks} Weeks
                  </span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 text-xs">
                  {/* Key Topics */}
                  <div className="space-y-2">
                    <span className="font-bold text-slate-300 uppercase tracking-wider text-[11px]">Core Architecture Topics</span>
                    <ul className="space-y-1.5 text-slate-400">
                      {milestone.keyTopics.map((topic, tIdx) => (
                        <li key={tIdx} className="flex items-center space-x-2">
                          <span className="text-brand-400">•</span>
                          <span>{topic}</span>
                        </li>
                      ))}
                    </ul>
                  </div>

                  {/* Action Checklist */}
                  <div className="space-y-2">
                    <span className="font-bold text-slate-300 uppercase tracking-wider text-[11px]">Milestone Checklist</span>
                    <div className="space-y-2">
                      {milestone.actionItems.map((item, aIdx) => {
                        const checkKey = `m-${mIdx}-a-${aIdx}`;
                        const isChecked = !!checkedItems[checkKey];
                        return (
                          <div
                            key={aIdx}
                            onClick={() => toggleCheck(checkKey)}
                            className="flex items-start space-x-2 cursor-pointer group"
                          >
                            {isChecked ? (
                              <CheckSquare className="h-4 w-4 text-brand-400 flex-shrink-0 mt-0.5" />
                            ) : (
                              <Square className="h-4 w-4 text-slate-600 group-hover:text-slate-400 flex-shrink-0 mt-0.5" />
                            )}
                            <span className={`${isChecked ? 'line-through text-slate-500' : 'text-slate-300'}`}>
                              {item}
                            </span>
                          </div>
                        );
                      })}
                    </div>
                  </div>

                  {/* Curated Resources */}
                  <div className="space-y-2">
                    <span className="font-bold text-slate-300 uppercase tracking-wider text-[11px] flex items-center space-x-1.5">
                      <BookOpen className="h-3.5 w-3.5 text-brand-400" />
                      <span>Recommended Resources</span>
                    </span>
                    <ul className="space-y-1.5 text-slate-400">
                      {milestone.recommendedResources.map((res, rIdx) => (
                        <li key={rIdx} className="rounded bg-slate-950/80 p-2 border border-slate-800/80 text-[11px] text-slate-300">
                          📖 {res}
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
