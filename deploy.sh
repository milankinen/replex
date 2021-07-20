#!/usr/bin/env bash
set -euo pipefail

rm -rf target
mkdir -p target
echo '#!/bin/bash
set -eux
mkdir -p /root/.gnupg && chmod 600 /root/.gnupg
cp -r /gnupg/* /root/.gnupg/
rm -f /root/.gnupg/S.*
rm -f /root/.gnupg/*.conf
export GPG_TTY=$(tty)

./lein clean
./lein deploy clojars
' > target/entrypoint.sh

mkdir -p .cache/lein
mkdir -p .cache/m2

docker run --rm -ti \
  -v $(cd $(dirname $0) && pwd):/build \
  -v $(cd $(dirname $0) && pwd)/.cache/m2:/root/.m2 \
  -v $(cd $(dirname $0) && pwd)/.cache/lein:/root/.lein \
  -v $HOME/.gnupg:/gnupg:ro \
  -w /build \
  openjdk:8-jdk \
  sh target/entrypoint.sh
