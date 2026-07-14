const ALLOWED_EMAIL_DOMAINS = [
  "gmail.com",
  "hotmail.com",
  "outlook.com",
  "yahoo.com"
];

// Name: letters only, single spaces between words, no leading/trailing space.
export const nameRules = {
  required: "Full name is required.",
  minLength: { value: 2, message: "Name must be at least 2 characters." },
  maxLength: { value: 100, message: "Name must be at most 100 characters." },
  pattern: {
    value: /^[A-Za-z]+(?: [A-Za-z]+)*$/,
    message: "Name must contain only letters, with no leading or trailing spaces."
  }
};

export const emailRules = {
  required: "Email is required.",
  validate: (value) => {
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
      return "Enter a valid email address.";
    }

    const domain = value.split("@")[1].toLowerCase();

    if (!ALLOWED_EMAIL_DOMAINS.includes(domain)) {
      return "Email must be a gmail, hotmail, outlook, or yahoo address.";
    }

    return true;
  }
};

export const phoneRules = {
  required: "Phone number is required.",
  pattern: {
    value: /^[6-9]\d{9}$/,
    message: "Phone must be 10 digits and start with 6-9."
  }
};

export const passwordRules = {
  required: "Password is required.",
  pattern: {
    value: /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,12}$/,
    message:
      "Password must be 8-12 characters with an uppercase, lowercase, digit, and special character (@$!%*?&)."
  }
};

export const dobRules = {
  required: "Date of birth is required.",
  validate: (value) => {
    const dob = new Date(value);
    const today = new Date();

    if (dob > today) {
      return "Date of birth cannot be in the future.";
    }

    const minAgeDate = new Date(
      today.getFullYear() - 18,
      today.getMonth(),
      today.getDate()
    );

    if (dob > minAgeDate) {
      return "You must be at least 18 years old.";
    }

    return true;
  }
};

export const qualificationRules = {
  required: "Qualification is required.",
  minLength: { value: 2, message: "Qualification must be at least 2 characters." }
};

export const experienceRules = {
  required: "Experience is required.",
  min: { value: 0, message: "Experience cannot be negative." }
};

export const feeRules = {
  required: "Consultation fee is required.",
  min: { value: 1, message: "Consultation fee must be greater than 0." }
};

export const requiredRule = (label) => ({
  required: `${label} is required.`
});
