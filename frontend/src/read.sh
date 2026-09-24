#!/usr/bin/env bash

# Рекурсивный обход текущей директории
find . \
  \( -path "./.idea" -o -path "./.git" -o -path "./target" -o -name "*.css" \) -prune \
  -o -type f -print0 | while IFS= read -r -d '' file; do

    # Проверка: текстовый ли файл
    if file "$file" | grep -q text; then
        echo "===== FILE: $file ====="
        cat "$file"
        echo -e "\n"
    else
        echo "----- SKIPPED (binary): $file -----"
    fi
done