# Releasing metadata-extractor to Maven Central

## Overview

When you create and publish a GitHub Release, the `release.yml` workflow automatically:

1. Builds the project with Maven
2. Generates source and Javadoc JARs
3. Signs all artifacts with GPG
4. Deploys to Maven Central via the Sonatype Central Portal (OSSRH Staging API)
5. Automatically releases the staging repository

Artifacts typically appear on Maven Central within 10–30 minutes after deployment.

## One-Time Setup

These steps only need to be done once (or when credentials expire/rotate).

### 1. Sonatype Central Portal Account

The `com.drewnoakes` namespace was migrated from OSSRH to the Central Portal.

1. Go to <https://central.sonatype.com/> and sign in
   - Use the same username/password you used for the old OSSRH
   - If you can't log in, contact central-support@sonatype.com to recover access
2. Verify your namespace at <https://central.sonatype.com/publishing/namespaces>
   — you should see `com.drewnoakes`
3. Go to <https://central.sonatype.com/usertoken>
4. Click **Generate User Token**, give it a name and expiration
5. **Save both values** (username token + password token) — they cannot be retrieved later

### 2. GPG Signing Key

Maven Central requires all artifacts to be GPG-signed.

#### Option A: Create a New GPG Key

```bash
# Generate a new key (choose RSA 4096-bit, use your email)
gpg --full-generate-key

# List keys to find your key ID
gpg --list-secret-keys --keyid-format long
# Output looks like:
#   sec   rsa4096/ABC123DEF456 2024-01-01 [SC]
# The key ID is the part after the slash (ABC123DEF456)

# Export the private key (ASCII armored)
gpg --armor --export-secret-keys ABC123DEF456 > private-key.asc

# Publish the public key to keyservers (required by Maven Central for verification)
gpg --keyserver keyserver.ubuntu.com --send-keys ABC123DEF456
gpg --keyserver keys.openpgp.org --send-keys ABC123DEF456
```

#### Option B: Use an Existing GPG Key

If you have a GPG key from previous releases:

```bash
# List your existing keys
gpg --list-secret-keys --keyid-format long

# Export it
gpg --armor --export-secret-keys YOUR_KEY_ID > private-key.asc
```

### 3. Configure GitHub Repository Secrets

Go to the repository on GitHub → **Settings** → **Secrets and variables** → **Actions**,
and add these four repository secrets:

| Secret Name       | Value                                                                                                                  |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------- |
| `OSSRH_USERNAME`  | Your Central Portal user token username (from step 1.5)                                                                |
| `OSSRH_TOKEN`     | Your Central Portal user token password (from step 1.5)                                                                |
| `GPG_PRIVATE_KEY` | The full contents of `private-key.asc` (including the `-----BEGIN PGP PRIVATE KEY BLOCK-----` header and footer lines) |
| `GPG_PASSPHRASE`  | The passphrase you chose when creating the GPG key                                                                     |

After adding the secrets, **delete the exported key file** from your local machine:

```bash
rm private-key.asc
```

## How to Release

1. **Create a GitHub Release**:

   - Go to <https://github.com/drewnoakes/metadata-extractor/releases/new>
   - **Tag**: `2.20.0` (create new tag on publish)
   - **Title**: `2.20.0`
   - Add release notes (or click "Generate release notes")
   - Click **Publish release**

2. The release workflow triggers automatically and uses the **tag name as the version**.
   No version needs to be committed in source code — the tag is the single source of truth.

3. Monitor progress in the **Actions** tab.

## Validation

### Watch the Workflow

1. Go to the **Actions** tab on GitHub
2. Click the running "Publish to Maven Central" workflow
3. The `Publish to Maven Central` step logs show:
   - Build and test output
   - GPG signing of each artifact
   - Upload to the Sonatype staging repository
   - Staging repository close and release

### Check Maven Central

Allow 10–30 minutes after a successful workflow run, then:

1. Visit <https://central.sonatype.com/artifact/com.drewnoakes/metadata-extractor>
   and verify the new version appears
2. Or check the raw repository:
   <https://repo1.maven.org/maven2/com/drewnoakes/metadata-extractor/>

### Test the Published Artifact

Create a temporary test project to verify the artifact resolves correctly.

Create a file called `pom.xml`:

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>test-metadata-extractor</artifactId>
    <version>1.0</version>
    <dependencies>
        <dependency>
            <groupId>com.drewnoakes</groupId>
            <artifactId>metadata-extractor</artifactId>
            <version>2.20.0</version>
        </dependency>
    </dependencies>
</project>
```

Then run:

```bash
mvn dependency:resolve
```

If it downloads the expected version, the release is live.

### First-Time Dry Run (Recommended)

Before your first real release, validate the pipeline using the manual workflow trigger:

1. Go to **Actions** → **Publish to Maven Central** → **Run workflow**
2. Leave the version field empty for a timestamped SNAPSHOT (e.g. `0.0.0-20260408120000-SNAPSHOT`),
   or enter a test version like `2.20.0-rc1`
3. SNAPSHOT versions go to the snapshots repository (not Maven Central releases),
   so this is safe to experiment with
4. Watch the logs to confirm build, signing, and upload all succeed

## Troubleshooting

### 401 Unauthorized during deploy

- Sonatype credentials are wrong or expired
- You must use a **Central Portal token**, not old OSSRH credentials
- Generate a new token at <https://central.sonatype.com/usertoken>
- Update the `OSSRH_USERNAME` and `OSSRH_TOKEN` GitHub secrets

### GPG signing failed

- Verify `GPG_PRIVATE_KEY` contains the complete armored key
  (including `-----BEGIN PGP PRIVATE KEY BLOCK-----` and `-----END PGP PRIVATE KEY BLOCK-----`)
- Verify `GPG_PASSPHRASE` is correct
- If signing still fails in CI, try upgrading `maven-gpg-plugin` from `1.6` to `3.2.7` in `pom.xml` —
  newer versions handle CI environments (non-interactive GPG agent) more reliably

### Staging rules failure

Sonatype enforces rules before releasing to Maven Central:

- All artifacts must have source and Javadoc JARs ✅ (already configured)
- All artifacts must be GPG-signed ✅ (configured via workflow)
- POM must include required metadata ✅ (name, description, URL, license, SCM, developers — all present)

If rules fail, check your deployments at <https://central.sonatype.com/publishing>
for specific error messages.

### Artifacts don't appear on Maven Central

- Allow up to 30 minutes for sync after a successful deploy
- Check <https://repo1.maven.org/maven2/com/drewnoakes/metadata-extractor/>
- Verify the workflow log shows the staging repository was released (not just closed)

## Reference

- [Central Portal](https://central.sonatype.com/)
- [OSSRH Staging API compatibility docs](https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/)
- [Token generation](https://central.sonatype.org/publish/generate-portal-token/)
- [Publishing requirements](https://central.sonatype.org/publish/requirements/)
