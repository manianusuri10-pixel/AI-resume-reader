import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { AuthModal } from './pages/AuthModal';
import { Dashboard } from './pages/Dashboard';
import { ResumeAnalyzer } from './pages/ResumeAnalyzer';
import { JobMatcher } from './pages/JobMatcher';
import { CareerRoadmapView } from './pages/CareerRoadmap';
import { MockInterviewView } from './pages/MockInterview';
import { RagExplorerView } from './pages/RagExplorer';
import { Sparkles, Terminal, Shield, Layers } from 'lucide-react';

const MainLayout: React.FC = () => {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [isAuthOpen, setIsAuthOpen] = useState(false);
  const { sessionMessage, dismissSessionMessage } = useAuth();

  return (
    <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100 selection:bg-brand-500 selection:text-black">
      {/* Background ambient lighting */}
      <div className="fixed inset-0 pointer-events-none overflow-hidden z-0">
        <div className="absolute -top-40 left-1/2 -translate-x-1/2 w-[700px] h-[350px] bg-brand-500/5 blur-[120px] rounded-full" />
        <div className="absolute top-1/3 -right-40 w-[450px] h-[350px] bg-purple-500/5 blur-[120px] rounded-full" />
      </div>

      <Navbar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        onOpenAuth={() => setIsAuthOpen(true)}
      />

      {sessionMessage && (
        <div className="relative z-10 mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8 pt-4">
          <div className="flex items-center justify-between rounded-xl border border-amber-500/40 bg-amber-500/10 px-4 py-3 text-sm text-amber-200">
            <span>{sessionMessage}</span>
            <button
              type="button"
              onClick={dismissSessionMessage}
              className="ml-4 rounded-md border border-amber-300/40 px-2 py-1 text-xs font-medium text-amber-100 hover:bg-amber-400/10"
            >
              Dismiss
            </button>
          </div>
        </div>
      )}

      <main className="relative z-10 flex-1 mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8 pt-8">
        {activeTab === 'dashboard' && (
          <Dashboard
            setActiveTab={setActiveTab}
            onOpenAuth={() => setIsAuthOpen(true)}
          />
        )}
        {activeTab === 'resume' && <ResumeAnalyzer />}
        {activeTab === 'job-match' && <JobMatcher />}
        {activeTab === 'roadmap' && <CareerRoadmapView />}
        {activeTab === 'interview' && <MockInterviewView />}
        {activeTab === 'rag' && <RagExplorerView />}
      </main>

      {/* Footer */}
      <footer className="relative z-10 border-t border-slate-900 bg-slate-950/80 py-6 mt-auto">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <div className="flex items-center space-x-2">
            <Sparkles className="h-4 w-4 text-brand-500" />
            <span className="font-semibold text-slate-400">AI Career Copilot</span>
            <span>—</span>
            <span>Production Java Spring Boot 3 & React Platform</span>
          </div>

          <div className="flex items-center space-x-5 text-slate-400">
            <span className="flex items-center space-x-1">
              <Shield className="h-3.5 w-3.5 text-brand-400" />
              <span>JWT Stateless Auth</span>
            </span>
            <span className="flex items-center space-x-1">
              <Layers className="h-3.5 w-3.5 text-purple-400" />
              <span>Cosine Vector RAG</span>
            </span>
            <span className="flex items-center space-x-1">
              <Terminal className="h-3.5 w-3.5 text-cyan-400" />
              <span>Docker & MySQL</span>
            </span>
          </div>
        </div>
      </footer>

      <AuthModal isOpen={isAuthOpen} onClose={() => setIsAuthOpen(false)} />
    </div>
  );
};

export function App() {
  return (
    <AuthProvider>
      <MainLayout />
    </AuthProvider>
  );
}

export default App;
