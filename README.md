# MCP-Server

# Weather Service MCP Server

A Spring Boot-based weather service that provides weather forecasts and alerts through Model Context Protocol (MCP) integration. This service currently uses the U.S. National Weather Service API to fetch weather data.

## Features

- Get detailed weather forecasts by latitude/longitude
- Retrieve weather alerts for US states
- MCP server integration for Cursor IDE
- Temperature, wind, and detailed forecast information
- Error handling for invalid locations

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Cursor IDE (for MCP integration)

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Ahmed-Naseer-01/MCP-Server.git
   cd Weather-server
   ```

2. **Set Java 21**
   ```bash
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
   ```

3. **Build the Project**
   ```bash
   ./mvnw clean package -DskipTests
   ```

4. **Configure MCP Server**
   Go to Cursor setting->MCP Tools->mcp.json(create this file)
   Create or update `~/.cursor/mcp.json`:
   ```json
   {
     "mcpServers": {
       "spring-ai-mcp-weather": {
         "command": "java",
         "args": [
           "-Dspring.ai.mcp.server.stdio=true",
           "-jar",
           "/path/to/your/Weather-server/target/Weather-server-0.0.1-SNAPSHOT.jar"
         ]
       }
     }
   }
   ```
   Replace `/path/to/your` with your actual project path.
## Limitations

- Currently only supports US locations through the National Weather Service API
- Alerts are only available for US states
- All temperatures are in Fahrenheit
