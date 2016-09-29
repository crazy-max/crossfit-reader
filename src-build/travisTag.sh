#!/bin/sh
echo "Start travisTag..."

version_gt() {
  test "$(echo "$@" | tr " " "\n" | sort -V | head -n 1)" != "$1";
}

echo "appVersion: $APP_VERSION"
echo "previousVersion: $PREVIOUS_VERSION"

doDeploy=0
if version_gt $APP_VERSION.0 $PREVIOUS_VERSION; then
  doDeploy=1
  git config --global user.email "builds@travis-ci.com"
  git config --global user.name "Travis CI"
  export GIT_TAG=v$APP_VERSION.$TRAVIS_BUILD_NUMBER
  git tag $GIT_TAG -a -m "Generated tag from TravisCI for $GIT_TAG"
  git push -q https://${GH_TOKEN}@github.com/crazy-max/crossfit-reader --tags
  ls -R
fi

echo "doDeploy: $doDeploy"
echo "export DO_DEPLOY=$doDeploy" >> ~/.bash_profile
