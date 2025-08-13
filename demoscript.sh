#!/bin/bash
# run_interview.sh - Terminal script to interact with the API

echo "🎯 AI Interview Prep Assistant - Terminal Client"
echo "================================================"

API_BASE="http://localhost:8080/api/interview"

# Function to start interview
start_interview() {
    echo "Starting interview..."
    read -p "Enter job title (e.g., Backend Engineer): " JOB_TITLE
    read -p "Enter topic (e.g., System Design): " TOPIC

    RESPONSE=$(curl -s -X POST "$API_BASE/start" \
        -H "Content-Type: application/json" \
        -d "{\"jobTitle\":\"$JOB_TITLE\",\"topic\":\"$TOPIC\"}")

    SESSION_ID=$(echo "$RESPONSE" | jq -r '.sessionId')
    QUESTION=$(echo "$RESPONSE" | jq -r '.currentQuestion')

    echo "✅ Interview started!"
    echo "📋 Session ID: $SESSION_ID"
    echo "❓ First Question: $QUESTION"
    echo ""

    # Start response loop
    response_loop $SESSION_ID
}

# Function to handle responses
response_loop() {
    local SESSION_ID=$1

    while true; do
        echo "💭 Your response (type 'quit' to end, 'status' to check session):"
        read -r USER_RESPONSE

        if [ "$USER_RESPONSE" = "quit" ]; then
            end_interview $SESSION_ID
            break
        elif [ "$USER_RESPONSE" = "status" ]; then
            check_session $SESSION_ID
            continue
        fi

        echo "📤 Submitting response..."
        curl -s -X POST "$API_BASE/respond" \
            -H "Content-Type: application/json" \
            -d "{\"sessionId\":\"$SESSION_ID\",\"response\":\"$USER_RESPONSE\"}" > /dev/null

        echo "⏳ Processing... (checking for next question in 3 seconds)"
        sleep 3

        # Get updated session
        check_session $SESSION_ID
        echo ""
    done
}

# Function to check session status
check_session() {
    local SESSION_ID=$1
    echo "🔍 Checking session status..."

    RESPONSE=$(curl -s "$API_BASE/session/$SESSION_ID")
    QUESTION=$(echo "$RESPONSE" | jq -r '.currentQuestion')
    ACTIVE=$(echo "$RESPONSE" | jq -r '.active')
    COUNT=$(echo "$RESPONSE" | jq -r '.questionCount')

    if [ "$ACTIVE" = "true" ]; then
        echo "✅ Session is active"
        echo "📊 Questions asked: $COUNT"
        if [ ! -z "$QUESTION" ] && [ "$QUESTION" != "null" ]; then
            echo "❓ Current Question: $QUESTION"
        fi
    else
        echo "❌ Session is not active"
    fi
}

# Function to end interview
end_interview() {
    local SESSION_ID=$1
    echo "🏁 Ending interview..."

    curl -s -X POST "$API_BASE/end/$SESSION_ID" > /dev/null
    echo "✅ Interview ended successfully!"
}

# Main menu
main_menu() {
    while true; do
        echo ""
        echo "Choose an option:"
        echo "1) Start new interview"
        echo "2) Check existing session"
        echo "3) Exit"
        read -p "Enter choice (1-3): " CHOICE

        case $CHOICE in
            1) start_interview ;;
            2)
                read -p "Enter session ID: " SESSION_ID
                check_session $SESSION_ID
                ;;
            3)
                echo "👋 Goodbye!"
                exit 0
                ;;
            *) echo "❌ Invalid choice" ;;
        esac
    done
}

# Check if server is running
echo "🔍 Checking if server is running..."
if curl -s "$API_BASE/../actuator/health" > /dev/null 2>&1; then
    echo "✅ Server is running!"
    main_menu
else
    echo "❌ Server is not running. Please start the application first:"
    echo "   mvn spring-boot:run"
    exit 1
fi