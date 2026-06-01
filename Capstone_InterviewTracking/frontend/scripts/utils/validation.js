export function validateEmail(email) {
  const v = (email || "").trim();
  if (!v) return "Email is required";
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v))
    return "Enter a valid email address";
  return null;
}

export function validatePassword(password) {
  const errors = [];
  if (!password || password.length < 6)
    errors.push("At least 6 characters required");
  if (!/[A-Z]/.test(password))
    errors.push("Must include an uppercase letter (A–Z)");
  if (!/[a-z]/.test(password))
    errors.push("Must include a lowercase letter (a–z)");
  if (!/[0-9]/.test(password)) errors.push("Must include a number (0–9)");
  if (!/[!@#$%^&*()\-_=+\[\]{};:'",.<>?/\\|`~]/.test(password))
    errors.push("Must include a special character (!@#$%^&* …)");
  return errors;
}

// ============================================================
// NAME  (letters, spaces, hyphens, apostrophes only; 2–60 chars)
// ============================================================
export function validateName(name) {
  const v = (name || "").trim();
  if (!v) return "Name is required";
  if (v.length < 2) return "Name must be at least 2 characters";
  if (v.length > 60) return "Name must be at most 60 characters";
  if (/[0-9]/.test(v)) return "Name must not contain numbers";
  if (!/^[a-zA-Z\s'\-]+$/.test(v))
    return "Name may only contain letters, spaces, hyphens, or apostrophes";
  return null;
}

export function validateDob(dob, minAge = 18) {
  if (!dob) return "Date of birth is required";
  const birth = new Date(dob);
  if (isNaN(birth.getTime())) return "Invalid date of birth";
  const today = new Date();
  if (birth >= today) return "Date of birth cannot be today or in the future";

  const age =
    today.getFullYear() -
    birth.getFullYear() -
    (today < new Date(today.getFullYear(), birth.getMonth(), birth.getDate()) ?
      1
    : 0);
  if (age < minAge) return `Candidate must be at least ${minAge} years old`;
  return null;
}

export function validatePhone(phone) {
  const v = (phone || "").trim();
  if (!v) return "Phone number is required";
  if (!/^\d{10}$/.test(v)) return "Phone number must be exactly 10 digits";
  return null;
}

export function validateSalaryRange(min, max) {
  const errors = [];
  const lo = parseFloat(min);
  const hi = parseFloat(max);
  const hasMin = min !== "" && min !== null && min !== undefined;
  const hasMax = max !== "" && max !== null && max !== undefined;

  if (hasMin && (isNaN(lo) || lo < 0))
    errors.push("Minimum salary must be a non-negative number");
  if (hasMax && (isNaN(hi) || hi < 0))
    errors.push("Maximum salary must be a non-negative number");
  if (hasMin && hasMax && !isNaN(lo) && !isNaN(hi) && lo > hi)
    errors.push("Minimum salary cannot exceed maximum salary");
  return errors;
}

export function validateExperienceRange(min, max) {
  const errors = [];
  const lo = parseFloat(min);
  const hi = parseFloat(max);
  const hasMin = min !== "" && min !== null && min !== undefined;
  const hasMax = max !== "" && max !== null && max !== undefined;

  if (hasMin) {
    if (isNaN(lo) || lo < 0)
      errors.push("Minimum experience must be a non-negative number");
    else if (lo > 50) errors.push("Minimum experience cannot exceed 50 years");
  }
  if (hasMax) {
    if (isNaN(hi) || hi < 0)
      errors.push("Maximum experience must be a non-negative number");
    else if (hi > 50) errors.push("Maximum experience cannot exceed 50 years");
  }
  if (hasMin && hasMax && !isNaN(lo) && !isNaN(hi) && lo > hi)
    errors.push("Minimum experience cannot exceed maximum experience");
  return errors;
}

export function validatePositiveNumber(value, label = "Value") {
  if (value === "" || value === null || value === undefined) return null;
  const n = parseFloat(value);
  if (isNaN(n) || n < 0) return `${label} must be a non-negative number`;
  return null;
}
