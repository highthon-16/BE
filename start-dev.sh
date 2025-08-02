#!/bin/bash
# start-dev.sh - 개발 환경 시작 스크립트

echo "🚀 개발 환경 시작 중..."

# 환경 변수 설정
export DB_URL="jdbc:postgresql://localhost:5432/ittae"
export DB_USERNAME="ittae_user"
export DB_PASSWORD="ittae_password"
export JWT_SECRET="dev_jwt_secret_key_change_in_production"
export JWT_EXPIRATION="86400000"
export GEMINI_API_KEY="your_gemini_api_key_here"
export MCP_SERVER_URL="http://localhost:8001"

# PostgreSQL 시작 (Docker)
echo "📊 PostgreSQL 시작 중..."
docker run -d \
  --name ittae-postgres \
  -e POSTGRES_DB=ittae \
  -e POSTGRES_USER=ittae_user \
  -e POSTGRES_PASSWORD=ittae_password \
  -p 5432:5432 \
  postgres:15

echo "⏳ PostgreSQL 초기화 대기 중..."
sleep 10

# MCP 서버 시작
echo "🐍 MCP 서버 시작 중..."
cd /Users/kangsiwoo/PycharmProjects/ittae-MCP
python http_server.py &
MCP_PID=$!

echo "⏳ MCP 서버 초기화 대기 중..."
sleep 5

# Spring Boot 애플리케이션 시작
echo "🌱 Spring Boot 애플리케이션 시작 중..."
cd /Users/kangsiwoo/IdeaProjects/ittae-BE
./gradlew bootRun &
SPRING_PID=$!

echo "✅ 시스템 시작 완료!"
echo "📱 Backend API: http://localhost:8080"
echo "🐍 MCP Server: http://localhost:8001"
echo "📖 API 문서: http://localhost:8080/swagger-ui.html"
echo ""
echo "💬 채팅 테스트 예시:"
echo "curl -X POST http://localhost:8080/api/chat/message \\"
echo "  -H \"Authorization: Bearer YOUR_JWT_TOKEN\" \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"message\": \"내일 오전 10시에 팀 미팅 일정을 만들어줘\"}'"

# 종료 핸들러
trap "echo '🛑 시스템 종료 중...'; kill $MCP_PID $SPRING_PID; docker stop ittae-postgres; docker rm ittae-postgres; exit" INT TERM

wait
