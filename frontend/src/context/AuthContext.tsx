import React, { createContext, useContext, useState, useEffect } from 'react';
import { User, AuthResponse } from '../types';
import { apiClient } from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  demoLogin: () => Promise<void>;
  register: (email: string, password: string, fullName: string, targetRole?: string, yearsOfExperience?: number) => Promise<void>;
  logout: () => void;
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

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(readStoredUser);
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('aicopilot_token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    if (token) {
      apiClient.getMe()
        .then((res) => {
          setUser(res.data);
          localStorage.setItem('aicopilot_user', JSON.stringify(res.data));
        })
        .catch(() => {
          // Token expired or invalid
          logout();
        })
        .finally(() => setIsLoading(false));
    } else {
      setIsLoading(false);
    }
  }, [token]);

  const handleAuthSuccess = (data: AuthResponse) => {
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

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        isLoading,
        login,
        demoLogin,
        register,
        logout,
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
