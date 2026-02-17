#!/bin/bash

# 1. Check if an argument was provided
if [ -z "$1" ]; then
    echo "Usage: $0 <commit-hash>"
    exit 1
fi

COMMIT_HASH=$1
PATCH_FILE="${COMMIT_HASH}.patch"

# 2. Validate if the argument is a valid git hash
if ! git rev-parse --verify "$COMMIT_HASH" >/dev/null 2>&1; then
    echo "Error: '$COMMIT_HASH' is not a valid git hash or is not reachable."
    exit 1
fi

echo "Processing commit: $COMMIT_HASH..."

# 3. Generate the patch to stdout and pipe to file
# We use --stdout and >| (clobber) as per your example
git format-patch -1 "$COMMIT_HASH" --stdout >| "$PATCH_FILE"

# 4. Dry Run: Check if the patch can be applied before actually doing it
echo "Testing patch application..."
if ! git apply --check -p3 --directory=src/main/java "$PATCH_FILE" >/dev/null 2>&1; then
    echo "Error: Patch -p3 application failed dry-run (path mismatch or conflicts)."
    rm "$PATCH_FILE"
    exit 1
fi

# 5. Apply the patch using git am
if git am -p3 --directory=src/main/java < "$PATCH_FILE"; then
    echo "Successfully applied patch $COMMIT_HASH"
    rm "$PATCH_FILE"
else
    echo "Error: git am failed. You may need to run 'git am --abort'."
    exit 1
fi
