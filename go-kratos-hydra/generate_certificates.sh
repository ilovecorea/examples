#!/bin/bash

# Set the domain name for the certificate
DOMAIN=localhost

# Create a self-signed certificate
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes -subj "/CN=${DOMAIN}"

echo "Certificates generated:"
echo "  - key.pem"
echo "  - cert.pem"