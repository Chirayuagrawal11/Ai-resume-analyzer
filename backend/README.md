# AI Resume Analyzer

Spring Boot backend for an AI-powered resume analyzer and job matcher.

## Requirements
- Java 25
- Maven 3.9+
- PostgreSQL 14+
- An OpenAI-compatible API key

## 1. Create database

In pgAdmin create:

resume_analyzer

The application will create the tables automatically.

## 2. Configure environment variables

Set:

AI_API_KEY=your_key
AI_API_URL=https://api.openai.com/v1/chat/completions
AI_MODEL=gpt-4o-mini
JWT_SECRET=a-long-random-secret-key

Or edit application.properties for local testing.

## 3. Run

cd backend
mvn spring-boot:run

Server:
http://localhost:8080

## API

POST /api/auth/register
POST /api/auth/login

POST /api/resumes/upload
GET  /api/resumes
GET  /api/resumes/{id}/analysis

POST /api/jobs
GET  /api/jobs
POST /api/jobs/match/{resumeId}

For protected endpoints send:
Authorization: Bearer YOUR_JWT_TOKEN

Resume upload uses multipart/form-data with field:
file

## Notes

The backend extracts text from PDF using Apache PDFBox, sends the extracted text to an AI model, expects structured JSON, and stores the analysis in PostgreSQL.

Do not commit API keys to GitHub.
