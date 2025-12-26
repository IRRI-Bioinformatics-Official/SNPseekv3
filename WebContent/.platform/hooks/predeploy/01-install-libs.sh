#!/bin/bash
set -e

# Detect which app directory exists
if [ -d "/var/app/staging/WEB-INF/hdf5_lib" ]; then
  APP_DIR="/var/app/staging"
elif [ -d "/var/app/current/WEB-INF/hdf5_lib" ]; then
  APP_DIR="/var/app/current"
else
  echo "Error: WEB-INF/hdf5_lib not found in current or staging directories."
  exit 1
fi

echo "Using application directory: $APP_DIR"

# Copy the libraries
cp -v "$APP_DIR/WEB-INF/hdf5_lib/"*.* /usr/share/tomcat9/lib/
