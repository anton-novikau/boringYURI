#!/bin/bash

# Copyright 2022 Anton Novikau
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

MAIN_BRANCH="master"
VERSION_PATTERN="[0-9]+\.[0-9]+\.[0-9]+"

fail() {
  echo "$1" >&2 && return 1
}

version_to_number() {
  printf "%03d%03d%03d" $(echo "$1" | tr '.' ' ')
}

validate_version() {
  local version="$(echo "$1" | sed -rn "s/^\[($VERSION_PATTERN)\].*$/\1/p")"
  [ -z "$version" ] && fail "The title of a pull request to '$MAIN_BRANCH' must start with a version name in brackets."

  git fetch --tags 1>/dev/null 2>&1

  local latest_version="$(git tag | grep -E "$VERSION_PATTERN" | sort -V | tail -1)"

  if [ "$(version_to_number "$version")" -le "$(version_to_number "$latest_version")" ]; then
    fail "Releasing version must be bigger than the latest released version '$latest_version'"
  fi

  local changelog="$(cat CHANGELOG.md | grep -E "^## Boring YURI $version \([0-9]{4}-[0-9]{2}-[0-9]{2}\)$")"
  [ -z "$changelog" ] && fail "'CHANGELOG.md' doesn't contain an entry for '$version'."

  echo "::set-output name=version::$version"
}

validate_version "$1"