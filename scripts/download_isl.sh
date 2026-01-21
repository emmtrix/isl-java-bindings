#!/usr/bin/env bash
set -euo pipefail

url="${1:-${ISL_URL:-}}"
output_dir="${2:-${ISL_OUTPUT_DIR:-downloads}}"

if [[ -z "$url" ]]; then
  echo "Usage: $0 <isl-url> [output-dir]" >&2
  echo "Or set ISL_URL and optional ISL_OUTPUT_DIR." >&2
  exit 1
fi

mkdir -p "$output_dir"

filename="${url##*/}"
output_path="$output_dir/$filename"

if command -v curl >/dev/null 2>&1; then
  curl -fL "$url" -o "$output_path"
elif command -v wget >/dev/null 2>&1; then
  wget -O "$output_path" "$url"
else
  echo "Error: curl or wget is required to download ISL." >&2
  exit 1
fi

echo "Downloaded ISL to $output_path"
