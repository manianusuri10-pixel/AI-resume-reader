import axios from 'axios';
import {
  AuthResponse,
  User,
  ResumeAnalysis,
  JobMatch,
  CareerRoadmap,
  InterviewSession,
  RagChunk,
  RagQueryResponse
} from '../types';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('aicopilot_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // If unauthenticated on protected routes, clear token
      const isAuthEndpoint = error.config?.url?.includes('/auth/');
      if (!isAuthEndpoint) {
        localStorage.removeItem('aicopilot_token');
        localStorage.removeItem('aicopilot_user');
      }
    }
    return Promise.reject(error);
  }
);

export const apiClient = {
  // Auth
  login: (data: { email: string; password: string }) =>
    api.post<AuthResponse>('/auth/login', data),
  
  register: (data: { email: string; password: string; fullName: string; targetRole?: string; yearsOfExperience?: number }) =>
    api.post<AuthResponse>('/auth/register', data),
  
  getMe: () =>
    api.get<User>('/auth/me'),

  // Resume Analysis
  uploadResume: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post<ResumeAnalysis>('/resumes/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  analyzeResumeText: (text: string, filename?: string) =>
    api.post<ResumeAnalysis>('/resumes/analyze-text', { text, filename }),

  getUserResumes: () =>
    api.get<any[]>('/resumes'),

  getResume: (id: number) =>
    api.get<ResumeAnalysis>(`/resumes/${id}`),

  // Job Matcher
  analyzeJobMatch: (data: {
    resumeId?: number;
    resumeText?: string;
    jobDescription: string;
    targetRole?: string;
    targetCompany?: string;
  }) => api.post<JobMatch>('/job-match/analyze', data),

  getJobMatches: () =>
    api.get<JobMatch[]>('/job-match/history'),

  // Career Roadmaps
  generateRoadmap: (data: {
    currentRole?: string;
    targetRole: string;
    timelineMonths?: number;
    currentSkills?: string[];
  }) => api.post<CareerRoadmap>('/roadmaps/generate', data),

  getUserRoadmaps: () =>
    api.get<CareerRoadmap[]>('/roadmaps'),

  // Technical Mock Interviews
  startInterview: (data: {
    targetRole: string;
    difficulty: string;
    questionCount?: number;
  }) => api.post<InterviewSession>('/interviews/start', data),

  submitAnswer: (data: {
    sessionId: number;
    questionId: number;
    answer: string;
  }) => api.post<InterviewSession>('/interviews/submit', data),

  getSession: (id: number) =>
    api.get<InterviewSession>(`/interviews/${id}`),

  getUserSessions: () =>
    api.get<InterviewSession[]>('/interviews'),

  // RAG Knowledge Base
  searchKnowledgeBase: (query: string, topK: number = 4) =>
    api.post<RagChunk[]>('/rag/search', { query, topK }),

  askWithRag: (query: string, topK: number = 4) =>
    api.post<RagQueryResponse>('/rag/ask', { query, topK }),

  ingestDocument: (data: { title: string; category: string; content: string; source?: string }) =>
    api.post<any>('/rag/ingest', data),
};
