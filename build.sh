#!/bin/bash
# Kingdom Tycoon - CI/CD Build Script
# This script automates the build process for fresh developers

set -e  # Exit on error

echo "========================================="
echo "Kingdom Tycoon - Automated Build Script"
echo "========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}→ $1${NC}"
}

# Step 1: Check prerequisites
echo "Step 1: Checking prerequisites..."
print_info "Checking Python installation..."
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version)
    print_success "Python found: $PYTHON_VERSION"
else
    print_error "Python 3 not found. Please install Python 3.8+"
    exit 1
fi

print_info "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    print_success "Java found: $JAVA_VERSION"
else
    print_error "Java not found. Please install JDK 17"
    exit 1
fi

print_info "Checking Gradle wrapper..."
if [ -f "./gradlew" ]; then
    print_success "Gradle wrapper found"
else
    print_error "Gradle wrapper not found"
    exit 1
fi

echo ""

# Step 2: Install Python dependencies
echo "Step 2: Installing Python dependencies..."
print_info "Installing Pillow for asset generation..."
python3 -m pip install --quiet pillow || {
    print_error "Failed to install Pillow"
    exit 1
}
print_success "Python dependencies installed"
echo ""

# Step 3: Generate placeholder assets
echo "Step 3: Generating placeholder assets..."
if [ -f "generate_placeholder_assets.py" ]; then
    print_info "Running asset generation script..."
    python3 generate_placeholder_assets.py || {
        print_error "Asset generation failed"
        exit 1
    }
    print_success "Placeholder assets generated"
else
    print_error "generate_placeholder_assets.py not found"
    exit 1
fi
echo ""

# Step 4: Clean previous builds
echo "Step 4: Cleaning previous builds..."
print_info "Running Gradle clean..."
./gradlew clean --quiet || {
    print_error "Gradle clean failed"
    exit 1
}
print_success "Build cleaned"
echo ""

# Step 5: Run unit tests
echo "Step 5: Running unit tests..."
print_info "Running test suite..."

# Run specific tests
TEST_CLASSES=(
    "com.ismail.kingdom.SaveSystemStressTest"
    "com.ismail.kingdom.BalanceSimulator"
    "com.ismail.kingdom.AdQAChecklist"
    "com.ismail.kingdom.ReleaseChecklist"
)

TEST_FAILED=0
for TEST_CLASS in "${TEST_CLASSES[@]}"; do
    print_info "Running $TEST_CLASS..."
    if ./gradlew test --tests "$TEST_CLASS" --quiet; then
        print_success "$TEST_CLASS passed"
    else
        print_error "$TEST_CLASS failed"
        TEST_FAILED=1
    fi
done

if [ $TEST_FAILED -eq 1 ]; then
    print_error "Some tests failed. Check test reports in build/reports/tests/"
    exit 1
fi

print_success "All tests passed"
echo ""

# Step 6: Build debug APK
echo "Step 6: Building debug APK..."
print_info "Running assembleDebug..."
./gradlew android:assembleDebug || {
    print_error "Debug build failed"
    exit 1
}
print_success "Debug APK built successfully"
echo ""

# Step 7: Verify APK
echo "Step 7: Verifying APK..."
APK_PATH="android/build/outputs/apk/debug/android-debug.apk"
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    print_success "APK found: $APK_PATH ($APK_SIZE)"
else
    print_error "APK not found at expected location"
    exit 1
fi
echo ""

# Step 8: Run lint checks (optional)
echo "Step 8: Running lint checks..."
print_info "Running Android lint..."
./gradlew android:lint --quiet || {
    print_error "Lint checks failed (non-critical)"
}
print_success "Lint checks completed"
echo ""

# Summary
echo "========================================="
echo "Build Summary"
echo "========================================="
print_success "Prerequisites: OK"
print_success "Assets generated: OK"
print_success "Tests passed: OK"
print_success "Debug APK built: OK"
echo ""
echo "APK Location: $APK_PATH"
echo "APK Size: $APK_SIZE"
echo ""
echo "Next steps:"
echo "  1. Install APK: adb install $APK_PATH"
echo "  2. Run on device: ./gradlew android:run"
echo "  3. View test reports: open build/reports/tests/test/index.html"
echo ""
print_success "Build completed successfully!"
echo "========================================="
