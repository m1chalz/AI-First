# TODO: Re-enable and Fix Checkstyle Rules

**Date**: 2025-12-16  
**Priority**: P2 (Code quality, not blocking)  
**Estimated Effort**: 2-3 hours

---

## Status

Checkstyle rules have been **temporarily disabled** to reduce noise during active development. These rules should be re-enabled and violations fixed in a dedicated cleanup task.

---

## Disabled Rules

### 1. ImportOrder (~100+ violations)
**Rule**: Imports should be ordered: Java stdlib → javax → third-party → project  
**Files affected**: Almost all `.java` files  
**Fix**: IntelliJ IDEA → Code → Optimize Imports (Ctrl+Alt+O)

**Example violation**:
```java
// Bad
import com.intive.aifirst.petspot.e2e.utils.TestConfig;
import org.openqa.selenium.WebDriver;
import java.time.Duration;

// Good
import java.time.Duration;

import org.openqa.selenium.WebDriver;

import com.intive.aifirst.petspot.e2e.utils.TestConfig;
```

---

### 2. LineLength (~30+ violations)
**Rule**: Lines should be ≤ 120 characters  
**Files affected**: Various (mostly long String literals and method calls)  
**Fix**: Break long lines, extract variables

**Example violation**:
```java
// Bad (129 chars)
System.out.println("SOFT ASSERT: Scrolling through entire list to check if '" + petName + "' is NOT visible - this should fail");

// Good
String message = String.format(
    "SOFT ASSERT: Scrolling through entire list to check if '%s' is NOT visible",
    petName
);
System.out.println(message);
```

---

### 3. OperatorWrap (~40+ violations)
**Rule**: Operators (`+`, `||`, `?`) should be on a new line (not at end of previous line)  
**Files affected**: Various  
**Fix**: Move operators to start of next line

**Example violation**:
```java
// Bad
String result = "First part " +
    "second part";

// Good
String result = "First part "
    + "second part";
```

---

### 4. ConstantName (~20+ violations)
**Rule**: `static final` fields should be `UPPER_CASE`  
**Files affected**: Manager and Helper classes  
**Fix**: Rename constants

**Example violation**:
```java
// Bad
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

// Good
private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
```

**Note**: This may require updating all usages (find & replace).

---

### 5. WhitespaceAround (~15+ violations)
**Rule**: Braces `{` `}` should have whitespace around them  
**Files affected**: Lambda expressions and try-catch blocks  
**Fix**: Add spaces

**Example violation**:
```java
// Bad
try {doSomething();} catch (Exception e) {}

// Good
try { doSomething(); } catch (Exception e) { }
```

---

## Other Violations (Lower Priority)

### HideUtilityClassConstructor (~10 violations)
- Utility classes (only static methods) should have private constructor
- Prevents instantiation of utility classes

### AvoidStarImport (~5 violations)
- `import java.util.*` → `import java.util.List;`
- More explicit, better readability

### UnusedImports (~5 violations)
- Remove unused imports
- IntelliJ: Code → Optimize Imports

### NeedBraces (~5 violations)
- Single-line `if` should use braces: `if (x) { return; }`

---

## How to Fix

### Step 1: Re-enable Rules
Edit `e2e-tests/java/checkstyle.xml` and uncomment disabled rules (one at a time):
```xml
<!-- Remove comment markers -->
<module name="ImportOrder">
  <property name="groups" value="/^java\./,javax,org,com"/>
  ...
</module>
```

### Step 2: Run Checkstyle
```bash
cd e2e-tests/java
mvn checkstyle:check
```

### Step 3: Fix Violations
Use IntelliJ IDEA auto-fix:
1. **ImportOrder**: Code → Optimize Imports (Ctrl+Alt+O) on each file
2. **LineLength**: Manual split (no auto-fix)
3. **OperatorWrap**: Code → Reformat Code (Ctrl+Alt+L)
4. **ConstantName**: Manual rename (Find & Replace)
5. **WhitespaceAround**: Code → Reformat Code (Ctrl+Alt+L)

### Step 4: Commit
```bash
git add .
git commit -m "fix: Checkstyle violations - import order, line length, etc."
```

---

## Why This Matters

- **Code consistency**: Easier to read and maintain
- **Team collaboration**: Same style across all developers
- **Best practices**: Follows Java conventions (JLS, Google Style Guide)
- **CI/CD**: Can be enforced in build pipeline

---

## When to Do This

**Recommended timing**:
- ✅ After major feature development is complete
- ✅ During code cleanup sprint
- ✅ Before releasing to production
- ❌ NOT during active feature development (too disruptive)

**Estimated effort**: 2-3 hours for all ~300 violations

---

## Files to Update

Run to get list of files with violations:
```bash
cd e2e-tests/java
mvn checkstyle:check | grep "WARN" | cut -d: -f1 | sort -u
```

**Most affected directories**:
- `src/test/java/com/intive/aifirst/petspot/e2e/utils/` (Manager/Helper classes)
- `src/test/java/com/intive/aifirst/petspot/e2e/steps/` (Step definitions)
- `src/test/java/com/intive/aifirst/petspot/e2e/pages/` (Page Objects)

---

## Related Files

- `/e2e-tests/java/checkstyle.xml` - Checkstyle configuration
- `/e2e-tests/java/pom.xml` - Maven Checkstyle plugin config
- `/e2e-tests/java/.editorconfig` - IDE formatting config (if exists)

---

## References

- [Checkstyle Documentation](https://checkstyle.sourceforge.io/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Java Language Specification](https://docs.oracle.com/javase/specs/)

