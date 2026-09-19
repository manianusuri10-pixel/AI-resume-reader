import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthResponse } from '../types';
import { apiClient, SESSION_EXPIRED_MESSAGE } from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  sessionMessage: string | null;
  login: (email: string, password: string) => Promise<void>;
  demoLogin: () => Promise<void>;
  register: (email: string, password: string, fullName: string, targetRole?: string, yearsOfExperience?: number) => Promise<void>;
  logout: () => void;
  dismissSessionMessage: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const readStoredUser = (): User | null => {
  const saved = localStorage.getItem('aicopilot_user');
  if (!saved) return null;

  try {
    return JSON.parse(saved) as User;
  } catch {
    localStorage.removeItem('aicopilot_user');
    return null;
  }
};

const readStoredSessionMessage = (): string | null => {
  return localStorage.getItem('aicopilot_session_message');
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(readStoredUser);
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('aicopilot_token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [sessionMessage, setSessionMessage] = useState<string | null>(readStoredSessionMessage);

  useEffect(() => {
    if (token) {
      apiClient.getMe()
        .then((res) => {
          setUser(res.data);
          localStorage.setItem('aicopilot_user', JSON.stringify(res.data));
        })
        .catch(() => {
          const expiredMessage = SESSION_EXPIRED_MESSAGE;
          localStorage.setItem('aicopilot_session_message', expiredMessage);
          setSessionMessage(expiredMessage);
          logout();
        })
        .finally(() => setIsLoading(false));
    } else {
      setIsLoading(false);
    }
  }, [token]);

  useEffect(() => {
    const handleSessionExpired = (event: Event) => {
      const customEvent = event as CustomEvent<{ message?: string }>;
      const message = customEvent.detail?.message ?? SESSION_EXPIRED_MESSAGE;
      setSessionMessage(message);
      localStorage.setItem('aicopilot_session_message', message);
      logout();
    };

    window.addEventListener('aicopilot:session-expired', handleSessionExpired);
    return () => {
      window.removeEventListener('aicopilot:session-expired', handleSessionExpired);
    };
  }, []);

  const handleAuthSuccess = (data: AuthResponse) => {
    localStorage.removeItem('aicopilot_session_message');
    setSessionMessage(null);
    setToken(data.token);
    const u: User = {
      id: data.id,
      email: data.email,
      fullName: data.fullName,
      targetRole: data.targetRole,
      yearsOfExperience: data.yearsOfExperience,
    };
    setUser(u);
    localStorage.setItem('aicopilot_token', data.token);
    localStorage.setItem('aicopilot_user', JSON.stringify(u));
  };

  const login = async (email: string, password: string) => {
    const res = await apiClient.login({ email, password });
    handleAuthSuccess(res.data);
  };

  const demoLogin = async () => {
    await login('demo@aicopilot.com', 'password123');
  };

  const register = async (
    email: string,
    password: string,
    fullName: string,
    targetRole?: string,
    yearsOfExperience?: number
  ) => {
    const res = await apiClient.register({ email, password, fullName, targetRole, yearsOfExperience });
    handleAuthSuccess(res.data);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('aicopilot_token');
    localStorage.removeItem('aicopilot_user');
  };

  const dismissSessionMessage = () => {
    setSessionMessage(null);
    localStorage.removeItem('aicopilot_session_message');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        isLoading,
        sessionMessage,
        login,
        demoLogin,
        register,
        logout,
        dismissSessionMessage,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
