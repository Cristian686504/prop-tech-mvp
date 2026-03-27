#!/bin/sh
set -e

# Create uploads directory if it doesn't exist
mkdir -p /app/uploads/properties

# Set proper permissions
chown -R appuser:appgroup /app/uploads

# Run application as appuser
exec su-exec appuser java $JAVA_OPTS -jar /app/proptech.jar
