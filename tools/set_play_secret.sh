#!/bin/bash
set -e

if [ -z "$1" ]; then
    echo "Usage: ./tools/set_play_secret.sh <path-to-service-account.json>"
    exit 1
fi

if [ ! -f "$1" ]; then
    echo "Error: File '$1' not found."
    exit 1
fi

echo "Setting PLAY_SERVICE_ACCOUNT_JSON secret in GitHub repository zaryif/Minimal_os..."
gh secret set PLAY_SERVICE_ACCOUNT_JSON < "$1"
echo "Success: PLAY_SERVICE_ACCOUNT_JSON has been configured in GitHub repository secrets."
