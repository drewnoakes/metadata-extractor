echo 'Determining version number for publication'
echo 'Looking for an existing release tag against this commit'

VERSION=$(git describe --tags --match release/* --exact-match 2>&1)
if [ $? -ne 0 ]
then
  LAST=$(git describe --tags --match release/* 2>&1)
  if [ $? -ne 0 ]
  then
    echo 'No release tags found at all; bail out'
    exit 1
  fi

  echo "No matching tag found. Push a tag like release/1.0.1 against HEAD in order to release.  Most recent tag is: ${LAST}"
  exit 0
fi

VERSION=$(echo $VERSION | sed 's#release/##g')
echo "Publishing version: ${VERSION}"

status=$(curl -s --head -w %{http_code} -o /dev/null https://repo1.maven.org/maven2/com/github/dalet-oss/arangobee/${VERSION}/)
if [ $status -eq 200 ]
then
  echo 'Version already available on Maven Central.  This must be a rebuild; nothing to do here.'
else
  echo 'Version not already available on Maven Central'

  # Decrypt the gpg key used for signing
  echo 'Decrypting the GPG key used for signing'
  openssl aes-256-cbc -K ${SONATYPE_GPGKEY_FILE_ENCRYPTION_KEY} -iv ${SONATYPE_GPGKEY_FILE_ENCRYPTION_IV} -in secret.gpg.enc -out secret.gpg -d
  export GPG_TTY=$(tty)
  if [ ! -f secret.gpg ]
  then
    echo 'Decryption failed; bail out'
    exit 1
  fi
  echo 'Decryption successful'

  # Work around some nonsense on the specific version of GPG that comes with Ubuntu - see https://www.fluidkeys.com/tweak-gpg-2.1.11/
  echo 'allow-loopback-pinentry' >> ~/.gnupg/gpg-agent.conf
  gpgconf --reload gpg-agent

  # Add the key into gpg; then sign something random to get the key into the gpg-agent
  echo ${SONATYPE_GPGKEY_PASSPHRASE} | gpg2 --passphrase-fd 0 --batch --yes --import secret.gpg
  touch /tmp/foo.txt
  gpg2 --pinentry-mode=loopback --passphrase ${SONATYPE_GPGKEY_PASSPHRASE} --sign /tmp/foo.txt

  # Build, sign and publish the artifacts
  mvn -Prelease deploy -DskipTests -Drevision=${VERSION} -Dgpg.executable=gpg2 -Dgpgkey.passphrase=${SONATYPE_GPGKEY_PASSPHRASE}
fi
