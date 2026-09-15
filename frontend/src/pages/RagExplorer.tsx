import React, { useState } from 'react';
import { apiClient } from '../api/client';
import { RagChunk, RagQueryResponse } from '../types';
import {
  Database,
  Search,
  BookOpen,
  Sparkles,
  PlusCircle,
  RefreshCw,
  Cpu,
  Layers,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';

export const RagExplorerView: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'search' | 'ask' | 'ingest'>('ask');
  const [query, setQuery] = useState('How does Project Loom Virtual Threads differ from Reactive WebFlux?');
  const [searchResults, setSearchResults] = useState<RagChunk[]>([]);
  const [askResult, setAskResult] = useState<RagQueryResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Ingest form
  const [newTitle, setNewTitle] = useState('');
  const [newCategory, setNewCategory] = useState('SYSTEM_DESIGN');
  const [newContent, setNewContent] = useState('');
  const [ingestSuccess, setIngestSuccess] = useState(false);

  const handleSearch = async () => {
    if (!query.trim()) return;
    setError(null);
    setLoading(true);
    try {
      const res = await apiClient.searchKnowledgeBase(query, 5);
      setSearchResults(res.data);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to search vector store.');
    } finally {
      setLoading(false);
    }
  };

  const handleAskWithRag = async () => {
    if (!query.trim()) return;
    setError(null);
    setLoading(true);
    try {
      const res = await apiClient.askWithRag(query, 3);
      setAskResult(res.data);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to query RAG engine.');
    } finally {
      setLoading(false);
    }
  };

  const handleIngest = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newContent.trim()) return;
    setError(null);
    setLoading(true);
    try {
      await apiClient.ingestDocument({
        title: newTitle || 'Custom Note',
        category: newCategory,
        content: newContent,
      });
      setIngestSuccess(true);
      setNewTitle('');
      setNewContent('');
      setTimeout(() => setIngestSuccess(false), 3000);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to index custom document.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8 pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-5">
        <div>
          <div className="flex items-center space-x-2">
            <h1 className="text-2xl font-extrabold text-white">RAG Vector Knowledge Base Explorer</h1>
            <span className="rounded-full bg-indigo-500/10 px-2.5 py-0.5 text-xs font-semibold text-indigo-400 border border-indigo-500/30">
              Cosine Similarity
            </span>
          </div>
          <p className="mt-1 text-xs text-slate-400">
            Explore how Retrieval-Augmented Generation indexes industry competency rubrics, calculates semantic vector similarities, and enriches AI responses.
          </p>
        </div>
      </div>

      {error && (
        <div className="flex items-center space-x-2 rounded-xl border border-red-500/30 bg-red-500/10 p-3 text-xs text-red-400">
          <AlertCircle className="h-4 w-4 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Tabs */}
      <div className="flex space-x-2 border-b border-slate-800 pb-3">
        <button
          onClick={() => setActiveTab('ask')}
          className={`rounded-lg px-4 py-2 text-xs font-semibold transition ${
            activeTab === 'ask'
              ? 'bg-indigo-500 text-white font-bold shadow'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Ask Copilot (RAG Injected)
        </button>
        <button
          onClick={() => setActiveTab('search')}
          className={`rounded-lg px-4 py-2 text-xs font-semibold transition ${
            activeTab === 'search'
              ? 'bg-indigo-500 text-white font-bold shadow'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Vector Similarity Search
        </button>
        <button
          onClick={() => setActiveTab('ingest')}
          className={`rounded-lg px-4 py-2 text-xs font-semibold transition ${
            activeTab === 'ingest'
              ? 'bg-indigo-500 text-white font-bold shadow'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          Ingest New Document
        </button>
      </div>

      {/* Tab: Ask with RAG */}
      {activeTab === 'ask' && (
        <div className="space-y-6">
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 space-y-4">
            <label className="block text-xs font-bold text-slate-300">
              Ask Any Career, Architectural, or Interview Question
            </label>
            <div className="flex gap-3">
              <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Ask about microservices, Spring Boot, resume ATS tactics..."
                className="flex-1 rounded-xl border border-slate-700 bg-slate-950/60 py-2.5 px-4 text-xs text-white focus:border-indigo-500 focus:outline-none"
              />
              <button
                onClick={handleAskWithRag}
                disabled={loading}
                className="inline-flex items-center space-x-2 rounded-xl bg-indigo-500 px-5 py-2.5 text-xs font-bold text-white hover:bg-indigo-400 transition disabled:opacity-50"
              >
                {loading ? <RefreshCw className="h-4 w-4 animate-spin" /> : <Sparkles className="h-4 w-4" />}
                <span>Ask RAG</span>
              </button>
            </div>
          </div>

          {askResult && (
            <div className="space-y-6 animate-fadeIn">
              {/* Synthesized Answer */}
              <div className="rounded-2xl border border-slate-800 bg-slate-900/80 p-6 space-y-3">
                <div className="flex items-center space-x-2 text-indigo-400 text-xs font-bold uppercase tracking-wider">
                  <Cpu className="h-4 w-4" />
                  <span>RAG Synthesized Response</span>
                </div>
                <div className="rounded-xl border border-slate-800 bg-slate-950 p-4 text-xs text-slate-200 whitespace-pre-wrap leading-relaxed font-mono">
                  {askResult.synthesizedAnswer}
                </div>
              </div>

              {/* Retrieved Context Chunks */}
              <div className="rounded-2xl border border-slate-800 bg-slate-900/40 p-6 space-y-4">
                <div className="flex items-center space-x-2 text-slate-300 text-xs font-bold uppercase tracking-wider">
                  <Layers className="h-4 w-4 text-indigo-400" />
                  <span>Retrieved Knowledge Chunks Grounding This Answer ({askResult.retrievedChunks.length})</span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {askResult.retrievedChunks.map((chunk) => (
                    <div key={chunk.id} className="rounded-xl border border-slate-800 bg-slate-950 p-4 space-y-2">
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-bold text-white truncate max-w-[200px]">{chunk.title}</span>
                        <span className="rounded bg-indigo-500/10 px-2 py-0.5 text-[10px] font-bold text-indigo-300 border border-indigo-500/20">
                          Score: {chunk.similarityScore}
                        </span>
                      </div>
                      <span className="text-[10px] text-slate-500 block uppercase font-medium">{chunk.category}</span>
                      <p className="text-xs text-slate-400 line-clamp-4 leading-relaxed">{chunk.content}</p>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Tab: Vector Search */}
      {activeTab === 'search' && (
        <div className="space-y-6">
          <div className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 space-y-4">
            <label className="block text-xs font-bold text-slate-300">Semantic Vector Cosine Search</label>
            <div className="flex gap-3">
              <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Search concepts e.g. 'Kafka streaming', 'STAR formula', 'Virtual threads'..."
                className="flex-1 rounded-xl border border-slate-700 bg-slate-950/60 py-2.5 px-4 text-xs text-white focus:border-indigo-500 focus:outline-none"
              />
              <button
                onClick={handleSearch}
                disabled={loading}
                className="inline-flex items-center space-x-2 rounded-xl bg-indigo-500 px-5 py-2.5 text-xs font-bold text-white hover:bg-indigo-400 transition disabled:opacity-50"
              >
                {loading ? <RefreshCw className="h-4 w-4 animate-spin" /> : <Search className="h-4 w-4" />}
                <span>Vector Search</span>
              </button>
            </div>
          </div>

          <div className="space-y-4">
            {searchResults.map((chunk) => (
              <div key={chunk.id} className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5 space-y-2">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <BookOpen className="h-4 w-4 text-indigo-400" />
                    <h3 className="text-sm font-bold text-white">{chunk.title}</h3>
                  </div>
                  <div className="flex items-center space-x-2">
                    <span className="rounded bg-slate-800 px-2 py-0.5 text-[10px] text-slate-300 font-medium">
                      {chunk.category}
                    </span>
                    <span className="rounded bg-brand-500/10 px-2.5 py-0.5 text-xs font-bold text-brand-400 border border-brand-500/20">
                      Cosine Sim: {chunk.similarityScore}
                    </span>
                  </div>
                </div>
                <p className="text-xs text-slate-300 leading-relaxed pt-1">{chunk.content}</p>
                <span className="text-[10px] text-slate-500 block pt-1">Source: {chunk.source}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Tab: Ingest */}
      {activeTab === 'ingest' && (
        <form onSubmit={handleIngest} className="rounded-2xl border border-slate-800 bg-slate-900/60 p-6 space-y-4 max-w-2xl">
          <h2 className="text-sm font-bold text-white">Embed & Index New Knowledge Chunk</h2>

          {ingestSuccess && (
            <div className="flex items-center space-x-2 rounded-xl bg-emerald-500/10 border border-emerald-500/30 p-3 text-xs text-emerald-400">
              <CheckCircle2 className="h-4 w-4 flex-shrink-0" />
              <span>Document successfully embedded into vector store!</span>
            </div>
          )}

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Document Title</label>
            <input
              type="text"
              required
              value={newTitle}
              onChange={(e) => setNewTitle(e.target.value)}
              placeholder="e.g. Distributed Caching Best Practices with Redis"
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-indigo-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Category</label>
            <select
              value={newCategory}
              onChange={(e) => setNewCategory(e.target.value)}
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 py-2 px-3 text-xs text-white focus:border-indigo-500 focus:outline-none"
            >
              <option value="SYSTEM_DESIGN">System Design & Distributed Systems</option>
              <option value="TECHNICAL_COMPETENCIES">Technical Competencies</option>
              <option value="INTERVIEW_RUBRIC">Technical Interview Rubric</option>
              <option value="RESUME_ATS_STANDARDS">Resume & ATS Standards</option>
              <option value="BEHAVIORAL_RUBRIC">Behavioral & Leadership</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Content / Body</label>
            <textarea
              rows={5}
              required
              value={newContent}
              onChange={(e) => setNewContent(e.target.value)}
              placeholder="Write or paste the reference notes to index..."
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 p-3.5 text-xs text-white focus:border-indigo-500 focus:outline-none leading-relaxed"
            />
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="inline-flex items-center space-x-2 rounded-xl bg-indigo-500 px-5 py-2 text-xs font-bold text-white hover:bg-indigo-400 transition disabled:opacity-50"
            >
              <PlusCircle className="h-4 w-4" />
              <span>Index Document (Calculate Embedding)</span>
            </button>
          </div>
        </form>
      )}
    </div>
  );
};
