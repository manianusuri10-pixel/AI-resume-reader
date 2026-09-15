import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Sparkles, User as UserIcon, LogOut, ShieldCheck, Zap } from 'lucide-react';

interface NavbarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  onOpenAuth: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ activeTab, setActiveTab, onOpenAuth }) => {
  const { user, isAuthenticated, logout, demoLogin } = useAuth();

  const navItems = [
    { id: 'dashboard', label: 'Dashboard' },
    { id: 'resume', label: 'Resume ATS' },
    { id: 'job-match', label: 'Job Matcher' },
    { id: 'roadmap', label: 'Career Roadmap' },
    { id: 'interview', label: 'Mock Interview' },
    { id: 'rag', label: 'RAG Knowledge' },
  ];

  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-800 bg-slate-950/80 backdrop-blur-md">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <div className="flex items-center space-x-3 cursor-pointer" onClick={() => setActiveTab('dashboard')}>
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-tr from-brand-600 to-emerald-400 text-black shadow-lg shadow-brand-500/20">
            <Sparkles className="h-5 w-5" />
          </div>
          <div>
            <div className="flex items-center space-x-1.5">
              <span className="text-lg font-extrabold tracking-tight text-white">Career Copilot</span>
              <span className="rounded bg-brand-500/10 px-1.5 py-0.5 text-[10px] font-semibold text-brand-400 border border-brand-500/30">AI RAG</span>
            </div>
            <p className="text-[11px] text-slate-400 font-medium">Full-Stack AI Career Platform</p>
          </div>
        </div>

        {/* Navigation Tabs */}
        <nav className="hidden md:flex items-center space-x-1 bg-slate-900/90 p-1 rounded-xl border border-slate-800">
          {navItems.map((item) => {
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                className={`rounded-lg px-3 py-1.5 text-xs font-semibold transition-all ${
                  isActive
                    ? 'bg-brand-500 text-slate-950 shadow-sm'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`}
              >
                {item.label}
              </button>
            );
          })}
        </nav>

        {/* User / Auth section */}
        <div className="flex items-center space-x-3">
          {isAuthenticated && user ? (
            <div className="flex items-center space-x-3">
              <div className="hidden sm:block text-right">
                <p className="text-xs font-semibold text-white">{user.fullName}</p>
                <p className="text-[10px] text-brand-400 font-medium">{user.targetRole || 'Software Engineer'}</p>
              </div>
              <div className="flex h-9 w-9 items-center justify-center rounded-full bg-slate-800 border border-slate-700 text-slate-200">
                <UserIcon className="h-4 w-4" />
              </div>
              <button
                onClick={logout}
                title="Sign out"
                className="rounded-lg p-2 text-slate-400 hover:bg-slate-800 hover:text-red-400 transition"
              >
                <LogOut className="h-4 w-4" />
              </button>
            </div>
          ) : (
            <div className="flex items-center space-x-2">
              <button
                onClick={demoLogin}
                className="hidden sm:inline-flex items-center space-x-1.5 rounded-lg border border-brand-500/30 bg-brand-500/10 px-3 py-1.5 text-xs font-semibold text-brand-400 hover:bg-brand-500/20 transition"
              >
                <Zap className="h-3.5 w-3.5" />
                <span>1-Click Demo</span>
              </button>
              <button
                onClick={onOpenAuth}
                className="inline-flex items-center space-x-1.5 rounded-lg bg-white px-3.5 py-1.5 text-xs font-semibold text-slate-950 hover:bg-slate-200 transition"
              >
                <ShieldCheck className="h-3.5 w-3.5" />
                <span>Sign In</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
