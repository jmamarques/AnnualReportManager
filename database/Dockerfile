# Use the official PostgreSQL image from Docker Hub
FROM postgres:16

# Set environment variables for PostgreSQL superuser
ENV POSTGRES_USER=wallet
ENV POSTGRES_PASSWORD=secretWallet

# Copy the initialization script to the container's initialization directory
#COPY init.sql /docker-entrypoint-initdb.d/

# Expose the default PostgreSQL port
EXPOSE 5432