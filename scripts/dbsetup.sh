#!/bin/bash

# Abfrage der PostgreSQL-Verbindungsdetails
read -p "PostgreSQL IP: " db_ip
read -p "PostgreSQL Port: " db_port
read -p "PostgreSQL Admin Username: " db_admin_username
read -sp "PostgreSQL Admin Passwort: " db_admin_password
echo
read -p "Möchtest du eine Spielperiode beginnend beim heutigen Datum anlegen (y/n)? " create_spielperiode
read -p "Soll ein Administrator User angelegt werden (y/n)? " create_admin

# Variablen für Administrator User, falls gewünscht
if [[ $create_admin == "j" ]]; then
  read -p "Administrator Username: " admin_username
  read -sp "Administrator Passwort: " admin_password
  echo
  # Passwort hashen
  admin_password_hash=$(echo -n $admin_password | openssl dgst -sha256 | sed 's/^.* //')
fi

# Verbindung zur Datenbank herstellen
PGPASSWORD=$db_admin_password psql -h $db_ip -p $db_port -U $db_admin_username -c "\q"
if [ $? -ne 0 ]; then
  echo "Verbindung zur Datenbank fehlgeschlagen."
  exit 1
else
  echo "Erfolgreich mit der Datenbank verbunden."
fi

# SQL-Schema ausführen
psql -h $db_ip -p $db_port -U $db_admin_username -f ../datenbank/postgres/mnwgdbmodel.sql
if [ $? -eq 0 ]; then
  echo "SQL-Schema erfolgreich ausgeführt."
else
  echo "Fehler beim Ausführen des SQL-Schemas."
  exit 1
fi

# Testdaten und/oder Administrator einrichten
if [[ $create_spielperiode == "y" ]]; then
  # Spielperiode anlegen
  date_today=$(date +%Y-%m-%d)
  date_end=$(date -d "+14 days" +%Y-%m-%d)
  PGPASSWORD=$db_admin_password psql -h $db_ip -p $db_port -U $db_admin_username -c "INSERT INTO syp.spielperiode VALUES (1, '$date_today', '$date_end');"
  if [ $? -eq 0 ]; then
    echo "Spielperiode erfolgreich angelegt."
  else
    echo "Fehler beim Anlegen der Spielperiode."
  fi
fi

if [[ $create_admin == "y" ]]; then
  # Administrator anlegen
  PGPASSWORD=$db_admin_password psql -h $db_ip -p $db_port -U $db_admin_username -c "INSERT INTO syp.administrator VALUES (1, '$admin_username', '$admin_password_hash');"
  if [ $? -eq 0 ]; then
    echo "Administrator erfolgreich angelegt."
  else
    echo "Fehler beim Anlegen des Administrators."
  fi
fi

