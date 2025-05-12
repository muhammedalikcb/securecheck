#!/bin/bash
PACKAGE_NAME="$1"
KEY_ALIAS="secure_check_key"

SHA256=$(keytool -list -printcert -jarfile app-release.apk | grep SHA256 | awk '{print $2}' | tr -d ':')
ENCRYPTED=$(echo -n "$SHA256" | openssl enc -aes-256-cbc -a -k "$KEY_ALIAS")
echo "ENCRYPTED_SIGNATURE_HASH=\"$ENCRYPTED\""