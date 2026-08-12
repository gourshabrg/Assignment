# Member Data Management System

An integrated data processing and cleaning utility designed to validate raw profile structures, handle structural syntax verification safely via custom exceptions, and distribute seamlessly as a standardized Python Wheel archive.

---

## Project Structure
Ensure your root project directory matches the structural map layout below:
```text
member_processor/
├── images/
│   └── image.png            # Deployment & execution screenshot
├── my_processor/
│   ├── __init__.py          # Packages directory and exposes entry points
│   ├── core.py              # Core execution, looping, and OOP blueprints
│   └── utils.py             # Validation routines, exceptions, and regex engine
├── pyproject.toml           # Modern PEP-517/518 build layout configuration
└── README.md                # System documentation
```

---

##  Step-by-Step Execution Guide

Follow these sequential terminal commands to initialize your environment, process the raw data vectors, compile your binary asset, and test deployment.

### 1. Environment Initialization
Open your terminal window inside your root project folder (`member_processor/`) and activate your isolated run space:
```bash
# Generate the virtual environment
python -m venv venv

# Bypass PowerShell script restrictions (Run this first on Windows)
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process

# Activate the environment on Windows
venv\Scripts\activate

# Activate the environment on macOS/Linux
source venv/bin/activate
```
*Your terminal line should now display the `(venv)` prefix.*

### 2. Run Local Code Logic
Before compiling, test your core functional code routine directly:
```bash
python my_processor/core.py
```

### 3. Compile the Distribution Wheel
Install your modern deployment requirements and compile your system code into a standard binary `.whl` bundle:
```bash
# Install tool requirements
pip install build setuptools wheel

# Execute the project builder
python -m build
```
*This action generates a distribution payload called `data_processor_task-1.0.0-py3-none-any.whl` inside a new `dist/` directory.*

### 4. Deploy and Package Verification
Simulate an end-user installing your newly authored library onto their operating system via `pip`:
```bash
# Install from local binary payload
pip install dist/data_processor_task-1.0.0-py3-none-any.whl

# Test internal exposure from outside the local script paths
python -c "from my_processor import Member; m = Member('Alice Smith', 'alice@example.com', '555-4321'); print(m)"
```

---

## Installation & Verification Proof
Below is the execution console screenshot validating proper script handling, structural exception trapping, compilation, and absolute deployment functionality:

### Installation Verification
![Package Verification And Test Screenshot](images\built_test.png)

### Validation Verification
![Test Verification Screenshot](images\test.png)

---

## Technical Code Architecture Explanation

This utility implements multiple core software patterns across its structure to ensure high data integrity:

### 1. Robust RegEx Validation Engine (`my_processor/utils.py`)
To isolate corrupted input entries safely before instantiating objects, data flows through highly restrictive Regular Expression pattern matchers inside our helpers module:
* **Email Validation (`^[\w\.-]+@[\w\.-]+\.\w+$`):** Restricts profiles to standard formats. Checks for string initialization (`^`), alphabetic/numeric components, an isolated literal `@` character, a secondary string layer, a hardcoded structural dot (`\.`), and an ending global top-level domain extension sequence (`$`).
* **Phone Validation (`^\d{3}-\d{4}$|^\d{7}$`):** Evaluates profile entries using a logical alternative `OR` operator (`|`). It perfectly allows standard hyphenated inputs matching `555-0101` OR solid blocks matching `5550102`.

### 2. Targeted Exception Boundary Handling
A custom error wrapper (`InvalidMemberDataError`) acts as an intentional structural firewall. Rather than relying on generic native system alerts, it catches semantic data corruption bugs specifically. 
If an incoming data item displays broken formatting or structural omissions:
1. The execution routine triggers an explicit `raise InvalidMemberDataError(...)`.
2. The runtime instantly drops execution of that broken line, bypasses object initialization, skips down to the targeted `except InvalidMemberDataError` safety catch boundary, outputs the custom validation failure log warning, and moves directly to processing the next data payload item safely without crashing the overall app runtime.

### 3. Object-Oriented Interface & Storage (`my_processor/core.py`)
* **`Member` Blueprint Class:** Standardizes successful inputs into unified object instances. Data undergoes final string normalization via `.strip()` inside the `__init__` constructor using clear instance field scopes (`self.name`, `self.email`, `self.phone`).
* **Storage Arrays:** Employs native `dictionaries` to temporarily buffer raw string structures and shifts records into a central dynamic `list` to easily hold initialized Python class object instances.

### 4. Functional Lambda Pipelines
Leverages advanced declarative programming methodologies. It implements a dedicated filtering utility using an inline anonymous lambda function passing a clear parameter expression block (`lambda m: domain in m.email`) straight into the highly-optimized built-in `filter()` interface. This extracts isolated data cohorts dynamically without writing tedious boilerplate control loops.



