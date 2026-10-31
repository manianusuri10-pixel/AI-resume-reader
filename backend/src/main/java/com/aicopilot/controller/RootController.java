package com.aicopilot.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> root() {
        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>AI Career Copilot - Backend API</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                        background: #090d16;
                        color: #f1f5f9;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        min-height: 100vh;
                        margin: 0;
                        padding: 20px;
                        box-sizing: border-box;
                    }
                    .card {
                        background: #0f172a;
                        border: 1px solid #1e293b;
                        border-radius: 16px;
                        padding: 36px;
                        max-width: 540px;
                        width: 100%;
                        box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
                        text-align: center;
                    }
                    .badge {
                        display: inline-block;
                        background: rgba(16, 185, 129, 0.15);
                        color: #10b981;
                        border: 1px solid rgba(16, 185, 129, 0.3);
                        border-radius: 9999px;
                        padding: 4px 14px;
                        font-size: 12px;
                        font-weight: 700;
                        margin-bottom: 16px;
                    }
                    h1 {
                        font-size: 24px;
                        margin: 0 0 10px 0;
                        color: #ffffff;
                    }
                    p {
                        color: #94a3b8;
                        font-size: 14px;
                        line-height: 1.6;
                        margin: 0 0 24px 0;
                    }
                    .btn {
                        display: inline-block;
                        background: #10b981;
                        color: #022c22;
                        font-weight: 700;
                        font-size: 14px;
                        padding: 12px 24px;
                        border-radius: 10px;
                        text-decoration: none;
                        transition: background 0.2s;
                    }
                    .btn:hover {
                        background: #34d399;
                    }
                    .links {
                        margin-top: 24px;
                        padding-top: 20px;
                        border-top: 1px solid #1e293b;
                        display: flex;
                        justify-content: center;
                        gap: 16px;
                        font-size: 12px;
                    }
                    .links a {
                        color: #64748b;
                        text-decoration: none;
                    }
                    .links a:hover {
                        color: #94a3b8;
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="badge">● Backend API Online</div>
                    <h1>AI Career Copilot API</h1>
                    <p>The Spring Boot REST backend is running on <strong>port 8080</strong>.<br>The frontend web application runs on <strong>port 5173</strong>.</p>
                    <a class="btn" href="http://localhost:5173">Open Web Application (port 5173) &rarr;</a>
                    <div class="links">
                        <a href="/api/rag/search" target="_blank">RAG API</a>
                        <a href="/h2-console" target="_blank">H2 Console</a>
                    </div>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok(html);
    }

    @GetMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> apiStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "AI Career Copilot API",
            "frontendUrl", "http://localhost:5173"
        ));
    }
}

