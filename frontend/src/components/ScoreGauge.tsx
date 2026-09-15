import React from 'react';

interface ScoreGaugeProps {
  score: number;
  maxScore?: number;
  size?: number;
  strokeWidth?: number;
  label?: string;
}

export const ScoreGauge: React.FC<ScoreGaugeProps> = ({
  score,
  maxScore = 100,
  size = 120,
  strokeWidth = 10,
  label = 'Score',
}) => {
  const radius = (size - strokeWidth) / 2;
  const circumference = radius * 2 * Math.PI;
  const normalizedScore = Math.min(maxScore, Math.max(0, score));
  const percentage = (normalizedScore / maxScore) * 100;
  const strokeDashoffset = circumference - (percentage / 100) * circumference;

  const getColor = (pct: number) => {
    if (pct >= 80) return '#22c55e'; // Green
    if (pct >= 65) return '#3b82f6'; // Blue
    if (pct >= 50) return '#f59e0b'; // Amber
    return '#ef4444'; // Red
  };

  const color = getColor(percentage);

  return (
    <div className="flex flex-col items-center justify-center">
      <div className="relative" style={{ width: size, height: size }}>
        <svg width={size} height={size} className="transform -rotate-90">
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="#1e293b"
            strokeWidth={strokeWidth}
            fill="transparent"
          />
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke={color}
            strokeWidth={strokeWidth}
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            strokeLinecap="round"
            fill="transparent"
            className="transition-all duration-1000 ease-out"
          />
        </svg>
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-2xl font-bold tracking-tight text-white">
            {Math.round(normalizedScore)}
            {maxScore === 100 ? '%' : ''}
          </span>
          <span className="text-[10px] font-medium uppercase tracking-wider text-slate-400">
            {label}
          </span>
        </div>
      </div>
    </div>
  );
};
