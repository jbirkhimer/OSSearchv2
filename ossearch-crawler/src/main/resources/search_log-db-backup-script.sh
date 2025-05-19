#!/bin/bash

# MySQL Backup and Delete Script for search_log table
#
# Purpose: This script backs up all records from the search_log table up to the last day
# of the previous month at 12:59pm, then deletes those backed-up records.
#
# Usage: ./search_log-db-backup-script.sh
#
# Cron setup (run on 1st of every month at 1:00 AM):
#   1. Edit crontab: crontab -e
#   2. Add this line: 0 1 1 * * /opt/ossearch-crawler/config/search_log-db-backup-script.sh >> /opt/ossearch-crawler/logs/search_log-db-backup_$(date +\%Y-\%m).log 2>&1
#
# Set variables
BACKUP_DIR="/opt/ossearch-crawler/search_log-db-backups"
DB_HOST="si-oss-proxy02.si.edu"
DB_PORT="9003"
DB_NAME="si_search_db_dmz"
DB_USER="your_database_user"
DB_PASS="your_database_password"
TABLE_NAME="search_log"
DATE=$(date +"%Y-%m-%d")

# Calculate the first day of current month at 00:00:00
CUTOFF_DATE=$(date +"%Y-%m-01 00:00:00")

# Create backup file name with timestamp
BACKUP_FILE="${BACKUP_DIR}/${TABLE_NAME}_backup_${DATE}.sql.gz"

# Backup records up to the cutoff date
echo "Backing up records from ${TABLE_NAME} up to ${CUTOFF_DATE}..."
mysqldump --host=${DB_HOST} --port=${DB_PORT} --user=${DB_USER} --password=${DB_PASS} ${DB_NAME} ${TABLE_NAME} \
  --no-create-info --skip-add-drop-table --complete-insert --where="created_date < '${CUTOFF_DATE}'" | gzip > ${BACKUP_FILE}

# Check if backup was successful
if [ $? -eq 0 ] && [ -s ${BACKUP_FILE} ]; then
  echo "Backup successful. Data saved to ${BACKUP_FILE}"

  # Delete the backed up records in batches
  echo "Deleting backed up records in batches..."

  # Set batch size
  BATCH_SIZE=100000

  # Delete in batches to reduce lock time
  while true; do
    DELETED_COUNT=$(mysql --host=${DB_HOST} --port=${DB_PORT} --user=${DB_USER} --password=${DB_PASS} ${DB_NAME} -N -e \
      "DELETE FROM ${TABLE_NAME} WHERE created_date < '${CUTOFF_DATE}' LIMIT ${BATCH_SIZE}; SELECT ROW_COUNT();")

    echo "Deleted ${DELETED_COUNT} records..."

    # If no more records were deleted, we're done
    if [ "$DELETED_COUNT" -eq "0" ]; then
      break
    fi

    # Small pause to reduce server load
    sleep 0.5
  done

  # Check if delete was successful
  if [ $? -eq 0 ]; then
    echo "Delete operation successful."
    echo "Summary:"
    echo "- Backup file: ${BACKUP_FILE}"
    echo "- Records deleted up to: ${CUTOFF_DATE}"
  else
    echo "Error: Failed to delete records."
  fi
else
  echo "Error: Backup failed. No records were deleted."
fi