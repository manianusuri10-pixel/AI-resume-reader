export interface User {
  id: number;
  email: string;
  fullName: string;
  targetRole: string;
  yearsOfExperience: number;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  id: number;
  email: string;
  fullName: string;
  targetRole: string;
  yearsOfExperience: number;
}

export interface BulletRewrite {
  original: string;
  improved: string;
  rationale: string;
}

export interface ResumeAnalysis {
  resumeId: number;
  fileName: string;
  atsScore: number;
  formattingScore: number;
  impactScore: number;
  totalWords: number;
  actionVerbsCount: number;
  quantifiableMetricsCount: number;
  extractedSkills: string[];
  sections: Record<string, string>;
  strengths: string[];
  criticalImprovements: string[];
  bulletRewrites: BulletRewrite[];
  rawText: string;
  createdAt: string;
}

export interface JobMatch {
  matchId: number;
  targetRole: string;
  targetCompany: string;
  matchScore: number;
  skillMatchPercentage: number;
  matchedSkills: string[];
  missingSkills: string[];
  recommendations: string[];
  tailoredPitch: string;
  createdAt: string;
}

export interface Milestone {
  phaseNumber: number;
  title: string;
  durationWeeks: number;
  description: string;
  keyTopics: string[];
  actionItems: string[];
  recommendedResources: string[];
}

export interface CareerRoadmap {
  roadmapId: number;
  currentRole: string;
  targetRole: string;
  timelineMonths: number;
  estimatedWeeklyHours: number;
  skillsToAcquire: string[];
  milestones: Milestone[];
  createdAt: string;
}

export interface InterviewQuestion {
  id: number;
  questionNumber: number;
  questionText: string;
  category: string;
  sampleIdealAnswer?: string;
  userResponse?: string;
  aiScore?: number;
  aiCritique?: string;
  suggestedImprovement?: string;
  evaluatedAt?: string;
}

export interface InterviewSession {
  id: number;
  targetRole: string;
  difficulty: string;
  status: 'IN_PROGRESS' | 'COMPLETED';
  totalScore: number;
  feedbackSummary?: string;
  questions: InterviewQuestion[];
  createdAt: string;
}

export interface RagChunk {
  id: number;
  title: string;
  category: string;
  content: string;
  source: string;
  similarityScore: number;
}

export interface RagQueryResponse {
  query: string;
  synthesizedAnswer: string;
  retrievedChunks: RagChunk[];
}
