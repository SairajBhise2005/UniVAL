# Faculty Scheduling System

A JavaFX application for managing faculty schedules at the university.

## Configuration

This application uses Supabase for backend services. To configure the application:

### Option 1: Environment Variables (Recommended)

Set the following environment variables:

```
SUPABASE_URL=your_supabase_url
SUPABASE_KEY=your_supabase_key
```

### Option 2: Configuration File

1. Copy the `config.properties.template` file to `config.properties`
2. Edit the `config.properties` file and add your Supabase credentials:

```
supabase.url=your_supabase_url
supabase.key=your_supabase_key
```

**Important:** Never commit the `config.properties` file to version control. It's already added to `.gitignore`.

## Building and Running

### Prerequisites

- Java 11 or higher
- Maven

### Build

```
mvn clean package
```

### Run

```
java -jar target/faculty-scheduling-system-1.0-SNAPSHOT.jar
```

## Security Notes

- The application stores sensitive credentials in environment variables or a local configuration file
- The configuration file is excluded from version control to prevent accidental exposure of credentials
- For production deployments, always use environment variables for sensitive information 